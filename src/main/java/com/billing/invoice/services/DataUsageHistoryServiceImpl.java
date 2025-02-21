package com.billing.invoice.services;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.DataUsageHistory;
import com.billing.invoice.exception_handler.exceptions.bad_request.exceptions.InvoiceIssuanceException;
import com.billing.invoice.repositories.DataUsageHistoryRepository;
import com.billing.invoice.services.interfaces.DataUsageHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;

import static com.billing.invoice.utilities.DataTimeUtilities.getFirstDayOfLastMonth;
import static com.billing.invoice.utilities.DataTimeUtilities.getLastDayOfLastMonth;

@Service
@RequiredArgsConstructor
public class DataUsageHistoryServiceImpl implements DataUsageHistoryService {

    private final DataUsageHistoryRepository repository;

    @Override
    public void createDataUsageHistory(Customer customer) {

        DataUsageHistory newDataUsageHistory = DataUsageHistory.builder()
                .startDate(getFirstDayOfLastMonth())
                .endDate(getLastDayOfLastMonth())
                .planType(customer.getPlanType())
                .dataUsedGB(customer.getDataUsedGB())
                .customer(customer)
                .build();

        repository.save(newDataUsageHistory);
    }

    @Override
    public void checkDataUsageExistencePreviousMonth(Long customerId) {
        int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
        long usageCount = repository.countDataUsageByCustomerAndMonth(customerId, previousMonth);
        if (usageCount > 0) {
            throw new InvoiceIssuanceException(Month.of(previousMonth));
        }
    }
}
