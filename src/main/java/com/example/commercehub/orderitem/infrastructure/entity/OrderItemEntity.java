package com.example.commercehub.orderitem.infrastructure.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Version
    @Column(updatable = false)
    private Long version;
    private UUID productId;
    private UUID orderId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
