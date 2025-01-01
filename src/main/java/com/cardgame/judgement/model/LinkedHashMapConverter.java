package com.cardgame.judgement.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.LinkedHashMap;

@Converter
public class LinkedHashMapConverter implements AttributeConverter<LinkedHashMap<String, Integer>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(LinkedHashMap<String, Integer> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting LinkedHashMap to JSON String", e);
        }
    }

    @Override
    public LinkedHashMap<String, Integer> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Integer>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON String to LinkedHashMap", e);
        }
    }
}
