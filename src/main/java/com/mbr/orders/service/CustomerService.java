package com.mbr.orders.service;

import com.mbr.orders.domain.Customer;
import com.mbr.orders.domain.OrderHeader;
import com.mbr.orders.repository.CustomerRepository;
import com.mbr.orders.controller.dto.CreateCustomerRequest;
import com.mbr.orders.controller.dto.CreateCustomerResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    // constructor injection here
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {

        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {

        Optional<Customer> _customer = getCustomerByEmail(request.getEmail());
        if (_customer.isPresent()) {
            throw new DataIntegrityViolationException("Duplicate email for the customer");
        } else {
            Customer customer = new Customer(
                    request.getEmail(),
                    request.getFullName()

            );

            Customer newCustomer = customerRepository.save(customer);
            CreateCustomerResponse response = new CreateCustomerResponse(
                    newCustomer.getId(),
                    newCustomer.getEmail(),
                    newCustomer.getFullName(),
                    newCustomer.getCreatedAt()
            );
            return response;
        }

    }

    public CreateCustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return new CreateCustomerResponse(
                customer.getId(),
                customer.getEmail(),
                customer.getFullName(),
                customer.getCreatedAt()
        );

    }

    public Optional<Customer> getCustomerByIdAndStatus(Long id, Customer.CustomerState state) {
        return customerRepository.findByIdAndState(id,state);
    }

}
