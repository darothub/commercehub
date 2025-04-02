package com.example.commercehub.product.presentation.mapper;


import com.example.commercehub.product.infrastructure.entity.ProductEntity;
import com.example.commercehub.product.presentation.dto.CreateProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDomainMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(CreateProductDTO dto);
}
