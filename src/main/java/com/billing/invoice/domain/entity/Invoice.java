package com.billing.invoice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "billing_date", updatable = false, nullable = false)
    private LocalDate billingDate;

    @Column(name = "invoice_file_path")
    private String invoiceFilePath;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PaymentHistory> paymentHistoryList = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (billingDate == null) {
            billingDate = LocalDate.now();
        }
    }
}
