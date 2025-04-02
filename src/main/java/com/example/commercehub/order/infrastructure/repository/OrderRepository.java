package com.example.commercehub.order.infrastructure.repository;

import com.example.commercehub.order.infrastructure.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
