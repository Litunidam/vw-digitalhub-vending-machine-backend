package com.vwdhub.vending.infrastructure.adapter.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {

    @JsonProperty("id")
    @Schema(description = "Identifier of product", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID id;

    @JsonProperty("name")
    @Schema(description = "Product name", example = "Fanta")
    private final String name;

    @JsonProperty("price")
    @Schema(description = "Product price", example = "2.70")
    private final BigDecimal price;

    @JsonProperty("stock")
    @Schema(description = "Available stock", example = "10")
    private Integer stock;

    @JsonProperty("expirationDate")
    @Schema(description = "Expiration date (YYYY-MM-DD)", example = "2025-12-01")
    private LocalDate expiration;
}
