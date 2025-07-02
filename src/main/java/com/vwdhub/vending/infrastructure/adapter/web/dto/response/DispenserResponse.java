package com.vwdhub.vending.infrastructure.adapter.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DispenserResponse {

    @JsonProperty("id")
    @Schema(description = "Identifier of dispenser", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID id;

    @JsonProperty("products")
    private final Map<UUID, ProductResponse> products;

    @JsonProperty("dispenserMoney")
    @Schema(description = "Money inventory of dispenser")
    private final Money dispenserMoney;

    @JsonProperty("status")
    @Schema(description = "Dispenser status", example = "AVAILABLE")
    private final DispenserStatus status;

    @JsonProperty("insertedMoney")
    @Schema(description = "Money inserted into dispenser")
    private final Money insertedMoney;
}
