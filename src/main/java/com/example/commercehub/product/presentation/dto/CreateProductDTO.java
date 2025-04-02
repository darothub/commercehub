package com.example.commercehub.product.presentation.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductDTO(
        @NotBlank(message = "is required")
        String name,
        String description,
        @NotNull(message = "is required")
        @PositiveOrZero(message = "must be zero or positive")
        BigDecimal price,
        @NotNull(message = "is required")
        @Min(value = 1, message = "must be at least 1")
        Integer stock
) {}
