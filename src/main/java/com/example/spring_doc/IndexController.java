/*
 * Copyright (c) 2013-2025 Wink.
 */

package com.example.spring_doc;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Bjorn Harvold
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
public class IndexController {

    /**
     * Status boolean response.
     *
     * @return the boolean response
     */
    @Operation(
            summary = "Ping me",
            description = "Ping me details."
    )
    @GetMapping(
            value = {"/ping"},
            headers = {
                    HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE,
            },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PingResponse status() {
        return new PingResponse(Money.of(10D, "USD"));
    }

}

