package com.billing.invoice.exception_handler.exceptions.not_found;

public class CustomerNotFoundException extends NotFoundException{
    public CustomerNotFoundException(Long id) {
        super( String.format("Customer with id <%d> not found", id));
    }
}
