package com.example.commercehub.orderitem.presentation.controller;

import com.example.commercehub.orderitem.application.OrderItemService;
import com.example.commercehub.orderitem.presentation.dto.CreateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.UpdateOrderItemDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderItemService orderItemService;

    private final UUID orderItemId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    private final OrderItemDTO orderItemDTO = new OrderItemDTO(
            orderItemId, orderId, productId, 2, BigDecimal.valueOf(19.99));

    private final CreateOrderItemDTO createDTO = new CreateOrderItemDTO(
            orderId, productId, 2, BigDecimal.valueOf(19.99));

    private final UpdateOrderItemDTO updateDTO = new UpdateOrderItemDTO(
            orderItemId, orderId, productId, 3, BigDecimal.valueOf(24.99));


    @Test
    void createOrderItem_shouldReturn201() throws Exception {
        given(orderItemService.createOrder(any()))
                .willReturn(orderItemDTO);

        mockMvc.perform(post("/api/v1/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(orderItemId.toString()));
    }

    @Test
    void createOrderItem_withInvalidInput_shouldReturn400() throws Exception {
        CreateOrderItemDTO invalidDTO = new CreateOrderItemDTO(
                null, null, -1, BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getOrderItem_shouldReturn200() throws Exception {
        given(orderItemService.getOrderItemById(orderItemId))
                .willReturn(orderItemDTO);

        mockMvc.perform(get("/api/v1/order-items/{id}", orderItemId)
                        .header("If-None-Match", "\"some-etag\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(2));
    }

    @Test
    void updateOrderItem_shouldReturn200WithUpdatedResource() throws Exception {
        OrderItemDTO updatedDTO = new OrderItemDTO(
                orderItemId, orderId, productId, 3, BigDecimal.valueOf(24.99));

        given(orderItemService.updateOrderItem(eq(orderItemId), any()))
                .willReturn(updatedDTO);

        mockMvc.perform(put("/api/v1/order-items/{id}", orderItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(3));
    }

    @Test
    void deleteOrderItem_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/order-items/{id}", orderItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Order item deleted successfully"));

        verify(orderItemService).deleteOrderItem(orderItemId);
    }
}