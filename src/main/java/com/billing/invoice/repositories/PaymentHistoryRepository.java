package com.billing.invoice.repositories;

import com.billing.invoice.domain.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query("SELECT p FROM PaymentHistory p WHERE p.invoice.id = :invoiceId")
    List<PaymentHistory> findAllByInvoiceId(@Param("invoiceId") Long invoiceId);
}
