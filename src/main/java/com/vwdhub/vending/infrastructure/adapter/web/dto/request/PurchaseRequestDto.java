package com.vwdhub.vending.infrastructure.adapter.web.dto.request;

import com.vwdhub.vending.domain.model.Coin;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class PurchaseRequestDto {

    @Schema(description = "Identifier of the product", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID productId;

    @NotNull(message = "Coins are required")
    @Schema(
            description = "Amount for each coin",
            type = "object",
            additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
            example = "{\n" +
                    "  \"EUR_2\": 1,\n" +
                    "  \"EUR_1\": 5,\n" +
                    "  \"CENT_50\": 2\n" +
                    "}"
    )
    private final Map<Coin, Integer> coins;

    @NotNull(message = "Need to confirm the purchase")
    private final boolean confirmed;

}
