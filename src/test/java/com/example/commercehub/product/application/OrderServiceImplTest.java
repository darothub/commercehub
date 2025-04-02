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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDomainMapper productDomainMapper;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID testId;
    private ProductEntity testEntity;
    private ProductDTO testDTO;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testEntity = ProductEntity.builder()
                .id(testId)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(19.99))
                .stock(100)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        testDTO = new ProductDTO(
                testId,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(19.99),
                100, Instant.now(),
                Instant.now()
        );
    }

    // Helper method
    private CreateProductDTO createTestCreateDTO() {
        return new CreateProductDTO(
                "New Product",
                "New Description",
                BigDecimal.valueOf(29.99),
                50
        );
    }

    private UpdateProductDTO createTestUpdateDTO() {
        return new UpdateProductDTO(
                "Updated Name",
                "Updated Description",
                BigDecimal.valueOf(39.99),
                75
        );
    }

    @Test
    void getAll_shouldReturnListOfProducts_whenProductsExist() {

        when(productRepository.findAll()).thenReturn(List.of(testEntity));
        when(productEntityMapper.toDTO(testEntity)).thenReturn(testDTO);

        List<ProductDTO> result = productService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testDTO);
        verify(productRepository).findAll();
        verify(productEntityMapper).toDTO(testEntity);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoProductsExist() {

        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTO> result = productService.getAll();

        assertThat(result).isEmpty();
        verify(productRepository).findAll();
        verifyNoInteractions(productEntityMapper);
    }

    @Test
    void getById_shouldReturnProduct_whenProductExists() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(productEntityMapper.toDTO(testEntity)).thenReturn(testDTO);

        ProductDTO result = productService.getById(testId);

        assertThat(result).isEqualTo(testDTO);
        verify(productRepository).findById(testId);
        verify(productEntityMapper).toDTO(testEntity);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {
        when(productRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(testId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
        verify(productRepository).findById(testId);
        verifyNoInteractions(productEntityMapper);
    }

    @Test
    void create_shouldSaveAndReturnProduct_whenValidInput() {
        CreateProductDTO createDTO = createTestCreateDTO();
        ProductEntity newEntity = ProductEntity.builder()
                .name(createDTO.name())
                .description(createDTO.description())
                .price(createDTO.price())
                .stock(createDTO.stock())
                .build();

        when(productDomainMapper.toEntity(createDTO)).thenReturn(newEntity);
        when(productRepository.saveAndFlush(newEntity)).thenReturn(testEntity);
        when(productEntityMapper.toDTO(testEntity)).thenReturn(testDTO);


        ProductDTO result = productService.create(createDTO);

        assertThat(result).isEqualTo(testDTO);
        verify(productDomainMapper).toEntity(createDTO);
        verify(productRepository).saveAndFlush(newEntity);
        verify(productEntityMapper).toDTO(testEntity);
    }

    @Test
    void update_shouldUpdateAndReturnProduct_whenProductExists() {

        UpdateProductDTO updateDTO = createTestUpdateDTO();
        when(productRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(productRepository.saveAndFlush(testEntity)).thenReturn(testEntity);
        when(productEntityMapper.toDTO(testEntity)).thenReturn(testDTO);

        ProductDTO result = productService.update(testId, updateDTO);

        assertThat(result).isEqualTo(testDTO);
        assertThat(testEntity.getName()).isEqualTo(updateDTO.name());
        assertThat(testEntity.getDescription()).isEqualTo(updateDTO.description());
        assertThat(testEntity.getPrice()).isEqualTo(updateDTO.price());
        assertThat(testEntity.getStock()).isEqualTo(updateDTO.stock());
        verify(productRepository).findById(testId);
        verify(productRepository).saveAndFlush(testEntity);
    }

    @Test
    void update_shouldHandlePartialUpdates_whenSomeFieldsAreNull() {
        UpdateProductDTO partialUpdateDTO = new UpdateProductDTO(
                null,
                "New Description",
                null,
                75
        );

        when(productRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(productRepository.saveAndFlush(testEntity)).thenReturn(testEntity);
        when(productEntityMapper.toDTO(testEntity)).thenReturn(testDTO);

        productService.update(testId, partialUpdateDTO);

        assertThat(testEntity.getName()).isEqualTo("Test Product");  // unchanged
        assertThat(testEntity.getDescription()).isEqualTo("New Description");  // updated
        assertThat(testEntity.getPrice()).isEqualTo(BigDecimal.valueOf(19.99));  // unchanged
        assertThat(testEntity.getStock()).isEqualTo(75);  // updated
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {

        UpdateProductDTO updateDTO = createTestUpdateDTO();
        when(productRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(testId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
        verify(productRepository).findById(testId);
        verify(productRepository, never()).saveAndFlush(any());
        verifyNoInteractions(productEntityMapper);
    }

    @Test
    void update_shouldNotUpdatePrice_whenPriceIsNullInDTO() {
        // Arrange
        UpdateProductDTO updateDTO = new UpdateProductDTO(
                "Updated Name",
                "Updated Description",
                null,
                75
        );

        BigDecimal originalPrice = testEntity.getPrice();
        when(productRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(productRepository.saveAndFlush(testEntity)).thenReturn(testEntity);
        when(productEntityMapper.toDTO(testEntity)).thenReturn(testDTO);

        productService.update(testId, updateDTO);

        assertThat(testEntity.getPrice()).isEqualTo(originalPrice);
    }
    @Test
    void update_shouldRejectNegativeStock_whenStockIsNegativeInDTO() {
        UpdateProductDTO invalidUpdateDTO = new UpdateProductDTO(
                "Updated Name",
                "Updated Description",
                BigDecimal.valueOf(39.99),
                -5
        );

        int originalStock = testEntity.getStock();
        when(productRepository.findById(testId)).thenReturn(Optional.of(testEntity));

        assertThatThrownBy(() -> productService.update(testId, invalidUpdateDTO))
                .isInstanceOf(InvalidProductUpdateException.class)
                .hasMessageContaining("Stock cannot be negative");

        assertThat(testEntity.getStock()).isEqualTo(originalStock);
        verify(productRepository, never()).saveAndFlush(any());
    }
    @Test
    void update_shouldThrowWhenPriceIsNegative() {
        UpdateProductDTO invalidDto = new UpdateProductDTO(null, null, BigDecimal.valueOf(-1), null);

        BigDecimal originalPrice = testEntity.getPrice();
        when(productRepository.findById(testId)).thenReturn(Optional.of(testEntity));

        assertThatThrownBy(() -> productService.update(testId, invalidDto))
                .isInstanceOf(InvalidProductUpdateException.class)
                .hasMessage("Price cannot be negative");
        assertThat(testEntity.getPrice()).isEqualTo(originalPrice);
        verify(productRepository, never()).saveAndFlush(any());
    }
    @Test
    void delete_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {

        when(productRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(testId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
        verify(productRepository, never()).delete(any());
    }

}