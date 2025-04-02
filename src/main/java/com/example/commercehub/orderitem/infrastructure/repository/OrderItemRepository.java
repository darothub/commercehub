package com.example.commercehub.orderitem.infrastructure.repository;

import com.example.commercehub.orderitem.infrastructure.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
}
