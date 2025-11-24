package com.mbr.orders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateOrderResponse {
    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> orderItems;
    private String orderStatus;
    private LocalDateTime createdAt;
}
