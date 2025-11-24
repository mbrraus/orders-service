package com.mbr.orders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponse {
    private String sku;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}
