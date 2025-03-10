package com.billing.invoice.configs;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Billing system",
                description = "API for operations with billing",
                version = "1.0.0"
        ),
        servers = {@Server(url = "/api")}
)
public class SwaggerConfig {
}