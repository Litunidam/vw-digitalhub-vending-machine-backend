package com.vwdhub.vending.infrastructure.persistence.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coin_inventory")
public class CoinEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "coin_id")
    private UUID id;

    @Column(name = "coin_name")
    private String coin;

    @Column(name = "coin_count")
    private int count;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispenser_id", nullable = false)
    private DispenserEntity dispenser;

}
