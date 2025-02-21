package com.billing.invoice.services;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.exception_handler.exceptions.not_found.excrptions.CustomerNotFoundException;
import com.billing.invoice.repositories.CustomerRepository;
import com.billing.invoice.services.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    @Override
    public Customer getCustomerById(Long id) {
        return repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    public void cleanDataUsedGB(Customer customer) {
        customer.setDataUsedGB(0);
        customer.setMonthsSubscribed(customer.getMonthsSubscribed() + 1);
        repository.save(customer);
    }
}
