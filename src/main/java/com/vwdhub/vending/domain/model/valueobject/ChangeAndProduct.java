package com.vwdhub.vending.domain.model.valueobject;

import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Builder
@Value
public class ChangeAndProduct {
    Money change;
    Product product;
    Money dispenserMoney;

}

