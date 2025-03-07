package com.billing.invoice.repositories;

import com.billing.invoice.domain.entity.DataUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DataUsageHistoryRepository extends JpaRepository<DataUsageHistory, Long> {

    @Query("SELECT COUNT(d) FROM DataUsageHistory d WHERE d.customer.id = :customerId AND MONTH(d.endDate) = :month")
    long countDataUsageByCustomerAndMonth(@Param("customerId") Long customerId, @Param("month") int month);

    @Query("SELECT d FROM DataUsageHistory d WHERE d.customer.id = :customerId")
    List<DataUsageHistory> findAllByCustomerId(@Param("customerId") Long customerId);
}
