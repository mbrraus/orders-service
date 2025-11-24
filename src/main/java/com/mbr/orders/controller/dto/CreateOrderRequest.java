package com.mbr.orders.controller.dto;

import com.mbr.orders.domain.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "customerId must not be null")
    private Long customerId;

    @NotEmpty
    private List<@Valid OrderItemRequest> orderItems;
}

