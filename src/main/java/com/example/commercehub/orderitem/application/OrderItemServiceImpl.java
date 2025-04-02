package com.example.commercehub.orderitem.application;

import com.example.commercehub.orderitem.infrastructure.entity.OrderItemEntity;
import com.example.commercehub.orderitem.infrastructure.repository.OrderItemRepository;
import com.example.commercehub.orderitem.presentation.dto.CreateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.UpdateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.mapper.OrderItemDomainMapper;
import com.example.commercehub.orderitem.presentation.mapper.OrderItemEntityMapper;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemEntityMapper orderItemEntityMapper;
    private final OrderItemDomainMapper orderItemDomainMapper;

    @Override
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findAll()
                .stream().map(orderItemEntityMapper::toDTO).toList();
    }

    @Transactional
    @Override
    public OrderItemDTO createOrder(CreateOrderItemDTO dto) {
        OrderItemEntity orderItemEntity = orderItemDomainMapper.toEntity(dto);
        orderItemEntity = orderItemRepository.saveAndFlush(orderItemEntity);

        return orderItemEntityMapper.toDTO(orderItemEntity);
    }

    @Override
    public OrderItemDTO getOrderItemById(UUID id) {
        log.info("Fetching order item by id {}", id);
        return orderItemRepository.findById(id)
                .map(orderItemEntityMapper::toDTO)
                .orElseThrow(() -> logAndThrow(id));
    }
    @Transactional
    @Override
    public OrderItemDTO updateOrderItem(UUID id, UpdateOrderItemDTO dto) {
        OrderItemDTO orderItemDTO = getOrderItemById(id);
        OrderItemEntity orderItemEntity = orderItemDomainMapper.toEntity(orderItemDTO);
        log.info("Updating order with id: {}", id);
        orderItemEntity = orderItemRepository.saveAndFlush(orderItemEntity);
        log.info("Order item with id updated: {}", id);
        return orderItemEntityMapper.toDTO(orderItemEntity);
    }


    @Override
    public void deleteOrderItem(UUID id) {
        OrderItemEntity item = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        orderItemRepository.delete(item);
        log.info("Deleted OrderItem: {}", id);
    }
    private static ResourceNotFoundException logAndThrow(UUID id) {
        log.error("Order item with id {} not found", id);
        return new ResourceNotFoundException("Order item not found");
    }
}
