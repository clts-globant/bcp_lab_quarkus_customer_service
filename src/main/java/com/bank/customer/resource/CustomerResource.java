package com.bank.customer.resource;

import com.bank.customer.dto.CustomerValidationResponse;
import com.bank.customer.service.CustomerService;
import com.bank.customer.dto.CustomerCreateRequest;
import com.bank.customer.entity.Customer;
import com.bank.customer.exception.CustomerAlreadyExistsException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Context;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

@ApplicationScoped
@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    Logger logger;

    @Inject
    CustomerService customerService;

    @Inject
    MeterRegistry meterRegistry;

    @Inject
    JsonWebToken jwt;

    @Context
    SecurityContext securityContext;

    /**
     * Safely gets the current user name, handling both JWT and test security contexts
     */
    private String getCurrentUserName() {
        try {
            // Try to get JWT name first
            return jwt != null ? jwt.getName() : null;
        } catch (IllegalStateException e) {
            // Fallback to SecurityContext for test scenarios
            if (securityContext != null && securityContext.getUserPrincipal() != null) {
                return securityContext.getUserPrincipal().getName();
            }
            return "unknown-user";
        }
    }

    private Counter customerQueriesCounter;
    private Counter customerCreationsCounter;

    @jakarta.annotation.PostConstruct
    void initMetrics() {
        customerQueriesCounter = Counter.builder("customer.queries.total")
                .description("Total number of customer queries")
                .register(meterRegistry);
        customerCreationsCounter = Counter.builder("customer.creations.total")
                .description("Total number of customer creations")
                .register(meterRegistry);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_VIEWER"})
    public Response getCustomerById(@PathParam("id") Long id) {
        customerQueriesCounter.increment();
        logger.infof("Getting customer by ID: %d, User: %s", id, getCurrentUserName());
        
        try {
            Customer customer = customerService.findById(id);
            if (customer == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Customer not found with id: " + id))
                    .build();
            }
            return Response.ok(customer).build();
        } catch (Exception e) {
            logger.errorf("Error getting customer by ID %d: %s", id, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error"))
                .build();
        }
    }

    @POST
    @Transactional
    @RolesAllowed({"ROLE_ADMIN"})
    public Response createCustomer(@Valid CustomerCreateRequest request) {
        customerCreationsCounter.increment();
        logger.infof("Creating customer with document ID: %s, User: %s", request.documentId, getCurrentUserName());
        
        try {
            Customer customer = customerService.createCustomer(request);
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } catch (CustomerAlreadyExistsException e) {
            logger.warnf("Customer already exists: %s", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        } catch (Exception e) {
            logger.errorf("Error creating customer: %s", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error"))
                .build();
        }
    }

    @GET
    @Path("/{id}/validate")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public Response validateCustomer(@PathParam("id") Long id) {
        logger.infof("Validating customer with ID: %d, User: %s", id, getCurrentUserName());
        
        try {
            boolean isValid = customerService.validateCustomer(id);
            return Response.ok(new CustomerValidationResponse(isValid, id)).build();
        } catch (Exception e) {
            logger.errorf("Error validating customer with ID %d: %s", id, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error"))
                .build();
        }
    }

    public static class ErrorResponse {
        public String message;
        public long timestamp;
        
        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
