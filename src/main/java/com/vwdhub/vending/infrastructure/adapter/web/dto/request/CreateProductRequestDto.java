package com.vwdhub.vending.infrastructure.adapter.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Getter
@Setter
@Builder
public class CreateProductRequestDto {

    @Schema(description = "Identifier of the product", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    UUID dispenserId;

    @Schema(description = "Product name", example = "Fanta")
    String name;

    @Schema(description = "Product price", example = "2.70")
    BigDecimal price;

    @Schema(description = "Available stock", example = "10")
    Integer stock;

    @Schema(description = "Expiration date (YYYY-MM-DD)", example = "2025-12-01")
    LocalDate expiration;
}
