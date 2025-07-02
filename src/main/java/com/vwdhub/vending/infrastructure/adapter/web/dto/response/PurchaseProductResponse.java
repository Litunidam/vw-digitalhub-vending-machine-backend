package com.vwdhub.vending.infrastructure.adapter.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PurchaseProductResponse {

    @JsonProperty("product")
    private final Product product;
    @JsonProperty("change")
    private final Money change;
}
