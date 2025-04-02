package com.example.commercehub.order.presentation.mapper;

import com.example.commercehub.order.infrastructure.entity.OrderEntity;
import com.example.commercehub.order.presentation.dto.CreateOrderDTO;
import com.example.commercehub.order.presentation.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDomainMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    OrderEntity toEntity(CreateOrderDTO dto);
    @Mapping(target = "version", ignore = true)
    OrderEntity toEntity(OrderDTO dto);
}