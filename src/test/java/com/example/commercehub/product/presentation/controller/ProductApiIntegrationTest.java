package com.example.commercehub.product.presentation.controller;

import com.example.commercehub.product.application.ProductService;
import com.example.commercehub.product.presentation.dto.CreateProductDTO;
import com.example.commercehub.product.presentation.dto.ProductDTO;
import com.example.commercehub.product.presentation.dto.UpdateProductDTO;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private final UUID testId = UUID.randomUUID();
    private final ProductDTO testProduct = new ProductDTO(
            testId,
            "Test Product",
            "Test Description",
            BigDecimal.valueOf(19.99),
            100,
            Instant.now(),
            Instant.now()
    );

    @Test
    void getAll_shouldReturn200WithProducts() throws Exception {
        when(productService.getAll())
                .thenReturn(List.of(testProduct));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(testId.toString())))
                .andExpect(jsonPath("$.data[0].name", is("Test Product")));
    }

    @Test
    void getById_shouldReturn200WhenProductExists() throws Exception {
        when(productService.getById(testId))
                .thenReturn(testProduct);

        mockMvc.perform(get("/api/v1/products/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(testId.toString())));
    }

    @Test
    void getById_shouldReturn404WhenProductNotFound() throws Exception {
        when(productService.getById(testId))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/{id}", testId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found")));
    }

    @Test
    void create_shouldReturn201WithCreatedProduct() throws Exception {
        CreateProductDTO createDto = new CreateProductDTO(
                "New Product",
                "New Description",
                BigDecimal.valueOf(29.99),
                50
        );

        when(productService.create(any(CreateProductDTO.class)))
                .thenReturn(testProduct);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("Test Product")));
    }

    @Test
    void create_shouldReturn400WhenInvalidInput() throws Exception {

        CreateProductDTO invalidDto = new CreateProductDTO(
                "",
                null,
                BigDecimal.valueOf(-10),
                -5
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturn200WithUpdatedProduct() throws Exception {
        UpdateProductDTO updateDto = new UpdateProductDTO(
                "Updated Name",
                "Updated Description",
                BigDecimal.valueOf(39.99),
                75
        );

        when(productService.update(eq(testId), any(UpdateProductDTO.class)))
                .thenReturn(testProduct);

        mockMvc.perform(put("/api/v1/products/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(testId.toString())));
    }

    @Test
    void update_shouldReturn404WhenProductNotFound() throws Exception {
        UpdateProductDTO updateDto = new UpdateProductDTO(
                "Updated Name",
                null,
                null,
                null
        );

        when(productService.update(eq(testId), any(UpdateProductDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(put("/api/v1/products/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn200WhenSuccessful() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is("Product deleted successfully")));

        Mockito.verify(productService).delete(testId);
    }

    @Test
    void delete_shouldReturn404WhenProductNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Product not found"))
                .when(productService).delete(testId);

        mockMvc.perform(delete("/api/v1/products/{id}", testId))
                .andExpect(status().isNotFound());
    }

}