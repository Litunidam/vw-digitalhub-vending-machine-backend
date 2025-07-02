package com.vwdhub.vending.infrastructure.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwdhub.vending.application.usecase.AddDispenserUseCase;
import com.vwdhub.vending.application.usecase.GetDispenserStatusUseCase;
import com.vwdhub.vending.application.usecase.GetDispenserUseCase;
import com.vwdhub.vending.application.usecase.PurchaseProductUseCase;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.model.*;
import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.CreateDispenserRequestDto;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.PurchaseRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DispenserController.class)
class DispenserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AddDispenserUseCase addDispenserUseCase;

    @MockBean
    private GetDispenserUseCase getDispenserUseCase;

    @MockBean
    private PurchaseProductUseCase purchaseProductUseCase;

    @MockBean
    private GetDispenserStatusUseCase getDispenserStatusUseCase;

    @Test
    @DisplayName("POST /dispenser → 200")
    void createDispenser() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Coke")
                .price(new BigDecimal("1.20"))
                .expiration(LocalDate.now().plusDays(5))
                .stock(10)
                .build();
        Money money = Money.builder().coins(Map.of(Coin.EUR_1, 2)).build();
        Dispenser returned = new Dispenser(id, Map.of(product.getId(), product), money);
        returned.setStatus(DispenserStatus.AVAILABLE);

        when(addDispenserUseCase.add(
                eq(id),
                anyList(),
                any(Money.class),
                eq(DispenserStatus.AVAILABLE))
        ).thenReturn(returned);

        CreateDispenserRequestDto dispenserRequest = CreateDispenserRequestDto.builder()
                .id(id)
                .products(new ArrayList<>())
                .initialCoins(money)
                .status(DispenserStatus.AVAILABLE)
                .build();

        mvc.perform(post("/api/v1/vending/dispenser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dispenserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.products").isMap())
                .andExpect(jsonPath("$.dispenserMoney.coins.EUR_1").value(2));
    }

    @Test
    @DisplayName("GET /dispenser/{id} → 200")
    void getDispenser() throws Exception {
        UUID dispenserId = UUID.randomUUID();
        Dispenser dispenser = new Dispenser(dispenserId, Map.of(), Money.builder().coins(Map.of()).build());
        dispenser.setStatus(DispenserStatus.OUT_OF_ORDER);

        when(getDispenserUseCase.get(dispenserId)).thenReturn(dispenser);

        mvc.perform(get("/api/v1/vending/dispenser/{id}", dispenserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dispenserId.toString()))
                .andExpect(jsonPath("$.status").value(Constants.OUT_OF_ORDER));
    }

    @Test
    @DisplayName("POST /dispenser/{id}/purchase → 200")
    void purchaseSuccess() throws Exception {
        UUID dispenserId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Map<Coin, Integer> coinsIn = Map.of(Coin.EUR_1, 3);

        Product product = Product.builder()
                .id(productId)
                .name("Sprite")
                .price(new BigDecimal("0.80"))
                .expiration(LocalDate.now().plusDays(2))
                .stock(5)
                .build();
        Money change = Money.builder().coins(Map.of(Coin.CENT_50, 2)).build();
        ChangeAndProduct cap = ChangeAndProduct.builder()
                .product(product)
                .change(change)
                .dispenserMoney(Money.builder().coins(Map.of()).build())
                .build();

        when(purchaseProductUseCase.purchase(
                eq(dispenserId),
                eq(productId),
                any(Money.class),
                eq(true))
        ).thenReturn(cap);

        PurchaseRequestDto purchaseRequest = PurchaseRequestDto.builder()
                .productId(productId)
                .coins(coinsIn)
                .confirmed(true)
                .build();

        mvc.perform(post("/api/v1/vending/dispenser/{id}/purchase", dispenserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id").value(productId.toString()))
                .andExpect(jsonPath("$.change.coins.CENT_50").value(2));
    }

    @Test
    @DisplayName("POST /dispenser/{id}/purchase → 400")
    void purchaseFailure() throws Exception {
        UUID dispenserId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Map<Coin, Integer> coinsIn = Map.of(Coin.EUR_2, 1);

        when(purchaseProductUseCase.purchase(
                eq(dispenserId),
                eq(productId),
                any(Money.class),
                eq(true))
        ).thenThrow(new IllegalArgumentException(Constants.NOT_ENOUGH_MONEY));

        PurchaseRequestDto purchaseRequest = PurchaseRequestDto.builder()
                .productId(productId)
                .coins(coinsIn)
                .confirmed(true)
                .build();

        mvc.perform(post("/api/v1/vending/dispenser/{id}/purchase", dispenserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.NOT_ENOUGH_MONEY))
                .andExpect(jsonPath("$.refund.EUR_2").value(1));
    }

    @Test
    @DisplayName("GET /dispenser/{id}/status → 200")
    void getStatus() throws Exception {
        UUID dispenserId = UUID.randomUUID();
        when(getDispenserStatusUseCase.getDispenserStatus(dispenserId))
                .thenReturn(DispenserStatus.OUT_OF_ORDER);

        mvc.perform(get("/api/v1/vending/dispenser/{id}/status", dispenserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Constants.OUT_OF_ORDER));
    }
}
