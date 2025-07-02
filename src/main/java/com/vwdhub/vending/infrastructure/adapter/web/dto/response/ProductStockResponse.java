package com.vwdhub.vending.infrastructure.adapter.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductStockResponse {

    @JsonProperty("stock")
    @Schema(description = "Available stock", example = "10")
    private final Integer stock;

}
