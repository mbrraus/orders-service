package com.mbr.orders.service;

import com.mbr.orders.domain.Customer;
import com.mbr.orders.domain.OrderHeader;
import com.mbr.orders.domain.OrderItem;
import com.mbr.orders.dto.CreateOrderRequest;
import com.mbr.orders.dto.CreateOrderResponse;
import com.mbr.orders.dto.OrderItemRequest;
import com.mbr.orders.dto.OrderItemResponse;
import com.mbr.orders.repository.OrderItemRepository;
import com.mbr.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {

        var orderItems = createOrderRequest.getOrderItems();

        // customer must exist and not blocked
        Optional<Customer> _customer = customerService.getCustomerByIdAndStatus(
                createOrderRequest.getCustomerId(), Customer.CustomerState.ACTIVE);

        if (_customer.isEmpty()) {
            throw new IllegalArgumentException("Customer not found or blocked");

        }
        // order must include at least one item
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        // item qty check
        for (OrderItemRequest item : orderItems) {
            if (item.getQuantity() < 1) {
                throw new IllegalArgumentException("Order item quantity must be at least 1");
            }
        }

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer(_customer.get());
        orderHeader.setTotalAmount(calculateTotalAmount(orderItems));
        orderHeader.setOrderStatus(OrderHeader.OrderStatus.NEW);

        OrderHeader createdOrderHeader = orderRepository.save(orderHeader);

        List<OrderItem> createdOrderItems = new ArrayList<>();

        for (OrderItemRequest item : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderHeader(createdOrderHeader);
            orderItem.setProductSku(item.getSku());
            orderItem.setQuantity(item.getQuantity());

            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setProductName("todo"); // that will come from Category

            createdOrderItems.add(orderItem);
            orderItemRepository.save(orderItem);

        }

        return convertToOrderResponse(createdOrderHeader);


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

    private BigDecimal calculateTotalAmount(List<OrderItemRequest> orderItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest item : orderItems) {
            BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
            BigDecimal unitPrice = item.getUnitPrice();
            totalAmount = totalAmount.add(qty.multiply(unitPrice));
        } // check stream usage here
        return totalAmount;
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