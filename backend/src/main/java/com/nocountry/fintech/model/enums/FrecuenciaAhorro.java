package com.nocountry.fintech.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FrecuenciaAhorro {
    BAJA,
    MEDIA,
    ALTA;

    @JsonCreator
    public static FrecuenciaAhorro fromString(String value) {
        if (value == null || value.isBlank()) {
            return MEDIA;
        }
        for (FrecuenciaAhorro f : FrecuenciaAhorro.values()) {
            if (f.name().equalsIgnoreCase(value.trim())) {
                return f;
            }
        }
        return MEDIA;
    }

    @JsonValue
    public String toValue() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
