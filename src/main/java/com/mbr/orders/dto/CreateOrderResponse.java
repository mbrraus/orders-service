package com.mbr.orders.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderResponse {
    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
    private String orderStatus;
    private LocalDateTime createdAt;
}
