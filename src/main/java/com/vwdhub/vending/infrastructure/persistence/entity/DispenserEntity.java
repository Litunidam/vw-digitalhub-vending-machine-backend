package com.vwdhub.vending.infrastructure.persistence.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dispensers")
public class DispenserEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "dispenser_id")
    private UUID id;

    @OneToMany(mappedBy = "dispenser", cascade = ALL, orphanRemoval = true)
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "dispenser", cascade = ALL, orphanRemoval = true)
    private List<CoinEntity> dispenserMoney;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispenser_status")
    private DispenserStatusEntity status;

    @Transient
    private List<CoinEntity> insertedMoney;

}
