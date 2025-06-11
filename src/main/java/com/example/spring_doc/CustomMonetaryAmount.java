/*
 * Copyright (c) 2024 Wink.
 */

package com.example.spring_doc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

/**
 * Created on: 5/21/23.
 *
 * @author Bjorn Harvold Responsibility:
 */
@Value
public class CustomMonetaryAmount {

    @NotNull(message = "amount is required")
    BigDecimal amount;

    @NotBlank(message = "currency is required")
    String currency;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    @ConstructorProperties({
            "amount",
            "currency"
    })
    public CustomMonetaryAmount(
            @JsonProperty(value = "amount", required = true) @NotNull(message = "amount is required") BigDecimal amount,
            @JsonProperty(value = "currency", required = true) @NotBlank(message = "currency is required") String currency
    ) {
        this.amount = amount;
        this.currency = currency;
    }
}
