package com.tenvia.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;

@Converter(autoApply = false)
public class RewardDTOConverter implements AttributeConverter<RewardDTO, String> {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(RewardDTO rewardDTO) {
        if (rewardDTO == null) return null;
        try {
            return mapper.writeValueAsString(rewardDTO);
        } catch (Exception e) { // Catches whatever new exception Jackson 3 throws
            throw new IllegalArgumentException("Failed to serialize RewardDTO to JSON", e);
        }
    }

    @Override
    public RewardDTO convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return mapper.readValue(dbData, RewardDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to RewardDTO", e);
        }
    }
}
