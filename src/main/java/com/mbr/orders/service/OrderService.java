package com.mbr.orders.service;

import com.mbr.orders.controller.dto.*;
import com.mbr.orders.domain.Customer;
import com.mbr.orders.domain.OrderHeader;
import com.mbr.orders.domain.OrderItem;
import com.mbr.orders.repository.OrderItemRepository;
import com.mbr.orders.repository.OrderRepository;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        // customer must exist and not blocked
        Optional<Customer> _customer = customerService.getCustomerByIdAndStatus(
                createOrderRequest.getCustomerId(), Customer.CustomerState.ACTIVE);

        if (_customer.isEmpty()) {
            throw new RuntimeException("Customer not found");

        }
        // order must include at least one item
        if (createOrderRequest.getOrderItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }
        // item qty check
        for (OrderItemRequest item : createOrderRequest.getOrderItems()) {
            if (item.getQuantity() < 1) {
                throw new RuntimeException("Order item quantity must be at least 1");
            }
        }

        // calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest item : createOrderRequest.getOrderItems()) {
            BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
            BigDecimal unitPrice = item.getUnitPrice();
            totalAmount = totalAmount.add(qty.multiply(unitPrice));
        } // check stream usage here

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer(_customer.get());
        orderHeader.setTotalAmount(totalAmount);
        orderHeader.setOrderStatus(OrderHeader.OrderStatus.NEW);

        OrderHeader createdOrderHeader = orderRepository.save(orderHeader);

        List<OrderItem> createdOrderItems = new ArrayList<>();

        for (OrderItemRequest item : createOrderRequest.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderHeader(createdOrderHeader);
            orderItem.setProductSku(item.getSku());
            orderItem.setQuantity(item.getQuantity());

            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setProductName("todo"); // that will come from Category

            createdOrderItems.add(orderItem);
            orderItemRepository.save(orderItem);

        }
        // map entities → DTO
        List<OrderItemResponse> itemResponses = createdOrderItems.stream()
                .map(oi -> new OrderItemResponse(
                        oi.getProductSku(),
                        oi.getProductName(),
                        oi.getUnitPrice(),
                        oi.getQuantity()
                ))
                .toList();
        return new CreateOrderResponse(
                createdOrderHeader.getId(),
                createdOrderHeader.getCustomer().getId(),
                createdOrderHeader.getTotalAmount(),
                itemResponses,
                createdOrderHeader.getOrderStatus().name(),
                createdOrderHeader.getCreatedAt()

        );


    }

    public CreateOrderResponse getOrderById(Long id) {
        OrderHeader order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToOrderResponse(order);

    }

    public Page<CreateOrderResponse> getOrders(
            Long customerId,
            OrderHeader.OrderStatus status,
            int page,
            int size,
            String sortParam
    ) {


        // paging
        Pageable pageable = makePaging(page, size, sortParam);

        Page<OrderHeader> orders;

        // deciding which method runs
        if (customerId != null && status != null) {
            orders = orderRepository.findByCustomer_IdAndOrderStatus(customerId, status, pageable);
        } else if (customerId != null) {
            orders = orderRepository.findByCustomer_Id(customerId, pageable);
        } else if (status != null) {
            orders = orderRepository.findByOrderStatus(status, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(this::convertToOrderResponse);

    }

    private Pageable makePaging(int page, int size, String sortParam) {
        // sorting
        String[] sortParts = sortParam.split(",");
        String sortField = sortParts[0];
        Sort sort = Sort.by(sortField);

        if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return PageRequest.of(page, size, sort);

    }

    private CreateOrderResponse convertToOrderResponse(OrderHeader order) {
        List<OrderItem> items = orderItemRepository.findByOrderHeader_Id(order.getId());
        List<OrderItemResponse> itemResponses = items.stream()
                .map(oi -> new OrderItemResponse(
                        oi.getProductSku(),
                        oi.getProductName(),
                        oi.getUnitPrice(),
                        oi.getQuantity()
                ))
                .toList();
        return new CreateOrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getTotalAmount(),
                itemResponses,
                order.getOrderStatus().name(),
                order.getCreatedAt()
        );
    }

}