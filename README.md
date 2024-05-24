# Mixer Gateway

A microservice which implements main entry point for Mixer REST API. This service is used as:
- Proxy to other microservices or resources
- Validation of request and response
- Security checking (roles and permissions)
- Caching and idempotency mechanism
- Monitoring and logging

For more information, visit
the [Confluence page.](https://musicmixer.atlassian.net/wiki/spaces/Architectu/pages/29884417/Mixer+Gateway)

## Prerequisites

The following tools and libraries are required for running this microservice:

- Java 21 LTS
- Docker with docker-compose

## Running

Mixer Gateway require various components for successfully starting the microservice (ex. Postgres database, Redis etc.).
All integration services are defined in `docker-compose` file. Run the following command from project root directory to
start all required services:

```
docker-compose up -d
```

To set up the database, please run Liquibase update command from the root directory of the project:

```
./mvnw liquibase:update
```

Start Mixer Gateway with provided Maven wrapper:

```
./mvnw spring-boot run
```

## Local testing

Testing of exposed endpoints can be done using [Bruno client](https://www.usebruno.com).

Open Bruno collection located in separated [GitHub repository](https://github.com/mixerfm/backend-api-definition), directory `mixer-gateway`.

Before sending a request make sure to fill all of required variables in `Vars` or `Body` tab.