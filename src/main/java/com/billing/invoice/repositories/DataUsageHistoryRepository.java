package com.billing.invoice.repositories;

import com.billing.invoice.domain.entity.DataUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DataUsageHistoryRepository extends JpaRepository<DataUsageHistory, Long> {

    @Query("SELECT COUNT(d) FROM DataUsageHistory d WHERE d.customer.id = :customerId AND MONTH(d.endDate) = :month")
    long countDataUsageByCustomerAndMonth(@Param("customerId") Long customerId, @Param("month") int month);

}
