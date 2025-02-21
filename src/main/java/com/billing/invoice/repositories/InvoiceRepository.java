package com.billing.invoice.repositories;

import com.billing.invoice.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.customer.id = :customerId AND MONTH(i.billingDate) = :month")
    long countInvoicesByCustomerAndMonth(@Param("customerId") Long customerId, @Param("month") int month);
}
