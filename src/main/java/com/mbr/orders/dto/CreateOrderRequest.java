package com.mbr.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "customerId must not be null")
    private Long customerId;

    @NotNull
    @NotEmpty(message = "Order must contain at least one item")
    private List<@Valid OrderItemRequest> orderItems;
}

