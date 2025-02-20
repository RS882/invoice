package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.entity.Customer;

public interface CustomerService {

    Customer getCustomerById(Long id);
}
