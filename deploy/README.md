## Compose Setup for Local Building

This is a ready-to-run demo docker compose file for running the compiled docker images directly
from [ghcr.io](https://ghcr.io).

Just run

```shell
docker compose up -d
```

or run it from the gutter icons in the IDE ([compose.yml](./compose.yml)).

It also includes a random JWK file to showcase a production use case where you may not want to include the private key
included in the classpath. Specifically for use with docker/kubernetes secrets this can be useful.

The [compose.yml](./compose.yml) Contains two services:

- [postgres:latest](https://hub.docker.com/layers/library/postgres/latest/images/sha256-692e5dca0962d91131c4ad681f50fb2b79dffb3ef8d766aef2c8cf671fea4034)
    - exposes port `5433`[^1] to the host for external access.
- [ghcr.io/mixusminimax/kotlin-spring-demo:0.0.1](https://github.com/mixusminimax/kotlin-spring-demo/pkgs/container/kotlin-spring-demo/848470495?tag=0.0.1)
    - listens on port 8080.

The relevant configuration to allow the server to connect to the database can be found in [.env](./.env).

[^1]: why not `5432`? Well, I was using that for the database that my local gradle project is using. Also, whoever is
trying this may already have a postgres instance running locally.

<!-- note: markdown footnotes are implemented in Intellij 2026.2, which comes out soon. -->
