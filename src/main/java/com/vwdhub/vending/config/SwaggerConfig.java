package com.vwdhub.vending.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "VW:DHUB Vending Machine API",
                version = "v1",
                description = "API REST to manage a vending machine"
        )
)
public class SwaggerConfig {
}
