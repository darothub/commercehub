package com.example.commercehub.order.application.service;

import com.example.commercehub.order.domain.OrderStatus;
import com.example.commercehub.order.infrastructure.entity.OrderEntity;
import com.example.commercehub.order.infrastructure.repository.OrderRepository;
import com.example.commercehub.order.presentation.dto.CreateOrderDTO;
import com.example.commercehub.order.presentation.dto.OrderDTO;
import com.example.commercehub.order.presentation.dto.UpdateOrderDTO;
import com.example.commercehub.order.presentation.mapper.OrderDomainMapper;
import com.example.commercehub.order.presentation.mapper.OrderEntityMapper;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final OrderDomainMapper orderDomainMapper;

    @Override
    public List<OrderDTO> getAllOrders() {
        log.info("Fetching orders");
        return orderRepository.findAll()
                .stream()
                .map(orderEntityMapper::toDTO)
                .toList();
    }

    @Override
    public OrderDTO getOrderById(UUID id) {
        log.info("Fetching order by id {}", id);
        return orderRepository.findById(id)
                .map(orderEntityMapper::toDTO)
                .orElseThrow(() -> logAndThrow(id));
    }

    @Override
    public OrderDTO createOrder(CreateOrderDTO orderDTO) {
        log.info("Creating order");
        OrderEntity orderEntity = orderDomainMapper.toEntity(orderDTO);
        orderEntity.setStatus(OrderStatus.CREATED);
        orderEntity = orderRepository.save(orderEntity);
        log.info("Order created");
        return orderEntityMapper.toDTO(orderEntity);
    }

    @Override
    public OrderDTO updateOrder(UUID id, UpdateOrderDTO updateOrderDTO) {
        OrderDTO orderDTO = getOrderById(id);
        OrderEntity orderEntity = orderDomainMapper.toEntity(orderDTO);
        log.info("Checking update with id: {}", id);
        if(updateOrderDTO.customerEmail() != null && !updateOrderDTO.customerEmail().isBlank()) orderEntity.setCustomerEmail(updateOrderDTO.customerEmail());
        if (updateOrderDTO.customerName() != null && !updateOrderDTO.customerName().isBlank()) orderEntity.setCustomerName(updateOrderDTO.customerName());
        if (updateOrderDTO.orderStatus() != null) orderEntity.setStatus(updateOrderDTO.orderStatus());
        log.info("Updating order with id: {}", id);
        orderEntity = orderRepository.save(orderEntity);
        log.info("Order with id updated: {}", id);
        return orderEntityMapper.toDTO(orderEntity);
    }

    @Override
    public void deleteOrder(UUID id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> logAndThrow(id));
        orderRepository.delete(order);
        log.info("Deleted order: {}", id);
    }
    private static ResourceNotFoundException logAndThrow(UUID id) {
        log.error("Order with id {} not found", id);
        return new ResourceNotFoundException("Order not found");
    }
}
