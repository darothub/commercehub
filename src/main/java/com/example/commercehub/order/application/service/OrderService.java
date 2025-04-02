package com.example.commercehub.order.application.service;

import com.example.commercehub.order.presentation.dto.CreateOrderDTO;
import com.example.commercehub.order.presentation.dto.OrderDTO;
import com.example.commercehub.order.presentation.dto.UpdateOrderDTO;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(UUID id);
    OrderDTO createOrder(CreateOrderDTO orderDTO);
    OrderDTO updateOrder(UUID id, UpdateOrderDTO orderDTO);
    void deleteOrder(UUID id);
}
