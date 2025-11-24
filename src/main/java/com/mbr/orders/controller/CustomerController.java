package com.mbr.orders.controller;

import com.mbr.orders.domain.Customer;
import com.mbr.orders.service.CustomerService;
import com.mbr.orders.controller.dto.CreateCustomerRequest;
import com.mbr.orders.controller.dto.CreateCustomerResponse;
import com.mbr.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequest customerRequest) {
        try {
            var response = customerService.createCustomer(customerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage()); // that's for 409 code.
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        try {
            var response = customerService.getCustomerById(id);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }



}
