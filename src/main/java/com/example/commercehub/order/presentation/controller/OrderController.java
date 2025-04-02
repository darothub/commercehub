package com.example.commercehub.order.presentation.controller;

import com.example.commercehub.order.application.service.OrderService;
import com.example.commercehub.order.presentation.dto.CreateOrderDTO;
import com.example.commercehub.order.presentation.dto.OrderDTO;
import com.example.commercehub.order.presentation.dto.UpdateOrderDTO;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management operations")
@Validated
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all orders list")
    public ApiResponse<List<OrderDTO>> getAll() {
        return ApiResponse.of(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get order by ID")
    public ApiResponse<OrderDTO> getById(@PathVariable UUID id) {
        return ApiResponse.of(orderService.getOrderById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order")
    public ApiResponse<OrderDTO> create(@Valid @RequestBody CreateOrderDTO dto) {
        return ApiResponse.of(orderService.createOrder(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order")
    public ApiResponse<OrderDTO> update(@PathVariable UUID id, @Valid @RequestBody UpdateOrderDTO dto) {
        return ApiResponse.of(orderService.updateOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete an order")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ApiResponse.of("Order deleted successfully");
    }
}