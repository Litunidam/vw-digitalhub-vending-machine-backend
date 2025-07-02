package com.vwdhub.vending.infrastructure.adapter.web;

import com.vwdhub.vending.application.usecase.AddDispenserUseCase;
import com.vwdhub.vending.application.usecase.GetDispenserStatusUseCase;
import com.vwdhub.vending.application.usecase.GetDispenserUseCase;
import com.vwdhub.vending.application.usecase.PurchaseProductUseCase;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.CreateDispenserRequestDto;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.PurchaseRequestDto;
import com.vwdhub.vending.infrastructure.adapter.web.dto.response.DispenserResponse;
import com.vwdhub.vending.infrastructure.adapter.web.dto.response.DispenserStatusResponse;
import com.vwdhub.vending.infrastructure.adapter.web.dto.response.ProductResponse;
import com.vwdhub.vending.infrastructure.adapter.web.dto.response.PurchaseProductResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/vending")
public class DispenserController {

    private final AddDispenserUseCase addDispenserUseCase;
    private final GetDispenserUseCase getDispenserUseCase;
    private final PurchaseProductUseCase purchaseProductUseCase;
    private final GetDispenserStatusUseCase getDispenserStatusUseCase;

    public DispenserController(AddDispenserUseCase addDispenserUseCase, GetDispenserUseCase getDispenserUseCase, PurchaseProductUseCase purchaseProductUseCase, GetDispenserStatusUseCase getDispenserStatusUseCase) {
        this.addDispenserUseCase = addDispenserUseCase;
        this.getDispenserUseCase = getDispenserUseCase;
        this.purchaseProductUseCase = purchaseProductUseCase;
        this.getDispenserStatusUseCase = getDispenserStatusUseCase;
    }

    @PostMapping("/dispenser")
    public ResponseEntity<DispenserResponse> createDispenser(@RequestBody CreateDispenserRequestDto request) {
        Dispenser dispenser = addDispenserUseCase.add(
                request.getId(),
                request.getProducts(),
                request.getInitialCoins(),
                request.getStatus()
        );
        DispenserResponse response = fromDispenserToDispenseResponse(dispenser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dispenser/{id}")
    public ResponseEntity<DispenserResponse> getDispenser(@PathVariable("id") UUID dispenserId) {
        Dispenser dispenser = getDispenserUseCase.get(dispenserId);
        DispenserResponse response = fromDispenserToDispenseResponse(dispenser);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/dispenser/{id}/purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> purchase(@PathVariable("id") UUID dispenserId, @RequestBody PurchaseRequestDto request) {
        try {
            ChangeAndProduct result = purchaseProductUseCase.purchase(
                    dispenserId,
                    request.getProductId(),
                    Money.builder().coins(request.getCoins()).build(),
                    request.isConfirmed()
            );
            PurchaseProductResponse response = PurchaseProductResponse.builder().product(result.getProduct()).change(result.getChange()).build();
            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", exception.getMessage(),
                    "refund", request.getCoins()
            ));
        }
    }

    @GetMapping(path = "/dispenser/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DispenserStatusResponse> getDispenserStatus(@PathVariable("id") UUID dispenserId) {
        DispenserStatus status = getDispenserStatusUseCase.getDispenserStatus(dispenserId);
        DispenserStatusResponse dispenserStatusResponse = DispenserStatusResponse.builder().status(status.name()).build();
        return ResponseEntity.ok(dispenserStatusResponse);
    }

    private DispenserResponse fromDispenserToDispenseResponse(Dispenser dispenser) {
        Map<UUID, ProductResponse> productsResponse = dispenser.getProducts().entrySet()
                .stream()
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                productEntry -> ProductResponse.builder()
                                        .id(productEntry.getKey())
                                        .stock(productEntry.getValue().getStock())
                                        .name(productEntry.getValue().getName())
                                        .price(productEntry.getValue().getPrice())
                                        .stock(productEntry.getValue().getStock())
                                        .expiration(productEntry.getValue().getExpiration())
                                        .build(),
                                (existing, replacement) -> existing,
                                LinkedHashMap::new

                        ));
        return DispenserResponse.builder()
                .id(dispenser.getId())
                .products(productsResponse)
                .dispenserMoney(dispenser.getDispenserMoney())
                .status(dispenser.getStatus())
                .build();
    }
}
