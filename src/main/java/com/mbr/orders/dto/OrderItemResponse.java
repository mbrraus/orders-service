package com.mbr.orders.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemResponse {
    private String productSku;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}
