package com.vwdhub.vending.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SwaggerConfigTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /v3/api-docs → 200 y contiene la info definida en @OpenAPIDefinition")
    void apiDocsExposesOpenApiInfo() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("VW:DHUB Vending Machine API"))
                .andExpect(jsonPath("$.info.version").value("v1"))
                .andExpect(jsonPath("$.info.description")
                        .value("API REST to manage a vending machine"));
    }
}
