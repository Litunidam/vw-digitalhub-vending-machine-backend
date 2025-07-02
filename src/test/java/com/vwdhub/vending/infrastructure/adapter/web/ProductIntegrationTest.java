package com.vwdhub.vending.infrastructure.adapter.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.CreateProductRequestDto;
import com.vwdhub.vending.infrastructure.persistence.entity.DispenserEntity;
import com.vwdhub.vending.infrastructure.persistence.entity.DispenserStatusEntity;
import com.vwdhub.vending.infrastructure.persistence.repository.DispenserSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class ProductIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DispenserSpringDataRepository dispenserRepository;

    private UUID dispenserId;

    @BeforeEach
    void setUp() {
        // Creamos un dispensador vacío para que exista la FK
        DispenserEntity de = DispenserEntity.builder()
                .status(DispenserStatusEntity.AVAILABLE)
                .products(new ArrayList<>())
                .dispenserMoney(new ArrayList<>())
                .build();
        de = dispenserRepository.save(de);
        dispenserId = de.getId();
    }

    @Test
    void createAndGetProductStock() throws Exception {

        CreateProductRequestDto createProductRequest = CreateProductRequestDto.builder()
                .dispenserId(dispenserId)
                .name("Fanta")
                .price(new BigDecimal("2.50"))
                .expiration(LocalDate.parse("2025-07-31"))
                .stock(25)
                .build();

        String json = mvc.perform(post("/api/v1/vending/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Fanta"))
                .andExpect(jsonPath("$.price").value(2.50))
                .andExpect(jsonPath("$.stock").value(25))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = mapper.readTree(json);
        String productId = node.get("id").asText();

        mvc.perform(get("/api/v1/vending/product/{id}/stock", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(25));
    }
}
