package com.mbr.orders.service;

import com.mbr.orders.dto.ProductDto;
import com.mbr.orders.mapper.OrderMapper;
import com.mbr.orders.domain.Customer;
import com.mbr.orders.domain.OrderHeader;
import com.mbr.orders.domain.OrderItem;
import com.mbr.orders.dto.CreateOrderRequest;
import com.mbr.orders.dto.CreateOrderResponse;
import com.mbr.orders.dto.OrderItemRequest;
import com.mbr.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CatalogClient catalogClient;

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

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer(_customer.get());
        orderHeader.setOrderStatus(OrderHeader.OrderStatus.NEW);
        orderHeader.setTotalAmount(BigDecimal.ZERO);
        OrderHeader createdOrderHeader = orderRepository.save(orderHeader);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest item : orderItems) {

            // item qty check
            if (item.getQuantity() < 1) {
                throw new IllegalArgumentException("Order item quantity must be at least 1");
            }
            ProductDto product = catalogClient.getBySku(item.getProductSku());
            if (!"ACTIVE".equals(product.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Product is not active"
                );
            }

            OrderItem orderItem = orderMapper.toOrderItem(item);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());

            orderItem.setOrderHeader(createdOrderHeader);
            createdOrderHeader.getItems().add(orderItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        }
        createdOrderHeader.setTotalAmount(totalAmount);
        return orderMapper.toCreateOrderResponse(createdOrderHeader);

    }

    @Transactional
    public CreateOrderResponse getOrderById(Long id) {
        OrderHeader order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toCreateOrderResponse(order);

    }

    @Transactional(readOnly = true)
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

        return orders.map(orderMapper::toCreateOrderResponse);
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

}