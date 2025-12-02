package org.javakov.ocr;

import org.javakov.ocr.type.OcrLanguage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class OcrApplication {

    private static final Path DEFAULT_IMAGE = Path.of(
            "src", "main", "resources", "text_print_ru.png");

    private static final Path DEFAULT_TESSDATA = Path.of(
            "src", "main", "resources", "tessdata");

    private static final Path DEFAULT_OUTPUT_DIR = Path.of("output");

    private static final OcrLanguage DEFAULT_LANGUAGE = OcrLanguage.RU;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            runWith(DEFAULT_IMAGE, DEFAULT_TESSDATA, DEFAULT_LANGUAGE, null, DEFAULT_OUTPUT_DIR);
            return;
        }

        CliArguments cliArguments = CliArguments.parse(args);
        runWith(
                cliArguments.imagePath(),
                cliArguments.tessdataPath(),
                cliArguments.language(),
                cliArguments.explicitModel(),
                cliArguments.outputDir()
        );
    }

    private static void runWith(Path imagePath,
                                Path tessdataPath,
                                OcrLanguage language,
                                String explicitModel,
                                Path outputDir) throws IOException {
        Path ensuredTessdata = ensureTessdata(tessdataPath);
        Path ensuredOutput = ensureOutputDir(outputDir);
        String languageModel = selectModel(language, explicitModel);

        BufferedImage image = ImagePreprocessor.loadAndEnhance(imagePath);
        TesseractService service = new TesseractService(ensuredTessdata, languageModel);
        String text = service.recognize(image);

        System.out.println("----- OCR RESULT START -----");
        System.out.println(text.stripTrailing());
        System.out.println("----- OCR RESULT END -----");

        Path outputFile = ensuredOutput.resolve(buildOutputName(imagePath));
        Files.writeString(outputFile, text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Result saved to: " + outputFile.toAbsolutePath());
    }

    private static String selectModel(OcrLanguage language, String explicitModel) {
        if (explicitModel != null && !explicitModel.isBlank()) {
            return explicitModel;
        }

        return language.tessName();
    }

    private static Path ensureTessdata(Path tessdata) throws IOException {
        if (!Files.exists(tessdata)) {
            throw new IOException("""
                    Tessdata folder '%s' not found. Run download_models.bat first \
                    or specify --tessdata path/to/folder.
                    """.formatted(tessdata));
        }
        return tessdata;
    }

    private static Path ensureOutputDir(Path outputDir) throws IOException {
        if (Files.exists(outputDir) && !Files.isDirectory(outputDir)) {
            throw new IOException("Output path '%s' exists but is not a directory.".formatted(outputDir));
        }
        Files.createDirectories(outputDir);
        return outputDir;
    }

    private static String buildOutputName(Path imagePath) {
        String fileName = imagePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileName = fileName.substring(0, dotIndex);
        }
        return fileName + ".txt";
    }
}
