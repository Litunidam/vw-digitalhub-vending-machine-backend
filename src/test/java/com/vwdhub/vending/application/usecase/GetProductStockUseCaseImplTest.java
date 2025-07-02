package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.application.usecase.impl.GetProductStockUseCaseImpl;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.ProductNotFoundException;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductStockUseCaseImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductStockUseCaseImpl useCase;

    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = Product.builder()
                .id(productId)
                .name("Test")
                .price(new BigDecimal("1.23"))
                .expiration(LocalDate.now().plusDays(1))
                .stock(7)
                .build();
    }

    @Test
    void whenProductExistsReturnStock() {

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        Integer stock = useCase.getProduct(productId);

        assertThat(stock).isEqualTo(7);
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void whenProductNotExistsThrowsProductNotFoundException() {

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        ProductNotFoundException ex = catchThrowableOfType(
                () -> useCase.getProduct(productId),
                ProductNotFoundException.class
        );
        assertThat(ex).hasMessage(Constants.PRODUCT_NOT_FOUND);

        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }
}
