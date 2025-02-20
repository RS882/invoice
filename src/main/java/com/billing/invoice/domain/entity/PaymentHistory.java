package com.billing.invoice.domain.entity;

import com.billing.invoice.domain.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "payment_history",
        indexes = @Index(name = "idx_invoice_id", columnList = "invoice_id"))
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "payment_date", nullable = false, updatable = false)
    @NotNull
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod;

    @Column(name = "amount_paid",
            columnDefinition = "NUMERIC(38,2) DEFAULT 0 CHECK (amount_paid >= 0)",
            nullable = false,
            updatable = false)
    @NotNull
    private BigDecimal amountPaid;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "invoice_id", nullable = false, updatable = false)
    @NotNull
    @ToString.Exclude
    private Invoice invoice;

    @PrePersist
    protected void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PaymentHistory that = (PaymentHistory) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
