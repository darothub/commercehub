package com.example.commercehub.product.presentation.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductDTO(
        @NotBlank(message = "Name is required")
        String name,
        String description,
        @NotNull(message = "Price is required")
        @Min(value = 1, message = "Price must be at least 1")
        BigDecimal price,
        @NotNull(message = "Stock is required")
        @Min(value = 1, message = "must be at least 1")
        Integer stock
) {}
