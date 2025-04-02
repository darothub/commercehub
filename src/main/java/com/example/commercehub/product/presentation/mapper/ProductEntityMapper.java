package com.example.commercehub.product.presentation.mapper;


import com.example.commercehub.product.infrastructure.entity.ProductEntity;
import com.example.commercehub.product.presentation.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    ProductDTO toDTO(ProductEntity entity);
}
