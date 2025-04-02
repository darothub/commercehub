
package com.example.commercehub.product.presentation.dto;

import java.math.BigDecimal;

public record UpdateProductDTO(
    String name,
    String description,
    BigDecimal price,
    Integer stock
) {}
