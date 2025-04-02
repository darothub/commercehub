package com.example.commercehub.orderitem.presentation.mapper;

import com.example.commercehub.orderitem.infrastructure.entity.OrderItemEntity;
import com.example.commercehub.orderitem.presentation.dto.CreateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemDomainMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    OrderItemEntity toEntity(CreateOrderItemDTO createOrderItemDTO);
    @Mapping(target = "version", ignore = true)
    OrderItemEntity toEntity(OrderItemDTO createOrderItemDTO);
}
