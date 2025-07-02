package com.vwdhub.vending.infrastructure.persistence.mapper;

import com.vwdhub.vending.domain.model.*;
import com.vwdhub.vending.infrastructure.persistence.entity.CoinEntity;
import com.vwdhub.vending.infrastructure.persistence.entity.DispenserEntity;
import com.vwdhub.vending.infrastructure.persistence.entity.DispenserStatusEntity;
import com.vwdhub.vending.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DispenserMapper {

    @Mapping(source = "products", target = "products", qualifiedByName = "productsMap")
    @Mapping(source = "dispenserMoney", target = "dispenserMoney", qualifiedByName = "toMoney")
    @Mapping(source = "status", target = "status", qualifiedByName = "toDispenserStatus")
    @Mapping(target = "insertedMoney", ignore = true)
    Dispenser toDomain(DispenserEntity dispenserEntity);

    @Mapping(source = "products", target = "products", qualifiedByName = "fromProductsMap")
    @Mapping(source = "dispenserMoney", target = "dispenserMoney", qualifiedByName = "fromMoney")
    @Mapping(source = "status", target = "status", qualifiedByName = "toStringStatus")
    @Mapping(target = "insertedMoney", ignore = true)
    DispenserEntity toEntity(Dispenser dispenser);

    @Named("productsMap")
    default Map<UUID, Product> productsMap(List<ProductEntity> products) {
        if (products == null) {
            return Collections.emptyMap();
        }
        Map<UUID, Product> map = new LinkedHashMap<>();
        for (ProductEntity e : products) {
            map.put(e.getId(), toProduct(e));
        }
        return map;
    }

    Product toProduct(ProductEntity product);

    ProductEntity toProductEntity(Product product);

    @Named("fromProductsMap")
    default List<ProductEntity> fromProductsMap(Map<UUID, Product> map) {
        if (map == null) {
            return Collections.emptyList();
        }
        return map.values().stream()
                .map(this::toProductEntity)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Named("toMoney")
    default Money toMoney(List<CoinEntity> coins) {
        if (coins == null) {
            return null;
        }
        return Money.builder()
                .coins(coins.stream().collect(Collectors.toMap(
                        coinEntity -> Coin.valueOf(coinEntity.getCoin())
                        , CoinEntity::getCount)))
                .build();
    }

    @Named("fromMoney")
    default List<CoinEntity> fromMoney(Money money) {
        if (money == null) return Collections.emptyList();
        List<CoinEntity> list = new ArrayList<>();
        money.getCoins().forEach((coin, count) ->
                list.add(CoinEntity.builder()
                        .coin(coin.name())
                        .count(count)
                        .build())
        );
        return list;
    }

    @Named("toDispenserStatus")
    default DispenserStatus toDispenserStatus(DispenserStatusEntity status) {
        return DispenserStatus.valueOf(status.name());
    }

    @Named("toStringStatus")
    default DispenserStatusEntity toStringStatus(DispenserStatus status) {
        return DispenserStatusEntity.valueOf(status.name());
    }


}
