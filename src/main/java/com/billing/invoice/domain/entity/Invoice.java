package com.billing.invoice.domain.entity;

import com.billing.invoice.constant.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "invoice",
        indexes = @Index(name = "idx_customer_id_for_invoice", columnList = "customer_id")
)
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "amount")
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "billing_date", updatable = false, nullable = false)
    @NotNull
    private LocalDate billingDate;

    @Column(name = "invoice_file_path", length = 512)
    private String invoiceFilePath;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @NotNull
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    @NotNull
    @ToString.Exclude
    Customer customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    List<PaymentHistory> paymentHistoryList = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (billingDate == null) {
            billingDate = LocalDate.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Invoice that = (Invoice) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
