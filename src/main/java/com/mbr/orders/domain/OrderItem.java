package com.mbr.orders.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_item")
public class OrderItem extends AbstractEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderHeader orderHeader;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    @Column(name = "product_name_snapshot", nullable = false)
    private String productName;

    @Column(name = "unit_price_snapshot", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
