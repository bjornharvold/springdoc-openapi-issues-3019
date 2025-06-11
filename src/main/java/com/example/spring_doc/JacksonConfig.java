/*
 * Copyright (c) 2013-2025 Wink.
 */

package com.example.spring_doc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.moneta.MonetaMoneyModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    /**
     * Object mapper object mapper.
     *
     * @return the object mapper
     */
    @Primary
    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        builder.featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
                DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
        );

        builder.featuresToEnable(
                SerializationFeature.WRITE_DATES_WITH_ZONE_ID,
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
        );

        builder.timeZone(TimeZone.getTimeZone("UTC"));

        builder.failOnEmptyBeans(false);
        builder.failOnUnknownProperties(false);

        // do not include null value in json to make object graph smaller
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        builder.modules(
                new MonetaMoneyModule().withMoney().withDefaultFormatting()
        );

        return builder.build();
    }

}
