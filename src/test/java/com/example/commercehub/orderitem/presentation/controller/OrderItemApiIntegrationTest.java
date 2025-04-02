package com.example.commercehub.orderitem.presentation.controller;

import com.example.commercehub.orderitem.application.OrderItemService;
import com.example.commercehub.orderitem.presentation.dto.CreateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.UpdateOrderItemDTO;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderItemApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderItemService orderItemService;

    private final UUID testOrderItemId = UUID.randomUUID();
    private final UUID testOrderId = UUID.randomUUID();
    private final UUID testProductId = UUID.randomUUID();


    private final OrderItemDTO testOrderItemDto = new OrderItemDTO(
            testOrderItemId,
            testOrderId,
            testProductId,
            2,
            BigDecimal.valueOf(19.99));

    private final CreateOrderItemDTO testCreateDto = new CreateOrderItemDTO(
            testOrderId,
            testProductId,
            2,
            BigDecimal.valueOf(19.99));

    private final UpdateOrderItemDTO testUpdateDto = new UpdateOrderItemDTO(
            testOrderItemId,
            testOrderId,
            testProductId,
            3,
            BigDecimal.valueOf(24.99));

    @Test
    void createOrderItem_shouldReturn201WithCreatedOrderItem() throws Exception {

        given(orderItemService.createOrder(any(CreateOrderItemDTO.class)))
                .willReturn(testOrderItemDto);

        mockMvc.perform(post("/api/v1/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(testOrderItemId.toString()))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.unitPrice").value(19.99));
    }

    @Test
    void createOrderItem_withInvalidInput_shouldReturn400() throws Exception {

        CreateOrderItemDTO invalidDto = new CreateOrderItemDTO(
                null,
                testProductId,
                -1,
                BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getAllOrderItems_shouldReturn200WithOrderItemsList() throws Exception {

        given(orderItemService.getAllOrderItems())
                .willReturn(List.of(testOrderItemDto));

        mockMvc.perform(get("/api/v1/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testOrderItemId.toString()))
                .andExpect(jsonPath("$.data[0].orderId").value(testOrderId.toString()));
    }

    @Test
    void getOrderItemById_shouldReturn200WithOrderItem() throws Exception {

        given(orderItemService.getOrderItemById(testOrderItemId))
                .willReturn(testOrderItemDto);

        mockMvc.perform(get("/api/v1/order-items/{id}", testOrderItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testOrderItemId.toString()))
                .andExpect(jsonPath("$.data.productId").value(testProductId.toString()));
    }

    @Test
    void getOrderItemById_withInvalidId_shouldReturn404() throws Exception {

        given(orderItemService.getOrderItemById(testOrderItemId))
                .willThrow(new ResourceNotFoundException("Order item not found"));

        mockMvc.perform(get("/api/v1/order-items/{id}", testOrderItemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order item not found"));
    }

    @Test
    void updateOrderItem_shouldReturn200WithUpdatedOrderItem() throws Exception {

        OrderItemDTO updatedDto = new OrderItemDTO(
                testOrderItemId,
                testOrderId,
                testProductId,
                3,
                BigDecimal.valueOf(24.99));

        given(orderItemService.updateOrderItem(eq(testOrderItemId), any(UpdateOrderItemDTO.class)))
                .willReturn(updatedDto);

        mockMvc.perform(put("/api/v1/order-items/{id}", testOrderItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(3))
                .andExpect(jsonPath("$.data.unitPrice").value(24.99));
    }

    @Test
    void updateOrderItem_withInvalidInput_shouldReturn400() throws Exception {

        UpdateOrderItemDTO invalidDto = new UpdateOrderItemDTO(
                testOrderItemId,
                null,
                null,
                -1,
                BigDecimal.ZERO);

        mockMvc.perform(put("/api/v1/order-items/{id}", testOrderItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteOrderItem_shouldReturn200WithSuccessMessage() throws Exception {

        mockMvc.perform(delete("/api/v1/order-items/{id}", testOrderItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Order item deleted successfully"));

        verify(orderItemService).deleteOrderItem(testOrderItemId);
    }

    @Test
    void deleteOrderItem_withInvalidId_shouldReturn404() throws Exception {

        willThrow(new ResourceNotFoundException("Order item not found"))
                .given(orderItemService).deleteOrderItem(testOrderItemId);

        mockMvc.perform(delete("/api/v1/order-items/{id}", testOrderItemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order item not found"));
    }
}