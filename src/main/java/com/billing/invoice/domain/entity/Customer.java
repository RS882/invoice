package com.billing.invoice.domain.entity;

import com.billing.invoice.constant.PlanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "name")
    @NotNull(message = "Name can not be null")
    private String name;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @NotNull(message = "Plan type can not be null")
    private PlanType planType = PlanType.BASIC;

    @Column(name = "months_subscribed", columnDefinition = "INT DEFAULT 0 CHECK (months_subscribed >= 0)")
    @Min(value = 0, message = "Months subscribed must be great 0")
    @Builder.Default
    private int monthsSubscribed = 0;

    @Column(name = "data_used_GB", columnDefinition = "DOUBLE DEFAULT 0 CHECK (data_used_GB >= 0)")
    @Builder.Default
    @Min(value = 0, message = "Data used must be great 0")
    private double dataUsedGB = 0;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    List<DataUsageHistory> dataUsageHistoryList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Customer that = (Customer) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}



