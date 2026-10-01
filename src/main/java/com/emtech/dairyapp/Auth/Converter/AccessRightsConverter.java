package com.emtech.dairyapp.Auth.Converter;

import com.emtech.dairyapp.Auth.Role.RoleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class AccessRightsConverter implements AttributeConverter<List<RoleService.AccessRight>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(List<RoleService.AccessRight> accessRights) {
        return accessRights != null && !accessRights.isEmpty() ? this.objectMapper.writeValueAsString(accessRights) : null;
    }

    @SneakyThrows
    @Override
    public List<RoleService.AccessRight> convertToEntityAttribute(String s) {
        return s != null && !s.isEmpty() ? this.objectMapper.readValue(s.trim(), new TypeReference<>() {}) : new ArrayList<>();
    }
}
