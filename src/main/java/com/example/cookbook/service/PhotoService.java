package com.example.cookbook.service;

import com.example.cookbook.domain.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public final class PhotoService {

    @Value("${uploads.path}")
    private String uploadsPath;

    public void savePhoto(Recipe recipe, MultipartFile file) {
        final Logger logger = Logger.getLogger(PhotoService.class.getName());
        final ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            final File uploadDir = new File(uploadsPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            final String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            try {
                byte[] fileInBytes = file.getBytes();
                InputStream in = new ByteArrayInputStream(fileInBytes);
                BufferedImage image = ImageIO.read(in);
                    if (image.getWidth() > 400 && image.getHeight() > 400) {
                        BufferedImage croppedImage = image.getSubimage(Math.round(image.getWidth() / 2) - 200, Math.round(image.getHeight() / 2) - 200, 400, 400);
                        ImageIO.write(croppedImage, "jpg", new File(uploadsPath + "/" + fileName));
                    } else {
                        if (image.getWidth() < image.getHeight()) {
                            BufferedImage croppedImage = image.getSubimage(0, Math.round((image.getHeight() - image.getWidth()) / 2), image.getWidth(), image.getWidth());
                            ImageIO.write(croppedImage, "jpg", new File(uploadsPath + "/" + fileName));
                        } else if (image.getWidth() > image.getHeight()) {
                            BufferedImage croppedImage = image.getSubimage(Math.round((image.getWidth() - image.getHeight()) / 2), 0, image.getHeight(), image.getHeight());
                            ImageIO.write(croppedImage, "jpg", new File(uploadsPath + "/" + fileName));
                        } else {
                            ImageIO.write(image, "jpg", new File(uploadsPath + "/" + fileName));
                        }
                    }
                    recipe.setFilename(fileName);
            } catch (IOException exception) {
                logger.severe("during saving photo error occurred");
                logger.severe(exception.getMessage());
            }
        }
    }
}
