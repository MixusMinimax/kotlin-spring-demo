# Kotlin Spring Demo

## Demo

A ready-to-run docker compose setup can be found in [deploy](./deploy).

An equivalent setup that instead of pulling the precompiled image builds the [Dockerfile](./Dockerfile) locally exists
in [deploy-build](./deploy-build).

In IntelliJ, you can run the included http scenarios:

* [httpscenarios/authenticate.http](httpscenarios/authenticate.http)  
  We log in, then refresh our jwt using the refresh token (intellij saves cookies between requests), and then use that
  jwt for authentication. Lastly, we log out. Keep in mind that the jwt can still be used until it expires in 15min,
  that is the tradeoff of server-stateless authentication.
* [httpscenarios/jwt.http](httpscenarios/jwt.http)    
  This one tries to add a user to an organization. Without logging in, it will not work. Then it logs in, and tries
  again. You can also take a look at the parsed jwt.

---

## Local Development

For local development, start a postgres database on port 5432. For that, you can run the
provided [compose.yml](./compose.yml).

IntelliJ should automatically pick up the provided [run configuration](./.run/SpringDemoApplication.run.xml). If not,
add a basic spring boot run configuration with the following spring profiles: `dev,localhost,demo`.

### Profiles

* Profile `dev` ([props](src/main/resources/application-dev.properties)) should be enabled, as it sets the jwk path to
  the one in the [resources](src/main/resources) folder. It also enables all spring actuator endpoints.

* Profile `demo` is used for populating the database with a demo user:

  ```json
  {
    "user": {
      "email": "demo@example.com"
    },
    "password": "password123"
  }
  ```

  It also creates a demo organization with the `ADD_USER` permission added to the demo user.
  Read [DemoInit.kt](src/main/kotlin/com/barmetler/springdemo/DemoInit.kt).

* Profile `localhost` ([props](./src/main/resources/application-localhost.properties)) configures the database
  connection to use the local database. This profile can be omitted if the following environment variables are set (in
  accordance with your running database):

  ```dotenv
  POSTGRES_PASSWORD=postgres
  POSTGRES_USER=postgres
  POSTGRES_DB=postgres
  POSTGRES_HOSTNAME=localhost
  ```

### Running Tests

