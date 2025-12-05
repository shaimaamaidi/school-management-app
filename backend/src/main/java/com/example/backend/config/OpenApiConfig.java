package com.example.backend.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(security = {@SecurityRequirement(name = "bearerAuth")})
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {}