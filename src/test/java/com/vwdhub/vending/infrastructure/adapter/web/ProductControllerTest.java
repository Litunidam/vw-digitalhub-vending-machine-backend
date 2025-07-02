package com.vwdhub.vending.infrastructure.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwdhub.vending.application.usecase.AddProductUseCase;
import com.vwdhub.vending.application.usecase.GetProductStockUseCase;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.CreateProductRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AddProductUseCase addProductUseCase;

    @MockBean
    private GetProductStockUseCase getProductStockUseCase;

    @Test
    @DisplayName("POST /api/v1/vending/product → 200")
    void createProduct() throws Exception {
        UUID dispenserId = UUID.randomUUID();
        UUID returnedProductId = UUID.randomUUID();
        String name = "Orange Juice";
        BigDecimal price = new BigDecimal("2.50");
        LocalDate expiration = LocalDate.now().plusDays(30);
        int stock = 25;

        Product returned = Product.builder()
                .id(returnedProductId)
                .name(name)
                .price(price)
                .expiration(expiration)
                .stock(stock)
                .build();

        when(addProductUseCase.add(
                eq(dispenserId),
                eq(name),
                eq(price),
                eq(expiration),
                eq(stock))
        ).thenReturn(returned);

        CreateProductRequestDto dto = CreateProductRequestDto.builder()
                .dispenserId(dispenserId)
                .name(name)
                .price(price)
                .expiration(expiration)
                .stock(stock)
                .build();

        mvc.perform(post("/api/v1/vending/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedProductId.toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.price").value(price.doubleValue()))
                .andExpect(jsonPath("$.expirationDate").value(expiration.toString()))
                .andExpect(jsonPath("$.stock").value(stock));
    }

    @Test
    @DisplayName("GET /api/v1/vending/product/{id}/stock → 200")
    void getProductStock() throws Exception {
        UUID productId = UUID.randomUUID();
        int currentStock = 42;

        when(getProductStockUseCase.getProduct(productId)).thenReturn(currentStock);

        mvc.perform(get("/api/v1/vending/product/{id}/stock", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(currentStock));
    }
}
