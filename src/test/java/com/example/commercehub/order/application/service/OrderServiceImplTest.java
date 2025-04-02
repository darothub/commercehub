package com.example.commercehub.order.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.commercehub.order.domain.OrderStatus;
import com.example.commercehub.order.infrastructure.entity.OrderEntity;
import com.example.commercehub.order.infrastructure.repository.OrderRepository;
import com.example.commercehub.order.presentation.dto.CreateOrderDTO;
import com.example.commercehub.order.presentation.dto.OrderDTO;
import com.example.commercehub.order.presentation.dto.UpdateOrderDTO;
import com.example.commercehub.order.presentation.mapper.OrderDomainMapper;
import com.example.commercehub.order.presentation.mapper.OrderEntityMapper;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntityMapper orderEntityMapper;

    @Mock
    private OrderDomainMapper orderDomainMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID testOrderId;
    private OrderEntity testOrderEntity;
    private OrderDTO testOrderDTO;
    private CreateOrderDTO testCreateOrderDTO;
    private UpdateOrderDTO testUpdateOrderDTO;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
        testOrderEntity = OrderEntity.builder()
                .id(testOrderId)
                .customerName("Me")
                .customerEmail("me@example.com")
                .status(OrderStatus.CREATED)
                .build();

        testOrderDTO = new OrderDTO(testOrderId, "Me", "me@example.com", BigDecimal.valueOf(10.0), Instant.now(), OrderStatus.CREATED);

        testCreateOrderDTO = new CreateOrderDTO("Me", "me@example.com", BigDecimal.valueOf(10.0));
        testUpdateOrderDTO = new UpdateOrderDTO("You", "you@example.com", BigDecimal.valueOf(10.0), OrderStatus.PROCESSING);
    }

    @Test
    void getAllOrders_shouldReturnListOfOrderDTOs() {

        when(orderRepository.findAll()).thenReturn(List.of(testOrderEntity));
        when(orderEntityMapper.toDTO(testOrderEntity)).thenReturn(testOrderDTO);

        List<OrderDTO> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrderDTO);
        verify(orderRepository).findAll();
        verify(orderEntityMapper).toDTO(testOrderEntity);
    }

    @Test
    void getOrderById_withValidId_shouldReturnOrderDTO() {

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrderEntity));
        when(orderEntityMapper.toDTO(testOrderEntity)).thenReturn(testOrderDTO);


        OrderDTO result = orderService.getOrderById(testOrderId);

        assertThat(result).isEqualTo(testOrderDTO);
        verify(orderRepository).findById(testOrderId);
        verify(orderEntityMapper).toDTO(testOrderEntity);
    }

    @Test
    void getOrderById_withInvalidId_shouldThrowException() {

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(testOrderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");
        verify(orderRepository).findById(testOrderId);
    }

    @Test
    void createOrder_shouldMapAndSaveNewOrder() {

        when(orderDomainMapper.toEntity(testCreateOrderDTO)).thenReturn(testOrderEntity);
        when(orderRepository.save(testOrderEntity)).thenReturn(testOrderEntity);
        when(orderEntityMapper.toDTO(testOrderEntity)).thenReturn(testOrderDTO);

        OrderDTO result = orderService.createOrder(testCreateOrderDTO);


        assertThat(result).isEqualTo(testOrderDTO);
        assertThat(testOrderEntity.getStatus()).isEqualTo(OrderStatus.CREATED);
        verify(orderDomainMapper).toEntity(testCreateOrderDTO);
        verify(orderRepository).save(testOrderEntity);
        verify(orderEntityMapper).toDTO(testOrderEntity);
    }

    @Test
    void updateOrder_withValidId_shouldUpdateFieldsAndSave() {

        OrderEntity updatedEntity = OrderEntity.builder()
                .id(testOrderId)
                .customerName("Me")
                .customerEmail("me@example.com")
                .status(OrderStatus.PROCESSING)
                .build();

        OrderDTO updatedDTO = new OrderDTO(
                testOrderId,
                "Me",
                "me@example.com",
                BigDecimal.valueOf(10.0),
                Instant.now(),
                OrderStatus.PROCESSING
        );

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrderEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(updatedEntity);
        when(orderEntityMapper.toDTO(updatedEntity)).thenReturn(updatedDTO);

        OrderDTO result = orderService.updateOrder(testOrderId, testUpdateOrderDTO);

        assertThat(result.customerName()).isEqualTo("Me");
        assertThat(result.customerEmail()).isEqualTo("me@example.com");
        assertThat(result.status()).isEqualTo(OrderStatus.PROCESSING);

        verify(orderRepository).findById(testOrderId);
        verify(orderRepository).save(testOrderEntity);
        verify(orderEntityMapper, times(1)).toDTO(any());
    }

    @Test
    void updateOrder_withPartialUpdates_shouldOnlyUpdateProvidedFields() {

        UpdateOrderDTO partialUpdate = new UpdateOrderDTO(null, "newme@example.com", BigDecimal.valueOf(10.0),null);

        OrderEntity updatedEntity = OrderEntity.builder()
                .id(testOrderId)
                .customerName("Me")
                .customerEmail("newme@example.com")
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrderEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(updatedEntity);
        when(orderEntityMapper.toDTO(updatedEntity)).thenReturn(
               new OrderDTO(
                        testOrderId,
                        "Me",
                        "newme@example.com",
                        BigDecimal.valueOf(10.0),
                        Instant.now(),
                        OrderStatus.PROCESSING
                )
        );


        OrderDTO result = orderService.updateOrder(testOrderId, partialUpdate);


        assertThat(result.customerName()).isEqualTo("Me");
        assertThat(result.customerEmail()).isEqualTo("newme@example.com");
        assertThat(result.status()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void deleteOrder_withValidId_shouldDeleteOrder() {

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrderEntity));


        orderService.deleteOrder(testOrderId);

        verify(orderRepository).findById(testOrderId);
        verify(orderRepository).delete(testOrderEntity);
    }

    @Test
    void deleteOrder_withInvalidId_shouldThrowException() {

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> orderService.deleteOrder(testOrderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");
        verify(orderRepository).findById(testOrderId);
        verify(orderRepository, never()).delete(any());
    }
}