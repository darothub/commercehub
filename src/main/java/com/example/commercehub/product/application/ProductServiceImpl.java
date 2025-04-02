package com.example.commercehub.product.application;

import com.example.commercehub.product.infrastructure.entity.ProductEntity;
import com.example.commercehub.product.infrastructure.repository.ProductRepository;
import com.example.commercehub.product.presentation.dto.CreateProductDTO;
import com.example.commercehub.product.presentation.dto.ProductDTO;
import com.example.commercehub.product.presentation.dto.UpdateProductDTO;
import com.example.commercehub.product.presentation.mapper.ProductDomainMapper;
import com.example.commercehub.product.presentation.mapper.ProductEntityMapper;
import com.example.commercehub.shared.exception.InvalidProductUpdateException;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductDomainMapper productDomainMapper;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public List<ProductDTO> getAll() {
        return productRepository.findAll()
                .stream()
                .map(productEntityMapper::toDTO)
                .toList();
    }

    @Override
    public ProductDTO getById(UUID id) {
        return productRepository.findById(id)
                .map(productEntityMapper::toDTO)
                .orElseThrow(() -> logAndThrow(id));
    }

    @Override
    public ProductDTO create(CreateProductDTO dto) {
        ProductEntity entity = productDomainMapper.toEntity(dto);
        ProductEntity saved = productRepository.saveAndFlush(entity);
        log.info("Created product with id {}, {}", saved.getCreatedAt(), saved.getUpdatedAt());
        return productEntityMapper.toDTO(saved);
    }

    @Override
    public ProductDTO update(UUID id, UpdateProductDTO dto) {
        ProductEntity existingEntity = productRepository.findById(id)
                .orElseThrow(() -> logAndThrow(id));

        validateAndUpdate(dto, existingEntity);

        ProductEntity savedEntity = productRepository.saveAndFlush(existingEntity);

        log.info("Updated product with id {}", savedEntity.getId());

        return productEntityMapper.toDTO(savedEntity);
    }

    @Override
    public void delete(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> logAndThrow(id));

        productRepository.delete(product);
        log.info("Deleted product with id {}", id);

    }
    private static void validateAndUpdate(UpdateProductDTO dto, ProductEntity existingEntity) {
        if (dto.stock() != null && dto.stock() < 0) {
            throw new InvalidProductUpdateException("Stock cannot be negative");
        }

        if (dto.price() != null && dto.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductUpdateException("Price cannot be negative");
        }

        if (dto.name() != null) existingEntity.setName(dto.name());
        if (dto.description() != null) existingEntity.setDescription(dto.description());
        if (dto.price() != null && dto.price().compareTo(BigDecimal.ZERO) >= 0)  existingEntity.setPrice(dto.price());
        if (dto.stock() != null) existingEntity.setStock(dto.stock());
    }
    private static ResourceNotFoundException logAndThrow(UUID id) {
        log.error("Product with id {} not found", id);
        return new ResourceNotFoundException("Product not found");
    }

}
