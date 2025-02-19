package com.billing.invoice.domain.entity;

import com.billing.invoice.domain.constant.PlanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    private PlanType planType = PlanType.BASIC;

    @Column(name = "months_subscribed", columnDefinition = "INT DEFAULT 0 CHECK (months_subscribed >= 0)")
    private int monthsSubscribed = 0;

    @Column(name = "data_used_GB", columnDefinition = "DOUBLE DEFAULT 0 CHECK (data_used_GB >= 0)")
    private double dataUsedGB = 0;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Invoice> invoices = new ArrayList<>();
}
