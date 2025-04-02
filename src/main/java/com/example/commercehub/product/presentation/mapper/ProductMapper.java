package com.example.commercehub.product.presentation.mapper;


import com.example.commercehub.product.domain.Product;
import com.example.commercehub.product.presentation.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
}
