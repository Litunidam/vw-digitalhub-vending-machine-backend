package com.vwdhub.vending.infrastructure.persistence.mapper;

import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toDomain(ProductEntity entity);

    ProductEntity toEntity(Product domain);

    default List<Product> toDomainList(List<ProductEntity> productEntityList) {
        if (productEntityList == null) return null;
        List<Product> productList = new ArrayList<>(productEntityList.size());
        for (ProductEntity productEntity : productEntityList) {
            productList.add(toDomain(productEntity));
        }
        return productList;
    }
}