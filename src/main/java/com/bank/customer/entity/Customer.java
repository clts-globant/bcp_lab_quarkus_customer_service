package com.bank.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "customers")
public class Customer extends PanacheEntity {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    @Column(nullable = false, length = 100)
    public String name;

    @NotBlank(message = "Document ID is required")
    @Size(max = 20, message = "Document ID must be less than 20 characters")
    @Column(name = "document_id", nullable = false, unique = true, length = 20)
    public String documentId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    public String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CustomerStatus status = CustomerStatus.ACTIVE;

    public enum CustomerStatus {
        ACTIVE, INACTIVE
    }

    public static Customer findById(Long id) {
        return find("id", id).firstResult();
    }

    /*public static boolean existsByDocumentId(String documentId) {
        return count("documentId", documentId) > 0;
    }*/


}
