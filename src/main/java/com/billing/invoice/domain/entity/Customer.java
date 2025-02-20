package com.billing.invoice.domain.entity;

import com.billing.invoice.domain.constant.PlanType;
import jakarta.persistence.*;
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
@AllArgsConstructor
@Builder
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @NotNull
    private PlanType planType = PlanType.BASIC;

    @Column(name = "months_subscribed", columnDefinition = "INT DEFAULT 0 CHECK (months_subscribed >= 0)")
    @Builder.Default
    private int monthsSubscribed = 0;

    @Column(name = "data_used_GB", columnDefinition = "DOUBLE DEFAULT 0 CHECK (data_used_GB >= 0)")
    @Builder.Default
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
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}



