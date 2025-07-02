package com.vwdhub.vending.infrastructure.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(name = "product_id")
    private UUID id;

    @Column(name = "product_name")
    private String name;

    @Column(name = "product_price")
    private BigDecimal price;

    @Column(name = "product_stock")
    private Integer stock;

    @Column(name = "product_expiration")
    private LocalDate expiration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispenser_id", nullable = false)
    private DispenserEntity dispenser;
}
