package com.example.commercehub.orderitem.application;

import com.example.commercehub.orderitem.presentation.dto.CreateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.UpdateOrderItemDTO;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    List<OrderItemDTO> getAllOrderItems();
    OrderItemDTO createOrder(CreateOrderItemDTO dto);
    OrderItemDTO getOrderItemById(UUID id);
    OrderItemDTO updateOrderItem(UUID id, UpdateOrderItemDTO dto);
    void deleteOrderItem(UUID id);
}
