package org.javakov.ocr.type;

public enum OcrMode {
    SIMPLE,
    HANDWRITTEN;

    public static OcrMode fromFlag(String value) {
        if (value == null || value.isBlank()) {
            return SIMPLE;
        }

        return switch (value.trim().toLowerCase()) {
            case "simple", "print", "printed" -> SIMPLE;
            case "hard", "handwritten", "complex" -> HANDWRITTEN;
            default -> throw new IllegalArgumentException(
                    "Unknown mode '%s'. Supported: simple, handwritten.".formatted(value));
        };
    }
}

