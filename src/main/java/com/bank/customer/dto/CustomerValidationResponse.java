package com.bank.customer.dto;

public class CustomerValidationResponse {

  public boolean valid;
  public Long customerId;

  public CustomerValidationResponse(boolean valid, Long customerId) {
    this.valid = valid;
    this.customerId = customerId;
  }

}
