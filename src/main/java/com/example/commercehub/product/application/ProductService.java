package com.example.commercehub.product.application;

import com.example.commercehub.product.presentation.dto.CreateProductDTO;
import com.example.commercehub.product.presentation.dto.ProductDTO;
import com.example.commercehub.product.presentation.dto.UpdateProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDTO> getAll();
    ProductDTO getById(UUID id);
    ProductDTO create(CreateProductDTO dto);
    ProductDTO update(UUID id, UpdateProductDTO dto);
    void delete(UUID id);
}