I am using [kotest](https://kotest.io/) (with junit 5) for this project. Simply run all tests from the IDE as usual, for
example by right-clicking the test folder and choosing `Run 'Tests in 'spring-demo.test''`

---

## Project Overview

### TL;DR

- Vertical Slice Architecture
- Use-Cases and Hexagonal(-ish) Architecture
- JWT auth using ed25519
- Stateful refresh tokens that can be revoked
- Unit testing using kotest
- Integration testing using kotest and mockmvc
- ktlint
- detekt
- Dockerization (BuildKit, docker compose)
- CI on GitHub for testing, ktlint, detekt
- CI on GitHub for creating semver docker images and publishing to ghcr.io

### Technologies Used

- Kotlin 2.3.21
- JDK 25 (latest supported by kotlin)
- Spring Boot 4.0.6
- Hibernate 7
- Liquibase

For testing:

- kotest 6.1.11
- springmockk 5.0.1

For more, read the [version catalog](./gradle/libs.versions.toml) and [buildscript](build.gradle.kts).

### High-Level Structure

> _DISCLAIMER:_
>
> It is imperative to mention that this project does not serve as an opinion piece, statement of fact, or guide.
> Instead, it is meant to be viewed as an experiment of different software patterns, where no pattern is fully followed
> to a tee.
>
> Since this project is a demonstration, I will go somewhat in-depth into lessons learned. Most of this can be skipped
> in favor of the TL;DR.

#### Vertical Slice Architecture

For this project, I experimented with vertical slices.

In a typical layered architecture, all components of one layer are grouped together, and layers are separated from each
other. This results in a clear separation of concerns.

However, this grouping is not aligned with typical workflows and the control flow of the program. For instance,
consider the following:

If we are working on a REST server and are implementing a feature that allows CRUD operations on dogs and cats, then
the project structure would look like this:

```yml
model:
  - # many files ...
  - Cat
  - # many files ...
  - Dog
  - # many files ...
repository:
  - # many files ...
  - CatRepository
  - # many files ...
  - DogRepository
  - # many files ...
service:
  - # many files ...
  - CatService
  - # many files ...
  - DogService
  - # many files ...
controller:
  - # many files ...
  - CatController
  - # many files ...
  - DogController
  - # many files ...
```

Add to this DTOs, mappers, configuration, etc...

Now to ask the important question:

> Which set of files will a developer be working on at a time?

Is the answer...

> a: multiple models, but nothing else
>
> b: multiple services, but nothing else
>
> c: everything regarding cats, but nothing else
>
> d: everything regarding dogs, but nothing else

To me, option c and d sound much more likely than a or b.

Vertical Slice Architecture separates this into completely separate features, in our case `cat` and `dog`.

These features contain everything from the presentation layer, application layer, the domain, through to persistence.

We replace `concern.feature.class` with `feature.concern.class`.

Aforementioned example transforms into this:

```yml
# many modules ...
cat:
  model:
    - Cat
  repository:
    - CatRepository
  service:
    - CatService
  controller:
    - CatController
# many modules ...
dog:
  model:
    - Dog
  repository:
    - DogRepository
  service:
    - DogService
  controller:
    - DogController
# many modules ...
```

We did not give up the layered design, simply distributed it.

From a workflow perspective, the developer no longer has to scroll back and forth through a mile-long list of classes.
They can simply collapse all folders, and only open `cat` or `dog`. All relevant files are visible in the project tree,
with no clutter.

From a maintainability perspective, the same applies, the scope through which to search for a class has reduced. Git
merge requests for new features will often add a single top-level folder. It is less likely for different developers to
create conflicting changes, like files with the same name, or classes that sound like they belong to one thing, when in
reality they belong to another.

That being said, there are definitely general purpose classes that may be needed in all features, which can still lie
outside this structure.

However, a somewhat hybrid approach is also possible. There is no "right" solution, but one that proves to work well for
a given team.

#### CQRS-lite and Hexagonal Architecture

While I do not fully follow a request-mediator-handler approach, as is more popular in .NET applications, I chose to
experiment with a somewhat related approach:

Instead of having one large service per feature that includes dozens of public functions, and often gets to thousands of
lines in length, I created one handler per actual use case.

In a typical "service", we often have many functions that don't actually interact with each other. They are simply
grouped into a `DogService` by association.

This results in quite horrible merge conflicts, as a new function in a service adds class fields for dependency
injection, private helper functions, and imports.

We can take another vertical slice through this. In my code base, a `*UseCase.kt` file looks like a service, but has
exactly one public function:

```yml
dog:
  api:
    - DogController
  api.dto:
    - DogDTO
    - PetDogRequest
    - PetDogResponse
    - SearchDogsRequest
    - SearchDogsResponse
  model:
    - Dog
  persistence: # could be called repository
    - DogRepository
    - DogFilter
  services:
    - SomeUtilityThatIsUsedInManyPlacesService
  usecases:
    - PetDogUseCase
    - SearchDogsUseCase
```

This has many advantages:

1. Different developers that work on different functionality no longer edit the same file.
2. Git commits include mostly additions instead of line edits.
3. Individual functions can be unit tested. Only the dependencies that are actually required need to be mocked.
4. No file is thousands of lines long for no reason.
5. Use cases can be used the exact same way, no matter whether it is with rest controllers, remote procedure calls, a
   user interface, or test framework.
6. They can be used with any infrastructure regarding databases, email delivery, or external api calls.

This is because each use case requests only the functionality from its dependencies that it actually needs, whereas a
conventional service has to request all functionalities that _any_ of its functions needs, combined.

It's not _really_ hexagonal architecture, but the same concept applies:

The application layer does not concern itself with rest, and it does not really concern itself with hibernate. That
second point is not fully separated though. Technically, domain entities should not be annotated with @Entity, and the
domain and application could actually lie in its own Gradle project that has no dependency on hibernate or even jpa.

For a smaller project, this hybrid approach works quite well, as it does not have to deal with all the boilerplate code
introduced by another mapping layer. One could pretend that JPA annotations and hibernate proxies are not directly
visible to the application source code, so from a code quality standpoint, it does the job of abstracting that
implementation detail away.
