package com.reelthoughts.util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.io.File;

public class ImageUtil {
    
    public String saveImageAndGetPath(Part part, String subfolder) throws IOException {
        if (part == null || part.getSize() == 0 || !isImageFile(part)) {
            System.out.println("[ImageUtil] No valid image file provided");
            return null;
        }

        // Get the webapp's root directory
        String webappRoot = System.getProperty("catalina.home") + File.separator + "webapps" + File.separator + "reelthoughts";
        System.out.println("[ImageUtil] Webapp root: " + webappRoot);

        // Create the full path for the upload directory
        String uploadDir = webappRoot + File.separator + "resources" + File.separator + "images" + File.separator + "system" + File.separator + subfolder;
        System.out.println("[ImageUtil] Upload directory: " + uploadDir);

        // Create the directory if it doesn't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            System.out.println("[ImageUtil] Creating directory: " + uploadDir);
            boolean created = directory.mkdirs();
            if (!created) {
                System.out.println("[ImageUtil] Failed to create directory: " + uploadDir);
                throw new IOException("Failed to create directory: " + uploadDir);
            }
            System.out.println("[ImageUtil] Directory created successfully");
        }

        // Generate unique filename
        String fileName = generateUniqueFilename(part);
        System.out.println("[ImageUtil] Generated filename: " + fileName);

        // Save file to disk
        File outputFile = new File(directory, fileName);
        System.out.println("[ImageUtil] Full file path: " + outputFile.getAbsolutePath());

        try (InputStream inputStream = part.getInputStream()) {
            // Check if the file already exists
            if (outputFile.exists()) {
                System.out.println("[ImageUtil] File already exists, will be replaced");
            }

            // Copy the file using Files.copy
            Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            // Verify the file was created
            if (outputFile.exists()) {
                System.out.println("[ImageUtil] File saved successfully. Size: " + outputFile.length() + " bytes");
                System.out.println("[ImageUtil] File absolute path: " + outputFile.getAbsolutePath());
                System.out.println("[ImageUtil] File exists: " + outputFile.exists());
                System.out.println("[ImageUtil] File can read: " + outputFile.canRead());
                System.out.println("[ImageUtil] File can write: " + outputFile.canWrite());
            } else {
                System.out.println("[ImageUtil] Error: File was not created");
                throw new IOException("Failed to save file");
            }
        } catch (IOException e) {
            System.out.println("[ImageUtil] Error saving file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // Return web-accessible path (always use forward slashes for URLs)
        String webPath = "/resources/images/system/" + subfolder + "/" + fileName;
        System.out.println("[ImageUtil] Web path: " + webPath);
        return webPath;
    }

    private String generateUniqueFilename(Part part) {
        String originalName = part.getSubmittedFileName();
        String fileName = System.currentTimeMillis() + "_" + 
              (originalName != null ? originalName : "user.png");
        System.out.println("[ImageUtil] Original filename: " + originalName);
        return fileName;
    }

    public boolean isImageFile(Part part) {
        boolean isImage = part != null && 
               part.getContentType() != null && 
               part.getContentType().startsWith("image/");
        System.out.println("[ImageUtil] Is image file: " + isImage + ", Content type: " + 
            (part != null ? part.getContentType() : "null"));
        return isImage;
    }
}