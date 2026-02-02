package pt.ulusofona.productservice.service;

import pt.ulusofona.productservice.dto.ProductRequest;
import pt.ulusofona.productservice.dto.ProductResponse;
import pt.ulusofona.productservice.model.Product;
import pt.ulusofona.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Notebook");
        testProduct.setDescription("Notebook de alta performance");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setStockQuantity(10);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        productRequest = new ProductRequest();
        productRequest.setName("Notebook");
        productRequest.setDescription("Notebook de alta performance");
        productRequest.setPrice(new BigDecimal("999.99"));
        productRequest.setStockQuantity(10);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        // Arrange
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setDescription("Mouse sem fio");
        product2.setPrice(new BigDecimal("29.99"));
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Notebook", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        ProductResponse result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Notebook", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(999L);
        });

        assertEquals("Produto não encontrado com ID: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void createProduct_ShouldCreateProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductResponse result = productService.createProduct(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Notebook", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateProduct() {
        // Arrange
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Notebook Atualizado");
        updateRequest.setDescription("Nova descrição");
        updateRequest.setPrice(new BigDecimal("1099.99"));
        updateRequest.setStockQuantity(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductResponse result = productService.updateProduct(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductNotExists_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(999L, productRequest);
        });

        assertEquals("Produto não encontrado com ID: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void searchProductsByName_ShouldReturnMatchingProducts() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("note")).thenReturn(Arrays.asList(testProduct));

        // Act
        List<ProductResponse> result = productService.searchProductsByName("note");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Notebook", result.get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("note");
    }
}

