# Kotlin Spring Demo

## Demo

A ready-to-run docker compose setup can be found in [deploy](./deploy).

An equivalent setup that instead of pulling the precompiled image builds the [Dockerfile](./Dockerfile) locally exists
in [deploy-build](./deploy-build).

---

## Local Development

For local development, start a postgres database on port 5432. For that, you can run the
provided [compose.yml](./compose.yml).

IntelliJ should automatically pick up the provided [run configuration](./.run/SpringDemoApplication.run.xml). If not,
add a basic spring boot run configuration with the following spring profiles: `dev,localhost`.

### Profiles

Profile `dev` ([props](./src/main/resources/application-dev.properties)) is used for populating the database with a demo
user:

```json
{
  "user": {
    "email": "demo@example.com"
  },
  "password": "password123"
}
```

It also configures the service to use the [jwk.json](./src/main/resources/jwk.json) included in the classpath, increases
logging, and enables all spring actuator endpoints.

---

Profile `localhost` ([props](./src/main/resources/application-localhost.properties)) configures the database connection
to use the local database. This profile can be omitted if the following environment variables are set (in accordance
with your running database):

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

tba.
