package com.example.commercehub.order.presentation.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.commercehub.order.application.service.OrderService;
import com.example.commercehub.order.domain.OrderStatus;
import com.example.commercehub.order.presentation.dto.CreateOrderDTO;
import com.example.commercehub.order.presentation.dto.OrderDTO;
import com.example.commercehub.order.presentation.dto.UpdateOrderDTO;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@WebMvcTest(OrderController.class)
class OrderControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private final UUID testOrderId = UUID.randomUUID();
    private final OrderDTO testOrderDto = new OrderDTO(
            testOrderId,
            "Me",
            "me@example.com",
            BigDecimal.valueOf(10.0),
            Instant.now(),
            OrderStatus.CREATED
    );

    @Test
    void getAllOrders_shouldReturn200WithOrderList() throws Exception {

        given(orderService.getAllOrders()).willReturn(List.of(testOrderDto));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testOrderId.toString()))
                .andExpect(jsonPath("$.data[0].customerName").value("Me"));
    }

    @Test
    void getOrderById_shouldReturn200WithOrder() throws Exception {

        given(orderService.getOrderById(testOrderId)).willReturn(testOrderDto);

        mockMvc.perform(get("/api/v1/orders/{id}", testOrderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(testOrderId.toString()))
                .andExpect(jsonPath("$.data.customerEmail").value("me@example.com"));
    }

    @Test
    void getOrderById_withInvalidId_shouldReturn404() throws Exception {

        given(orderService.getOrderById(testOrderId))
                .willThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/{id}", testOrderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void createOrder_shouldReturn201WithCreatedOrder() throws Exception {

        CreateOrderDTO createDto = new CreateOrderDTO("You", "you@example.com", BigDecimal.valueOf(10.0));
        given(orderService.createOrder(any(CreateOrderDTO.class))).willReturn(testOrderDto);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.status").value("CREATED"));
    }

    @Test
    void createOrder_withInvalidInput_shouldReturn400() throws Exception {

        CreateOrderDTO invalidDto = new CreateOrderDTO("", "invalid-email", BigDecimal.valueOf(10.0));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateOrder_shouldReturn200WithUpdatedOrder() throws Exception {

        UpdateOrderDTO updateDto = new UpdateOrderDTO("You", "you@example.com", OrderStatus.PROCESSING);
        OrderDTO updatedDto = new OrderDTO(
                testOrderId,
                "You",
                "you@example.com",
                testOrderDto.totalPrice(),
                testOrderDto.orderDate(),
                OrderStatus.PROCESSING
        );

        given(orderService.updateOrder(eq(testOrderId), any(UpdateOrderDTO.class))).willReturn(updatedDto);

        mockMvc.perform(put("/api/v1/orders/{id}", testOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerName").value("You"))
                .andExpect(jsonPath("$.data.status").value("PROCESSING"));
    }

    @Test
    void updateOrder_withPartialUpdate_shouldReturn200() throws Exception {

        UpdateOrderDTO partialUpdate = new UpdateOrderDTO(null, "new@example.com", null);
        OrderDTO updatedDto = new OrderDTO(
                testOrderId,
                testOrderDto.customerName(),
                "new@example.com",
                testOrderDto.totalPrice(),
                testOrderDto.orderDate(),
                OrderStatus.PROCESSING
        );

        given(orderService.updateOrder(eq(testOrderId), any(UpdateOrderDTO.class))).willReturn(updatedDto);

        mockMvc.perform(put("/api/v1/orders/{id}", testOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerName").value("Me")) // unchanged
                .andExpect(jsonPath("$.data.customerEmail").value("new@example.com")); // updated
    }

    @Test
    void deleteOrder_shouldReturn200WithSuccessMessage() throws Exception {


        mockMvc.perform(delete("/api/v1/orders/{id}", testOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Order deleted successfully"));
    }

    @Test
    void deleteOrder_withInvalidId_shouldReturn404() throws Exception {

        doThrow(new ResourceNotFoundException("Order not found"))
                .when(orderService).deleteOrder(testOrderId);

        mockMvc.perform(delete("/api/v1/orders/{id}", testOrderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }


}