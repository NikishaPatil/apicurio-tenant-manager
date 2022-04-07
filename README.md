# Apicurio Tenant Manager
A common tenant manager component for use when deploying Apicurio applications using multi-tenant configurations.

## Build Configuration

There are two main configuration profiles:

* _dev_ - suitable for development
* _prod_ - for production

## Getting started

```shell
./mvnw clean install -DskipTests
cd api
../mvnw quarkus:dev
```

This will result in the tenant manager API starting on http://localhost:8585 using an in-memory database (H2).

## Runtime configuration

The following parameters are available to alter configuration at runtime:

| Option  | Command argument  | Environment variable   |
|---|---|---|
| Datasource URL  | `-Dquarkus.jdbc.url`  | `DATASOURCE_URL`  |
| Datasource username  | `-Dquarkus.datasource.username`   | `DATASOURCE_USERNAME`  |
| Datasource password  | `-Dquarkus.datasource.password`   | `DATASOURCE_PASSWORD`   |