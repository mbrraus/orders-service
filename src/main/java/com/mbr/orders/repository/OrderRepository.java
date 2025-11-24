package com.mbr.orders.repository;

import com.mbr.orders.domain.OrderHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderHeader, Long> {
    Page<OrderHeader> findByCustomer_IdAndOrderStatus(
            Long customerId,
            OrderHeader.OrderStatus status,
            Pageable pageable);

    Page<OrderHeader> findByCustomer_Id(
            Long customerId,
            Pageable pageable
    );

    Page<OrderHeader> findByOrderStatus(
            OrderHeader.OrderStatus status,
            Pageable pageable
    );

}
