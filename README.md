# Account service

This project uses Quarkus.

This microservice creates and allows basic validation of customers for the case uses described by the transactions
microservice: https://github.com/clts-globant/bcp_lab_quarkus_transaction_service

Features:

* Get customer basic details by id

`
curl -v -X GET http://localhost:8081/api/customers/{customer_id}
-H "Authorization: Bearer $JWT_TOKEN"
`

Details include name, documentid, email and status (`ACTIVE` if valid for transactions.

* Create a new customer

`
curl -v -X POST http://localhost:8081/api/customers
-H "Content-Type: application/json"
-H "Authorization: Bearer $JWT_ADMIN_TOKEN"
-d '{
"name": "Jane Doe",
"email": "jane.doe@example.com",
"documentId": "ID-987654321"
}'
`

No particular validation details checked at the moment, like unique email or documentId, but the respective
code is commented out if you wanna try it by yourself.

* Customer validation to see if it's valid for transactions

`
curl -v -X GET http://localhost:8081/api/customers/{customer_id}/validate
-H "Authorization: Bearer $JWT_TOKEN"
`

Look for `valid` in the response body, boolean value.

Basic health checks (like `q/health`) and metrics are supported thanks to Quarkus/micrometer.
Read https://quarkus.io/guides/management-interface-reference for more details.

## Unit/integration tests
Run
```shell script
./mvnw test
```

## Running the application in dev mode

You can run your application in dev mode + live coding:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
Not an uber jar, as the dependencies are copied into the `target/quarkus-app/lib/` directory.

Run with `java -jar target/quarkus-app/quarkus-run.jar`.

For uber jar:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

Run with `java -jar target/*-runner.jar`.

## Creating a native executable

```shell script
./mvnw package -Dnative
```

Application not tested with native build, so far.

You can run the native executable build in a container with:
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Then simply execute with: `./target/account-service-1.0.0-SNAPSHOT-runner`

## Generating a valid JWT

The JWT is only checked for completeness, not for full authorization+authentication, as a full identity system
wasn't implemented for the whole solution. As long as the JWT is valid, it should be usable.

Instructions to generate a key pair: https://techdocs.akamai.com/iot-token-access-control/docs/generate-rsa-keys
Instructions to generate a JWT wit said keys: https://techdocs.akamai.com/iot-token-access-control/docs/generate-jwt-rsa-key