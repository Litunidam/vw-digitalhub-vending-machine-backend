package com.vwdhub.vending.infrastructure.persistence.mapper;

import com.vwdhub.vending.domain.model.Coin;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.infrastructure.persistence.entity.CoinEntity;
import com.vwdhub.vending.infrastructure.persistence.entity.DispenserStatusEntity;
import com.vwdhub.vending.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class DispenserMapperTest {

    private final DispenserMapper mapper = Mappers.getMapper(DispenserMapper.class);

    @Test
    void productsMapreturnEmptyMap() {
        Map<UUID, Product> result = mapper.productsMap(null);
        assertThat(result).isEmpty();
    }

    @Test
    void productsMapEmptyListreturnEmptyMap() {
        Map<UUID, Product> result = mapper.productsMap(Collections.emptyList());
        assertThat(result).isEmpty();
    }

    @Test
    void productsMapreturnDomainProducts() {
        UUID id1 = UUID.randomUUID();
        ProductEntity e1 = new ProductEntity();
        e1.setId(id1);
        e1.setName("P1");
        e1.setPrice(new BigDecimal("1.00"));
        e1.setExpiration(LocalDate.of(2025, 1, 1));
        e1.setStock(5);

        UUID id2 = UUID.randomUUID();
        ProductEntity e2 = new ProductEntity();
        e2.setId(id2);
        e2.setName("P2");
        e2.setPrice(new BigDecimal("2.50"));
        e2.setExpiration(LocalDate.of(2025, 6, 1));
        e2.setStock(0);

        List<ProductEntity> list = List.of(e1, e2);
        Map<UUID, Product> map = mapper.productsMap(list);

        assertThat(map).hasSize(2)
                .containsKeys(id1, id2);

        Product p1 = map.get(id1);
        assertThat(p1.getId()).isEqualTo(id1);
        assertThat(p1.getName()).isEqualTo("P1");
        assertThat(p1.getStock()).isEqualTo(5);

        Product p2 = map.get(id2);
        assertThat(p2.getName()).isEqualTo("P2");
        assertThat(p2.getPrice()).isEqualByComparingTo("2.50");
    }

    @Test
    void fromProductsMapreturnEmptyList() {
        List<ProductEntity> result = mapper.fromProductsMap(null);
        assertThat(result).isEmpty();
    }

    @Test
    void fromProductsMapEmptyMapreturnEmptyList() {
        List<ProductEntity> result = mapper.fromProductsMap(Collections.emptyMap());
        assertThat(result).isEmpty();
    }

    @Test
    void fromProductsMapreturnListOfEntities() {
        UUID id = UUID.randomUUID();
        Product domain = Product.builder()
                .id(id)
                .name("DX")
                .price(new BigDecimal("9.99"))
                .expiration(LocalDate.of(2024, 12, 31))
                .stock(3)
                .build();
        Map<UUID, Product> src = new LinkedHashMap<>();
        src.put(id, domain);

        List<ProductEntity> list = mapper.fromProductsMap(src);
        assertThat(list).hasSize(1);

        ProductEntity ent = list.get(0);
        assertThat(ent.getId()).isEqualTo(id);
        assertThat(ent.getName()).isEqualTo("DX");
        assertThat(ent.getStock()).isEqualTo(3);
    }

    @Test
    void toMoneyreturnNull() {
        assertThat(mapper.toMoney(null)).isNull();
    }

    @Test
    void toMoneyreturnMoneyNoCoins() {
        Money m = mapper.toMoney(Collections.emptyList());
        assertThat(m).isNotNull();
        assertThat(m.getCoins()).isEmpty();
    }

    @Test
    void toMoneyreturnCoinsAdded() {
        CoinEntity c1 = CoinEntity.builder().coin(Coin.EUR_1.name()).count(2).build();
        CoinEntity c2 = CoinEntity.builder().coin(Coin.CENT_50.name()).count(5).build();

        Money m = mapper.toMoney(List.of(c1, c2));
        assertThat(m.getCoins())
                .containsEntry(Coin.EUR_1, 2)
                .containsEntry(Coin.CENT_50, 5);
    }

    @Test
    void fromMoneyReturnEmptyList() {
        assertThat(mapper.fromMoney(null)).isEmpty();
    }

    @Test
    void fromMoneyEmptyMoneyReturnEmptyList() {
        Money zero = Money.builder().coins(Collections.emptyMap()).build();
        assertThat(mapper.fromMoney(zero)).isEmpty();
    }

    @Test
    void fromMoneyReturnListOfCoinEntities() {
        Money money = Money.builder()
                .coins(Map.of(Coin.EUR_1, 1, Coin.CENT_50, 3))
                .build();

        List<CoinEntity> list = mapper.fromMoney(money);
        assertThat(list).hasSize(2)
                .extracting("coin", "count")
                .containsExactlyInAnyOrder(
                        tuple(Coin.EUR_1.name(), 1),
                        tuple(Coin.CENT_50.name(), 3)
                );
    }

    @Test
    void toDispenserStatusAllEnumValuesMapped() {
        for (DispenserStatusEntity ent : DispenserStatusEntity.values()) {
            DispenserStatus dom = mapper.toDispenserStatus(ent);
            assertThat(dom.name()).isEqualTo(ent.name());
        }
    }

    @Test
    void toStringStatusAllEnumValuesMapped() {
        for (DispenserStatus dom : DispenserStatus.values()) {
            DispenserStatusEntity ent = mapper.toStringStatus(dom);
            assertThat(ent.name()).isEqualTo(dom.name());
        }
    }
}
