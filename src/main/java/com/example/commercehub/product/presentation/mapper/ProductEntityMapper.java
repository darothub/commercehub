package com.example.commercehub.product.presentation.mapper;


import com.example.commercehub.product.domain.Product;
import com.example.commercehub.product.infrastructure.entity.ProductEntity;
import com.example.commercehub.product.presentation.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    Product toDomain(ProductEntity entity);
    ProductDTO toDTO(ProductEntity entity);
}
