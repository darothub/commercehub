
package com.example.commercehub.product.presentation.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record UpdateProductDTO(
    String name,
    String description,
    BigDecimal price,
    @Min(value = 0, message = "can not be negative")
    Integer stock
) {}
