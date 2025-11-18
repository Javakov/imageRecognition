package org.javakov.ocr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.file.Path;

public final class ImagePreprocessor {

    private ImagePreprocessor() {
    }

    public static BufferedImage loadAndEnhance(Path imagePath) throws IOException {
        BufferedImage original = ImageIO.read(imagePath.toFile());
        if (original == null) {
            throw new IOException("Unsupported image format: " + imagePath);
        }

        BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = gray.createGraphics();
        graphics.drawImage(original, 0, 0, null);
        graphics.dispose();

        // Improve contrast and brightness to aid OCR.
        RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
        rescaleOp.filter(gray, gray);
        return gray;
    }
}

