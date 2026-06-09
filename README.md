# laa-data-claims-certificated-api
[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/laa-data-claims-certificated-api/badge)](https://github-community.service.justice.gov.uk/repository-standards/laa-data-claims-certificated-api)

## Overview

Template GitHub repository used for Spring Boot Java microservice projects.

The project uses the `laa-spring-boot-gradle-plugin` Gradle plugin which provides
sensible defaults for the following plugins:

- [Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
- [Dependency Management](https://plugins.gradle.org/plugin/io.spring.dependency-management)
- [Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [Java](https://docs.gradle.org/current/userguide/java_plugin.html)
- [Maven Publish](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Spring Boot](https://plugins.gradle.org/plugin/org.springframework.boot)
- [Test Logger](https://github.com/radarsh/gradle-test-logger-plugin)
- [Versions](https://github.com/ben-manes/gradle-versions-plugin)

The plugin is provided by -  [laa-spring-boot-common](https://github.com/ministryofjustice/laa-spring-boot-common), where you can find
more information regarding setup and usage.

### Project Structure
Includes the following subprojects:

- `laa-data-claims-certificated-api` - example OpenAPI specification used for generating API stub interfaces and documentation.
- `laa-data-claims-certificated-service` - example REST API service with CRUD operations interfacing a JPA repository with a PostgreSQL database.

## TODO: Update this README

Replace this section with clear documentation for your service. Include what it does, how to run it locally, environment variables, and any other details relevant to developers.

### Database scripts
The *.sql scripts in  `src/main/resources` have been included to provide an example database for demonstration purposes only and should be removed for your application.

## Build And Run Application

### Build application
`./gradlew clean build`

### Run integration tests
`./gradlew integrationTest`

### Run application
`./gradlew bootRun`

### Run application via Docker
`docker compose up`

### Debug application running via Docker

#### Configuration

* Go to Run > Edit Configurations
* Click + (Add New Configuration)
* Select Remote JVM Debug
* Configure:
* Name: Docker Debug
* Debugger mode: Attach to remote JVM
* Host: localhost
* Port: 5005
* Use module classpath: Select (laa-data-claims-certificated-api)

#### Debugging
* run `docker compose up`
* run > Debug 'Docker Debug'

#### Local Development Logging

When running with the `local` profile, structured logging is disabled, for console output:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Logging Configuration

This application uses **ECS (Elastic Common Schema) structured logging** for production environments and console logging for local development.

#### Structured Logging (Default/Production)

By default, the application outputs logs in ECS JSON format with distributed tracing support:
```json
{
  "@timestamp": "2026-03-06T16:25:18.992904Z",
  "ecs": {
    "version": "8.11"
  },
  "log": {
    "level": "INFO",
    "logger": "uk.gov.justice.laa.data.claims.certificated.api.controller.ItemController"
  },
  "message": "Getting all items",
  "process": {
    "pid": 49402,
    "thread": {
      "name": "http-nio-8080-exec-2"
    }
  },
  "service": {
    "environment": "local",
    "name": "laa-data-claims-certificated-api",
    "node": {
      "name": "unknown"
    },
    "version": "1.0.0"
  },
  "spanId": "fe4586c5fd5f7021",
  "traceId": "69aaffee8d19869cfe4586c5fd5f7021"
}
```
#### logback-spring.xml Conflicts

Adding `logback-spring.xml` will:
- Override the profile-based logging configuration in `application.yml`

## Application Endpoints

### API Documentation

#### Swagger UI
- http://localhost:8081/swagger-ui/index.html

#### API docs (JSON)
- http://localhost:8081/v3/api-docs

### Actuator Endpoints
The following actuator endpoints have been configured:
- http://localhost:8081/actuator
- http://localhost:8081/actuator/health

## Application Configuration

### Sentry
In order to integrate with Sentry, the following properties need to be configured in the `application.yml`:

```
sentry:
  dsn: <configure sentry dsn url here>
  environment: <configure environment name here>
```

### Rate Limiting

The API is protected against excessive use with [resilience4j](https://resilience4j.readme.io/docs/ratelimiter)
rate limiters. Each API operation has its **own** rate limiter instance so that heavy traffic on one
endpoint cannot exhaust the budget of another.

#### Rules and thresholds

Limits are configured under `resilience4j.ratelimiter.instances` in
[`application.yml`](laa-data-claims-certificated-service/src/main/resources/application.yml). The
defaults are:

| Operation            | HTTP request           | Rate limiter instance    | Limit            |
|----------------------|------------------------|--------------------------|------------------|
| List items           | `GET /api/v1/items`    | `getItemsRateLimiter`    | 10 requests / second |
| Get item by id       | `GET /api/v1/items/{id}` | `getItemRateLimiter`   | 10 requests / second |
| Create item          | `POST /api/v1/items`   | `createItemRateLimiter`  | 10 requests / second |
| Update item          | `PUT /api/v1/items/{id}` | `updateItemRateLimiter`| 10 requests / second |
| Delete item          | `DELETE /api/v1/items/{id}` | `deleteItemRateLimiter` | 10 requests / second |

Each instance is configured with three properties:

- `limitForPeriod` – the maximum number of requests permitted within each refresh window.
- `limitRefreshPeriod` – the length of the window, after which the permit count is reset.
- `timeoutDuration` – how long a caller waits for a permit before being rejected. This is set to
  `0s` so that requests over the limit are rejected **immediately** with HTTP 429 rather than
  blocking until a permit becomes available.

#### Behaviour when a limit is exceeded

When an endpoint receives more requests than its limit allows within the refresh window, the excess
requests are rejected with **HTTP 429 Too Many Requests**. The response body is an
[RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) problem detail served as
`application/problem+json`:

```json
{
  "type": "about:blank",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Rate limit exceeded. Please try again later."
}
```

This fallback is implemented once in
[`BaseApiController`](laa-data-claims-certificated-service/src/main/java/uk/gov/justice/laa/data/claims/certificated/api/controller/BaseApiController.java);
controllers extend it and reference the shared `genericFallback` method from their `@RateLimiter`
annotations. Rate limiter instance names are centralised as constants in
[`RateLimiterNames`](laa-data-claims-certificated-service/src/main/java/uk/gov/justice/laa/data/claims/certificated/api/constants/RateLimiterNames.java)
so the annotations always stay in sync with the configuration.

#### Guidance for API consumers

- **Stay within the limits** above. Spread bulk or batch work out over time rather than sending it
  as a single burst.
- **Handle HTTP 429 gracefully.** Treat a 429 as a signal to back off and retry later rather than
  retrying immediately, which would simply consume the next window's budget.
- **Use exponential backoff with jitter.** A common strategy is to wait ~1s, then 2s, 4s, 8s … (with
  a small random jitter) between retries, up to a sensible maximum number of attempts.
- **Make requests idempotent where possible** so that retries are safe.

#### How to configure the limits

To change a limit, edit the relevant instance under `resilience4j.ratelimiter.instances` in
`application.yml`. For example, to allow 50 requests every 2 seconds for the list-items endpoint:

```yaml
resilience4j.ratelimiter:
  instances:
    getItemsRateLimiter:
      limitForPeriod: 50
      limitRefreshPeriod: 2s
      timeoutDuration: 0s
```

Values can also be overridden per environment without code changes via Spring environment
properties / environment variables, e.g.:

```
RESILIENCE4J_RATELIMITER_INSTANCES_GETITEMSRATELIMITER_LIMITFORPERIOD=50
```

When adding a new controller/endpoint, add a matching instance here, add a constant to
`RateLimiterNames`, and reference it from the endpoint's `@RateLimiter` annotation.

## Libraries Used
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html) - used to provide various endpoints to help monitor the application, such as view application health and information.
- [Spring Boot Web](https://docs.spring.io/spring-boot/reference/web/index.html) - used to provide features for building the REST API implementation.
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa.html) - used to simplify database access and interaction, by providing an abstraction over persistence technologies, to help reduce boilerplate code.
- [Springdoc OpenAPI](https://springdoc.org/) - used to generate OpenAPI documentation. It automatically generates Swagger UI, JSON documentation based on your Spring REST APIs.
- used to capture application exception events at runtime, which can be monitored via the Sentry UI.
- [Lombok](https://projectlombok.org/) - used to help to reduce boilerplate Java code by automatically generating common
  methods like getters, setters, constructors etc. at compile-time using annotations.
- [MapStruct](https://mapstruct.org/) - used for object mapping, specifically for converting between different Java object types, such as Data Transfer Objects (DTOs)
  and Entity objects. It generates mapping code at compile code.
- [PostgreSQL](https://www.postgresql.org/) - used to provide a local/example database.
- [resilience4j](https://resilience4j.readme.io/docs/ratelimiter) - used to provide per-endpoint rate limiting, returning HTTP 429 responses when configured thresholds are exceeded.
- [Sentry for Java SDK](https://docs.sentry.io/platforms/java/) - used to capture application exception events at runtime, which can be monitored via the Sentry UI.

## ⚠️ Temporary Dependency Overrides

The following Gradle dependency overrides are **temporary** and should be removed once the dependency versions are
available in a future `laa-spring-boot-common` release.

| Dependency                                  | Overridden Version | Reason                                                                                                                                    | Date Added |
|---------------------------------------------|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------|------------|
| `com.fasterxml.jackson.core:jackson-core`   | `2.21.2`           | Fixes Snyk issue - [SNYK-JAVA-COMFASTERXMLJACKSONCORE-15907551](https://security.snyk.io/vuln/SNYK-JAVA-COMFASTERXMLJACKSONCORE-15907551) | 2026-04-30 |
| `org.apache.tomcat.embed:tomcat-embed-core` | `11.0.22`          | Fixes Snyk issues - [SNYK-JAVA-ORGAPACHETOMCATEMBED-15989820](https://security.snyk.io/vuln/SNYK-JAVA-ORGAPACHETOMCATEMBED-15989820), [SNYK-JAVA-ORGAPACHETOMCATEMBED-16643259](https://security.snyk.io/vuln/SNYK-JAVA-ORGAPACHETOMCATEMBED-16643259), [SNYK-JAVA-ORGAPACHETOMCATEMBED-16691231](https://security.snyk.io/vuln/SNYK-JAVA-ORGAPACHETOMCATEMBED-16691231) | 2026-04-30 |
| `tools.jackson.core:jackson-core`           | `3.1.1`            | Fixes Snyk issue - [SNYK-JAVA-TOOLSJACKSONCORE-15907550](https://security.snyk.io/vuln/SNYK-JAVA-TOOLSJACKSONCORE-15907550)               | 2026-04-30 |
| `org.postgresql:postgresql`                 | `42.7.11`          | Fixes Snyk issue - [SNYK-JAVA-ORGPOSTGRESQL-16321668](https://security.snyk.io/vuln/SNYK-JAVA-ORGPOSTGRESQL-16321668)                     | 2026-05-21 |

### Run Pact contract tests

The application uses Pact provider verification tests to ensure API compatibility with consumer services.

#### Local Development / Offline Mode
While the service is in initial development and before a consumer publishes a contract to the live Pact Broker, verification runs in offline mode using a local pact contract file.

1. Ensure the dummy consumer contract JSON file is located at:
   `src/pactTest/resources/pacts/`
2. Ensure the test class uses the `@PactFolder` annotation pointed to that directory.
3. Execute the Pact test suite locally:
   ```bash
   ./gradlew pactTest