package com.example.commercehub.order.presentation.mapper;


import com.example.commercehub.order.infrastructure.entity.OrderEntity;
import com.example.commercehub.order.presentation.dto.OrderDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderEntityMapper {

    OrderDTO toDTO(OrderEntity entity);
}
