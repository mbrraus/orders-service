package com.mbr.orders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemRequest {
    private String sku;
    private int quantity;
    private BigDecimal unitPrice; // this normally needs to come from Catalog service
}
