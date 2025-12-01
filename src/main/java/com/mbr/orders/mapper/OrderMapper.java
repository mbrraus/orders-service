package com.mbr.orders.mapper;

import com.mbr.orders.domain.OrderHeader;
import com.mbr.orders.domain.OrderItem;
import com.mbr.orders.dto.CreateOrderResponse;
import com.mbr.orders.dto.OrderItemRequest;
import com.mbr.orders.dto.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "productName", constant = "todo")
    @Mapping(target = "orderHeader", ignore = true)
    OrderItem toOrderItem(OrderItemRequest request);

    @Mapping(target = "customerId", source = "customer.id")
    CreateOrderResponse toCreateOrderResponse(OrderHeader orderHeader);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}

