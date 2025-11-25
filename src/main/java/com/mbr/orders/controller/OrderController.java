package com.mbr.orders.controller;


import com.mbr.orders.dto.CreateOrderRequest;
import com.mbr.orders.domain.OrderHeader;
import com.mbr.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest orderRequest) {

        try {
            var response = orderService.createOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {

        try {
            var Response = orderService.getOrderById(id);
            return ResponseEntity.status(HttpStatus.OK).body(Response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }

    @GetMapping()
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) OrderHeader.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ResponseEntity.ok(
                orderService.getOrders(customerId, status, page, size, sort)
        );
    }

}

