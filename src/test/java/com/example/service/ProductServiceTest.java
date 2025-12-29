package com.example.service;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldSaveProduct() {
        Product product = new Product();
        product.setName("Mobile");
        product.setPrice(20000);

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        Product saved = productService.save(product);

        assertNotNull(saved);
        assertEquals("Mobile", saved.getName());
    }

    @Test
    void shouldReturnAllProducts() {
        List<Product> list = List.of(
                new Product(1L, "Laptop", 50000),
                new Product(2L, "Tablet", 30000)
        );

        when(productRepository.findAll()).thenReturn(list);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldDeleteProduct() {
        productService.delete(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }
}
