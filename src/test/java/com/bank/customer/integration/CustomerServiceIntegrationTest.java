package com.bank.customer.integration;

import com.bank.customer.dto.CustomerCreateRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Integration tests for Customer Service endpoints.
 */
@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerServiceIntegrationTest {

    public static final String TEST_NAME = "John Doe";
    private static String createdCustomerId;
    private static final String TEST_DOCUMENT_ID = "12345678";
    private static final String TEST_EMAIL = "john.doe@example.com";

    @Test
    @Order(1)
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    public void testCreateCustomer() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.name = TEST_NAME;
        request.documentId = TEST_DOCUMENT_ID;
        request.email = TEST_EMAIL;

        createdCustomerId = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/customers")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", is("John Doe"))
            .body("documentId", is(TEST_DOCUMENT_ID))
            .body("email", is(TEST_EMAIL))
            .body("status", is("ACTIVE"))
            .log().body()
            .extract()
            .path("id")
            .toString();
    }

    @Test
    @Order(2)
    @TestSecurity(user = "user", roles = {"ROLE_USER"})
    public void testGetCustomerById() {
        given()
            .when()
            .get("/api/customers/" + createdCustomerId)
            .then()
            .statusCode(200)
            .body("id", is(Integer.valueOf(createdCustomerId)))
            .body("name", is("John Doe"))
            .body("documentId", is(TEST_DOCUMENT_ID))
            .body("email", is(TEST_EMAIL))
            .body("status", is("ACTIVE"));
    }

    @Test
    @Order(3)
    @TestSecurity(user = "user", roles = {"ROLE_USER"})
    public void testValidateCustomer_ActiveCustomer() {
        given()
            .when()
            .get("/api/customers/" + createdCustomerId + "/validate")
            .then()
            .statusCode(200)
            .body("valid", is(true))
            .body("customerId", is(Integer.valueOf(createdCustomerId)));
    }

    @Test
    @Order(4)
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    public void testCreateCustomer_InvalidData() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.name = ""; // Empty name
        request.documentId = "999999";
        request.email = "invalid-email"; // Invalid email format

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/customers")
            .then()
            .statusCode(400); // Bad Request
    }

    @Test
    @Order(5)
    @TestSecurity(user = "user", roles = {"ROLE_USER"})
    public void testGetCustomer_NotFound() {
        given()
            .when()
            .get("/api/customers/99999")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(6)
    @TestSecurity(user = "user", roles = {"ROLE_USER"})
    public void testGetCustomerByDocumentId_NotFound() {
        given()
            .when()
            .get("/api/customers/document/99999999")
            .then()
            .statusCode(404);
    }

    @Test
    @Order(7)
    @TestSecurity(user = "user", roles = {"ROLE_USER"})
    public void testValidateCustomer_NotFound() {
        given()
            .when()
            .get("/api/customers/99999/validate")
            .then()
            .statusCode(200)
            .body("valid", is(false));
    }

    // Health checks
    @Test
    public void testHealthEndpoint() {
        given()
            .when()
            .get("/q/health")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }

    @Test
    public void testMetricsEndpoint() {
        given()
            .when()
            .get("/q/metrics")
            .then()
            .statusCode(200);
    }
}
