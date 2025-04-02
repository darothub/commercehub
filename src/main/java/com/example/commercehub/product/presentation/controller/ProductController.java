package com.example.commercehub.product.presentation.controller;

import com.example.commercehub.product.application.ProductService;
import com.example.commercehub.product.presentation.dto.CreateProductDTO;
import com.example.commercehub.product.presentation.dto.ProductDTO;
import com.example.commercehub.product.presentation.dto.UpdateProductDTO;
import com.example.commercehub.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management operations")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of products")
    public ApiResponse<List<ProductDTO>> getAll() {
        log.info("Fetching paginated products...");
        return ApiResponse.of(productService.getAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get product by ID")
    public ApiResponse<ProductDTO> getById(@PathVariable @NotNull UUID id) {
        log.info("Fetching product with ID: {}", id);
        return ApiResponse.of(productService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product")
    public ApiResponse<ProductDTO> create(@Valid @RequestBody CreateProductDTO dto) {
        log.info("Creating new product with name: {}", dto.name());
        return ApiResponse.of(productService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ApiResponse<ProductDTO> update(@PathVariable UUID id, @Valid @RequestBody UpdateProductDTO dto) {
        log.info("Updating product with ID: {}", id);
        return ApiResponse.of(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a product")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        log.info("Deleting product with ID: {}", id);
        productService.delete(id);
        return ApiResponse.of("Product deleted successfully");
    }
}
