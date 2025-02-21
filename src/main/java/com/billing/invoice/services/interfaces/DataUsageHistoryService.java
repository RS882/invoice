package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.entity.Customer;

public interface DataUsageHistoryService {

    void createDataUsageHistory(Customer customer);

    void checkDataUsageExistencePreviousMonth(Long customerId);
}
