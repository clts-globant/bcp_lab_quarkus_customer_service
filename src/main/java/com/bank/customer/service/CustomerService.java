package com.bank.customer.service;

import com.bank.customer.dto.CustomerCreateRequest;
import com.bank.customer.entity.Customer;
import com.bank.customer.exception.CustomerAlreadyExistsException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CustomerService {

    @Inject
    Logger logger;

    public Customer findById(Long id) {
        return Customer.findById(id);
    }

    @Transactional
    public Customer createCustomer(CustomerCreateRequest request) throws CustomerAlreadyExistsException {
        logger.infof("Creating customer with document ID: %s", request.documentId);

        // Check if customer already exists by document ID
        /*if (Customer.existsByDocumentId(request.documentId)) {
            throw new CustomerAlreadyExistsException("Customer with document ID " + request.documentId + " already exists");
        }*/

        // Check if customer already exists by email
        /*if (Customer.existsByEmail(request.email)) {
            throw new CustomerAlreadyExistsException("Customer with email " + request.email + " already exists");
        }*/

        // Create new customer
        Customer customer = new Customer();
        customer.name = request.name;
        customer.documentId = request.documentId;
        customer.email = request.email;
        customer.status = Customer.CustomerStatus.ACTIVE;

        customer.persist();

        logger.infof("Customer created successfully with ID: %d", customer.id);
        return customer;
    }

    public boolean validateCustomer(Long id) {
        Customer customer = Customer.findById(id);
        if (customer == null) {
            return false;
        }

        return customer.status == Customer.CustomerStatus.ACTIVE;
    }


}
