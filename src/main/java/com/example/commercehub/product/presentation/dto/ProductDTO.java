
package com.example.commercehub.product.presentation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Instant createdAt,
        Instant updatedAt
) {}
