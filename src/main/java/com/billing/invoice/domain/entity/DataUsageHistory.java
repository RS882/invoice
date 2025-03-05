package com.billing.invoice.domain.entity;

import com.billing.invoice.constant.PlanType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "data_usage_history",
        indexes = @Index(name = "idx_customer_id", columnList = "customer_id")
)
@Check(constraints = "data_used_gb >= 0")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class DataUsageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "start_date", nullable = false, updatable = false)
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false, updatable = false)
    @NotNull
    private LocalDate endDate;

    @Column(name = "plan_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private PlanType planType;

    @Column(name = "data_used_gb", nullable = false, updatable = false)
    @NotNull
    private double dataUsedGB;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    @NotNull
    @ToString.Exclude
    Customer customer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DataUsageHistory that = (DataUsageHistory) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}

