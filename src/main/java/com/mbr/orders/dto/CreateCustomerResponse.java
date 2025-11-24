package com.mbr.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CreateCustomerResponse {
    private Long id;
    private String email;
    private String fullName;
    private LocalDateTime createdDate;
}
