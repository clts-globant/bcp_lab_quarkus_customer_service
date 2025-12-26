package com.bank.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerCreateRequest {

  @NotBlank
  @Size(max = 100)
  public String name;

  @NotBlank
  @Size(max = 20)
  public String documentId;

  @NotBlank
  @Email
  @Size(max = 100)
  public String email;

}
