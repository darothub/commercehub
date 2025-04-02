package com.example.commercehub.orderitem.application;

import com.example.commercehub.orderitem.infrastructure.entity.OrderItemEntity;
import com.example.commercehub.orderitem.infrastructure.repository.OrderItemRepository;
import com.example.commercehub.orderitem.presentation.dto.CreateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.OrderItemDTO;
import com.example.commercehub.orderitem.presentation.dto.UpdateOrderItemDTO;
import com.example.commercehub.orderitem.presentation.mapper.OrderItemDomainMapper;
import com.example.commercehub.orderitem.presentation.mapper.OrderItemEntityMapper;
import com.example.commercehub.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemEntityMapper orderItemEntityMapper;

    @Mock
    private OrderItemDomainMapper orderItemDomainMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private UUID testOrderItemId;
    private UUID testOrderId;
    private UUID testProductId;
    private OrderItemEntity testOrderItemEntity;
    private OrderItemDTO testOrderItemDTO;
    private CreateOrderItemDTO testCreateOrderItemDTO;
    private UpdateOrderItemDTO testUpdateOrderItemDTO;

    @BeforeEach
    void setUp() {
        testOrderItemId = UUID.randomUUID();
        testOrderId = UUID.randomUUID();
        testProductId = UUID.randomUUID();

        testOrderItemEntity = OrderItemEntity.builder()
                .id(testOrderItemId)
                .orderId(testOrderId)
                .productId(testProductId)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(19.99))
                .build();

        testOrderItemDTO = new OrderItemDTO(
                testOrderItemId,
                testOrderId,
                testProductId,
                2,
                BigDecimal.valueOf(19.99));

        testCreateOrderItemDTO = new CreateOrderItemDTO(
                testOrderId,
                testProductId,
                2,
                BigDecimal.valueOf(19.99));

        testUpdateOrderItemDTO = new UpdateOrderItemDTO(
                testOrderItemId,
                testOrderId,
                testProductId,
                3,
                BigDecimal.valueOf(24.99));
    }


    @Test
    void getAllOrderItems_shouldReturnListOfOrderItemDTOs() {

        given(orderItemRepository.findAll()).willReturn(List.of(testOrderItemEntity));
        given(orderItemEntityMapper.toDTO(testOrderItemEntity)).willReturn(testOrderItemDTO);


        List<OrderItemDTO> result = orderItemService.getAllOrderItems();


        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testOrderItemDTO);
        verify(orderItemRepository).findAll();
        verify(orderItemEntityMapper).toDTO(testOrderItemEntity);
    }

    @Test
    void createOrderItem_shouldMapAndSaveNewOrderItem() {

        given(orderItemDomainMapper.toEntity(testCreateOrderItemDTO)).willReturn(testOrderItemEntity);
        given(orderItemRepository.saveAndFlush(testOrderItemEntity)).willReturn(testOrderItemEntity);
        given(orderItemEntityMapper.toDTO(testOrderItemEntity)).willReturn(testOrderItemDTO);

        OrderItemDTO result = orderItemService.createOrder(testCreateOrderItemDTO);

        assertThat(result).isEqualTo(testOrderItemDTO);
        verify(orderItemDomainMapper).toEntity(testCreateOrderItemDTO);
        verify(orderItemRepository).saveAndFlush(testOrderItemEntity);
        verify(orderItemEntityMapper).toDTO(testOrderItemEntity);
    }


    @Test
    void getOrderItemById_withValidId_shouldReturnOrderItemDTO() {
        given(orderItemRepository.findById(testOrderItemId)).willReturn(Optional.of(testOrderItemEntity));
        given(orderItemEntityMapper.toDTO(testOrderItemEntity)).willReturn(testOrderItemDTO);

        OrderItemDTO result = orderItemService.getOrderItemById(testOrderItemId);

        assertThat(result).isEqualTo(testOrderItemDTO);
        verify(orderItemRepository).findById(testOrderItemId);
        verify(orderItemEntityMapper).toDTO(testOrderItemEntity);
    }

    @Test
    void getOrderItemById_withInvalidId_shouldThrowResourceNotFoundException() {
        given(orderItemRepository.findById(testOrderItemId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.getOrderItemById(testOrderItemId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order item not found");
        verify(orderItemRepository).findById(testOrderItemId);
    }

    @Test
    void updateOrderItem_shouldUpdateAndReturnUpdatedOrderItem() {
        OrderItemEntity updatedEntity = OrderItemEntity.builder()
                .id(testOrderItemId)
                .orderId(testOrderId)
                .productId(testProductId)
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(24.99))
                .build();

        OrderItemDTO updatedDTO = new OrderItemDTO(
                testOrderItemId,
                testOrderId,
                testProductId,
                3,
                BigDecimal.valueOf(24.99));

        given(orderItemRepository.findById(testOrderItemId)).willReturn(Optional.of(testOrderItemEntity));
        given(orderItemRepository.saveAndFlush(testOrderItemEntity)).willReturn(updatedEntity);
        given(orderItemEntityMapper.toDTO(updatedEntity)).willReturn(updatedDTO);

        OrderItemDTO result = orderItemService.updateOrderItem(testOrderItemId, testUpdateOrderItemDTO);

        assertThat(result.quantity()).isEqualTo(3);
        assertThat(result.unitPrice()).isEqualByComparingTo("24.99");
        verify(orderItemRepository).saveAndFlush(testOrderItemEntity);
    }

    @Test
    void updateOrderItem_withPartialUpdate_shouldOnlyUpdateProvidedFields() {

        UpdateOrderItemDTO partialUpdate = new UpdateOrderItemDTO(
                testOrderItemId,
                null,
                null,
                5,
                null);

        OrderItemEntity updatedEntity = OrderItemEntity.builder()
                .id(testOrderItemId)
                .orderId(testOrderId)
                .productId(testProductId)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(19.99))
                .build();

        OrderItemDTO updatedDTO = new OrderItemDTO(
                testOrderItemId,
                testOrderId,
                testProductId,
                5,
                BigDecimal.valueOf(19.99));

        given(orderItemRepository.findById(testOrderItemId)).willReturn(Optional.of(testOrderItemEntity));
        given(orderItemRepository.saveAndFlush(testOrderItemEntity)).willReturn(updatedEntity);
        given(orderItemEntityMapper.toDTO(updatedEntity)).willReturn(updatedDTO);

        OrderItemDTO result = orderItemService.updateOrderItem(testOrderItemId, partialUpdate);

        assertThat(result.quantity()).isEqualTo(5);
        assertThat(result.orderId()).isEqualTo(testOrderId);
        assertThat(result.unitPrice()).isEqualByComparingTo("19.99");
    }

    @Test
    void deleteOrderItem_withValidId_shouldDeleteOrderItem() {
        given(orderItemRepository.findById(testOrderItemId)).willReturn(Optional.of(testOrderItemEntity));

        orderItemService.deleteOrderItem(testOrderItemId);

        verify(orderItemRepository).delete(testOrderItemEntity);
    }

    @Test
    void deleteOrderItem_withInvalidId_shouldThrowResourceNotFoundException() {

        given(orderItemRepository.findById(testOrderItemId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.deleteOrderItem(testOrderItemId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order item not found");
        verify(orderItemRepository, never()).delete(any());
    }

}