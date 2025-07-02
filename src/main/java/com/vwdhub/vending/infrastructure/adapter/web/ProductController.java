package com.vwdhub.vending.infrastructure.adapter.web;

import com.vwdhub.vending.application.usecase.AddProductUseCase;
import com.vwdhub.vending.application.usecase.GetProductStockUseCase;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.infrastructure.adapter.web.dto.request.CreateProductRequestDto;
import com.vwdhub.vending.infrastructure.adapter.web.dto.response.ProductResponse;
import com.vwdhub.vending.infrastructure.adapter.web.dto.response.ProductStockResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/vending/product")
public class ProductController {
    private final AddProductUseCase addProductUseCase;
    private final GetProductStockUseCase getProductStockUseCase;

    public ProductController(AddProductUseCase addProductUseCase, GetProductStockUseCase getProductStockUseCase) {
        this.addProductUseCase = addProductUseCase;
        this.getProductStockUseCase = getProductStockUseCase;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid CreateProductRequestDto request) {
        Product product = addProductUseCase.add(
                request.getDispenserId(),
                request.getName(),
                request.getPrice(),
                request.getExpiration(),
                request.getStock()
        );
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .expiration(product.getExpiration())
                .stock(product.getStock())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<ProductStockResponse> getProductStock(@PathVariable("id") UUID productId) {
        Integer stock = getProductStockUseCase.getProduct(productId);
        ProductStockResponse productStockResponse = ProductStockResponse.builder().stock(stock).build();
        return ResponseEntity.ok(productStockResponse);
    }
}
