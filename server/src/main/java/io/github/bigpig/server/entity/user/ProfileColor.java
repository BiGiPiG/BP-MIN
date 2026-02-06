package io.github.bigpig.server.entity.user;

public enum ProfileColor {
    VIOLET_GRADIENT("linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%)"),
    INDIGO_GRADIENT("linear-gradient(135deg, #8b5cf6 0%, #c4b5fd 100%)"),
    FUCHSIA_GRADIENT("linear-gradient(135deg, #a855f7 0%, #d8b4fe 100%)"),
    PINK_GRADIENT("linear-gradient(135deg, #d946ef 0%, #f0abfc 100%)"),
    ORANGE_GRADIENT("linear-gradient(135deg, #f97316 0%, #fdba74 100%)"),
    AMBER_GRADIENT("linear-gradient(135deg, #ea580c 0%, #fed7aa 100%)"),
    BICOLOR_VIOLET_ORANGE("linear-gradient(135deg, #7e4aff 0%, #f97316 100%)"),
    BICOLOR_FUCHSIA_AMBER("linear-gradient(135deg, #a855f7 0%, #ea580c 100%)");

    private final String gradient;

    ProfileColor(String gradient) {
        this.gradient = gradient;
    }

    public String getGradient() {
        return gradient;
    }
}