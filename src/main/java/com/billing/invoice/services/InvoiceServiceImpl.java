package com.billing.invoice.services;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.exception_handler.exceptions.bad_request.exceptions.InvoiceIssuanceException;
import com.billing.invoice.repositories.InvoiceRepository;
import com.billing.invoice.services.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository repository;
    @Override
    public Invoice createNewInvoice(Customer customer, BigDecimal amount) {

        Invoice newInvoice = Invoice.builder()
                .customer(customer)
                .amount(amount)
                .build();

        return repository.save(newInvoice);
    }

    @Override
    public void checkInvoiceIssuanceCurrentsMonth(Long customerId) {
        LocalDate now = LocalDate.now();
        long invoiceCount = repository.countInvoicesByCustomerAndMonth(customerId, now.getMonthValue());
        if (invoiceCount > 0) {
            throw new InvoiceIssuanceException(now.getMonth().minus(1));
        }
    }
}
