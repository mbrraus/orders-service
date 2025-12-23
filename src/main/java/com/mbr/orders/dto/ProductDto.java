package com.mbr.orders.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {
    private String sku;
    private String name;
    private BigDecimal price;
    private String status;
}
