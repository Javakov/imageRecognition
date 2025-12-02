package org.javakov.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

public class TesseractService {

    private static final Logger log = LoggerFactory.getLogger(TesseractService.class);
    private final ITesseract tesseract;

    public TesseractService(Path tessdata, String languageModel) {
        this.tesseract = new Tesseract();
        Path dataPath = prepareTessdata(tessdata);
        this.tesseract.setDatapath(dataPath.toString());
        this.tesseract.setLanguage(languageModel);
    }

    public String recognize(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new IllegalStateException("Tesseract failed: " + e.getMessage(), e);
        }
    }

    private Path prepareTessdata(Path tessdata) {
        if (Files.exists(tessdata)) {
            return tessdata.toAbsolutePath();
        }

        log.warn("Provided tessdata folder '{}' was not found. Falling back to tess4j default tessdata.",
                tessdata);
        return LoadLibs.extractTessResources("tessdata").toPath();
    }
}
