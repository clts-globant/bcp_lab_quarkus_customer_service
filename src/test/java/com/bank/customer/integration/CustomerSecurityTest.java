package com.bank.customer.integration;

import com.bank.customer.dto.CustomerCreateRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
  Security tests for HTTP endpoints in microservice
 */
@QuarkusTest
public class CustomerSecurityTest {

    @Test
    @TestSecurity(user = "user", roles = {"ROLE_USER"})
    public void testValidateCustomerNoSecurityError() {
        given()
            .when()
            .get("/api/customers/101/validate")
            .then()
            .statusCode(200);
    }

    @Test
    public void testGetCustomerWithoutAuthenticationShouldFail() {
        given()
            .when()
            .get("/api/customers/1")
            .then()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "viewer", roles = {"ROLE_VIEWER"})
    public void testCreateCustomerWithInsufficientRolesShouldFail() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.name = "Unauthorized User";
        request.documentId = "999999";
        request.email = "unauthorized@example.com";

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/customers")
            .then()
            .statusCode(403);
    }

}
