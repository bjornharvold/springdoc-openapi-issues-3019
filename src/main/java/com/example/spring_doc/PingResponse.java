package com.example.spring_doc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.money.MonetaryAmount;
import java.beans.ConstructorProperties;

/**
 * Created on: 6/11/25.
 *
 * @author Bjorn Harvold
 * Responsibility:
 */
@Value
public class PingResponse {
    @Schema(description = "Show me the money!")
    MonetaryAmount amount;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    @ConstructorProperties({
            "amount"
    })
    public PingResponse(
            @JsonProperty("amount") MonetaryAmount amount
    ) {
        this.amount = amount;
    }
}
