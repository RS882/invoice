package com.billing.invoice.services;

import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.repositories.InvoiceRepository;
import com.billing.invoice.services.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository repository;
    @Override
    public Invoice saveInvoice(Invoice invoice) {
        return repository.save(invoice);
    }
}
