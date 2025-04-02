package com.example.commercehub.orderitem.presentation.controller;

import com.example.commercehub.orderitem.application.OrderItemService;
import com.example.commercehub.orderitem.presentation.dto.CreateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.UpdateOrderItemDTO;
import com.example.commercehub.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
@Tag(name = "Order-items", description = "Order items management operations")
@Validated
public class OrderItemController {

    private final OrderItemService service;

    @PostMapping
    @Operation(summary = "Create a new order-item")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderItemDTO> create(@Valid @RequestBody CreateOrderItemDTO dto) {
        log.info("Creating order item...");
        return ApiResponse.of(service.createOrder(dto));
    }
    @GetMapping
    @Operation(summary = "Get all order-items list")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<OrderItemDTO>> getAll() {
        log.info("Fetching all order items...");
        return ApiResponse.of(service.getAllOrderItems());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order-item by ID")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<OrderItemDTO> getById(@PathVariable UUID id) {
        log.info("Fetching order item with ID {}", id);
        return ApiResponse.of(service.getOrderItemById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order-item")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<OrderItemDTO> update(@PathVariable UUID id, @Valid @RequestBody UpdateOrderItemDTO dto) {
        log.info("Updating order item with ID {}", id);
        return ApiResponse.of(service.updateOrderItem(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order-item")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> delete(@PathVariable UUID id) {
        log.info("Deleting order item with ID {}", id);
        service.deleteOrderItem(id);
        return ApiResponse.of("Order item deleted successfully");
    }
}