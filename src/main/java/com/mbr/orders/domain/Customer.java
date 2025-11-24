package com.mbr.orders.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "customer")
@NoArgsConstructor
public class Customer extends AbstractEntity {
    public enum CustomerState {
        ACTIVE,
        BLOCKED
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerState state;

    public Customer (String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
        this.state = CustomerState.ACTIVE;
    }

//  private LocalDateTime createdDate;

}

