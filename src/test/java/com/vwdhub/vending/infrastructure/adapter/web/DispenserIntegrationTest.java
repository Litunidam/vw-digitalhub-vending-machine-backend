package com.vwdhub.vending;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.CreateDispenserRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DispenserIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void createAndGetDispenser() throws Exception {
        CreateDispenserRequestDto createDispenserRequest = CreateDispenserRequestDto.builder()
                .id(UUID.randomUUID())
                .products(Collections.emptyList())
                .initialCoins(Money.builder().coins(Collections.emptyMap()).build())
                .status(DispenserStatus.AVAILABLE)
                .build();

        String json = mvc.perform(post("/api/v1/vending/dispenser")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDispenserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String returnedId = mapper.readTree(json).get("id").asText();

        mvc.perform(get("/api/v1/vending/dispenser/{id}", returnedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedId))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }
}
