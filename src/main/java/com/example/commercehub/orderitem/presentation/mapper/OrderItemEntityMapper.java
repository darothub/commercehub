package com.example.commercehub.orderitem.presentation.mapper;


import com.example.commercehub.orderitem.infrastructure.entity.OrderItemEntity;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemEntityMapper {
    OrderItemDTO toDTO(OrderItemEntity entity);
}