package org.javakov.ocr.type;

import java.util.Locale;

public enum OcrLanguage {
    RU("rus"),
    EN("eng");

    private final String tessName;

    OcrLanguage(String tessName) {
        this.tessName = tessName;
    }

    public String tessName() {
        return tessName;
    }

    public static OcrLanguage from(String value) {
        if (value == null || value.isBlank()) {
            return RU;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "ru", "rus", "russian" -> RU;
            case "en", "eng", "english" -> EN;
            default -> throw new IllegalArgumentException(
                    "Unsupported language '%s'. Use ru|en.".formatted(value));
        };
    }
}
