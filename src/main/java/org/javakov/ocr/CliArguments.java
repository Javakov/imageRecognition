package org.javakov.ocr;

import org.javakov.ocr.type.OcrLanguage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public record CliArguments(
        Path imagePath,
        Path tessdataPath,
        OcrLanguage language,
        String explicitModel,
        Path outputDir
) {

    public static CliArguments parse(String[] args) {
        Map<String, String> values = getValues(args);

        Path image = requirePath(values.remove("image"));
        if (!Files.exists(image)) {
            throw new IllegalArgumentException("Image '%s' is missing".formatted(image));
        }

        Path tessdata = getPathOrDefault(values.remove("tessdata"),
                Path.of("src", "main", "resources", "tessdata"));
        Path output = getPathOrDefault(values.remove("output"), Path.of("output"));

        OcrLanguage lang = OcrLanguage.from(values.remove("lang"));
        String model = values.remove("model");

        if (!values.isEmpty()) {
            throw new IllegalArgumentException("Unknown flags: " + values.keySet());
        }

        return new CliArguments(image, tessdata, lang, model, output);
    }

    private static Map<String, String> getValues(String[] args) {
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            String token = args[i];
            if (!token.startsWith("--")) {
                throw new IllegalArgumentException("Unexpected token '%s'. Use --key value pairs.".formatted(token));
            }
            String key = token.substring(2);
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Flag '%s' requires a value".formatted(token));
            }
            values.put(key, args[++i]);
        }
        return values;
    }

    private static Path requirePath(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Provide --image path/to/file");
        }
        return Path.of(raw);
    }

    private static Path getPathOrDefault(String raw, Path fallback) {
        return raw == null ? fallback : Path.of(raw);
    }
}
