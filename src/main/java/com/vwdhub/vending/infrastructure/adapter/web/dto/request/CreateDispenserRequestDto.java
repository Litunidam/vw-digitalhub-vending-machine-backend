package com.vwdhub.vending.infrastructure.adapter.web.dto.request;

import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Getter
@Setter
@Builder
public class CreateDispenserRequestDto {

    UUID id;

    List<Product> products;

    Money initialCoins;

    DispenserStatus status;

    public CreateDispenserRequestDto(UUID id, List<Product> products, Money initialCoins, DispenserStatus status) {
        this.id = id;
        this.products = products;
        this.initialCoins = initialCoins;
        this.status = status;
    }
}
