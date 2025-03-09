package com.billing.invoice.services;

import com.billing.invoice.constant.InvoiceStatus;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.domain.entity.PaymentHistory;
import com.billing.invoice.exception_handler.exceptions.bad_request.exceptions.InvoiceIssuanceException;
import com.billing.invoice.exception_handler.exceptions.not_found.excrptions.InvoiceNotFoundException;
import com.billing.invoice.repositories.InvoiceRepository;
import com.billing.invoice.services.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.billing.invoice.utilities.BigDecimalUtilities.scaleValue;

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
            throw new InvoiceIssuanceException(now.minusMonths(1).getMonth());
        }
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return repository.findById(id).orElseThrow(() -> new InvoiceNotFoundException(id));
    }

    @Override
    public BigDecimal calculateRemainingBalance(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);

        return calculateRemainingBalance(invoice);
    }

    private BigDecimal calculateRemainingBalance(Invoice invoice) {

        BigDecimal paidAmount = scaleValue(
                invoice.getPaymentHistoryList()
                        .stream()
                        .map(PaymentHistory::getAmountPaid)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return scaleValue(invoice.getAmount().subtract(paidAmount)).max(BigDecimal.ZERO);
    }

    @Override
    public Invoice updateInvoiceFilePath(Invoice invoice, String invoiceFilePath) {
        if (invoiceFilePath == null || invoiceFilePath.isBlank()) {
            return invoice;
        }
        invoice.setInvoiceFilePath(invoiceFilePath);
        return repository.save(invoice);
    }

    @Override
    public void checkAndUpdateInvoiceStatus(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        BigDecimal remainingBalance = calculateRemainingBalance(invoice);

        if (invoice.getAmount().compareTo(remainingBalance) == 0) {
            return;
        }
        InvoiceStatus newStatus = remainingBalance.compareTo(BigDecimal.ZERO) > 0
                ? InvoiceStatus.PARTIALLY_PAID
                : InvoiceStatus.PAID;

        if (!invoice.getStatus().equals(newStatus)) {
            invoice.setStatus(newStatus);
            repository.save(invoice);
        }
    }
}
