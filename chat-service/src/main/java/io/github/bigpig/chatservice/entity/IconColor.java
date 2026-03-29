package io.github.bigpig.chatservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum IconColor {
    VIOLET_GRADIENT("linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%)"),
    INDIGO_GRADIENT("linear-gradient(135deg, #8b5cf6 0%, #c4b5fd 100%)"),
    FUCHSIA_GRADIENT("linear-gradient(135deg, #a855f7 0%, #d8b4fe 100%)"),
    PINK_GRADIENT("linear-gradient(135deg, #d946ef 0%, #f0abfc 100%)"),
    ORANGE_GRADIENT("linear-gradient(135deg, #f97316 0%, #fdba74 100%)"),
    AMBER_GRADIENT("linear-gradient(135deg, #ea580c 0%, #fed7aa 100%)"),
    BI_COLOR_VIOLET_ORANGE("linear-gradient(135deg, #7e4aff 0%, #f97316 100%)"),
    BI_COLOR_FUCHSIA_AMBER("linear-gradient(135deg, #a855f7 0%, #ea580c 100%)");

    private final String gradient;
    private static final Map<String, IconColor> GRADIENT_MAP;

    IconColor(String gradient) {
        this.gradient = gradient;
    }

    static {
        GRADIENT_MAP = new HashMap<>();
        for (IconColor color : values()) {
            GRADIENT_MAP.put(color.gradient, color);
        }
    }

    @JsonValue
    public String getGradient() {
        return gradient;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static IconColor fromGradient(String gradient) {
        if (gradient == null || gradient.isBlank()) {
            return null;
        }
        IconColor color = GRADIENT_MAP.get(gradient);
        if (color == null) {
            throw new IllegalArgumentException("Unknown gradient: " + gradient);
        }
        return color;
    }
}
