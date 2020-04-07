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

@Service
public class FileService {

    @Value("${uploads.path}")
    private String uploadsPath;

    public void savePhoto(Recipe recipe, MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadsPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            byte[] fileInBytes = file.getBytes();
            InputStream in = new ByteArrayInputStream(fileInBytes);
            BufferedImage image = ImageIO.read(in);
            try {
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
            } catch (java.lang.NullPointerException ignored) {
            }
        }
    }
}
