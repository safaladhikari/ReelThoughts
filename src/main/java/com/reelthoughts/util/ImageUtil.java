package com.reelthoughts.util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class ImageUtil {
    
    // Use proper Windows path format
    private static final String UPLOAD_BASE_DIR = getUploadBaseDir();

    private static String getUploadBaseDir() {
        // Get the deployment directory path properly
        String deployPath = ImageUtil.class.getClassLoader().getResource("").getPath();
        deployPath = deployPath.substring(0, deployPath.indexOf("/WEB-INF/"));
        
        // Convert to Windows format if needed
        return deployPath.replace("/", "\\") + "resources\\images\\system\\";
    }

    public String saveImageAndGetPath(Part part, String subfolder) throws IOException {
        if (part == null || part.getSize() == 0 || !isImageFile(part)) {
            return null;
        }

        // 1. Prepare upload directory with proper path separators
        Path uploadPath = Paths.get(UPLOAD_BASE_DIR + subfolder.replace("/", "\\"));
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Generate unique filename
        String fileName = generateUniqueFilename(part);

        // 3. Save file to disk
        Path filePath = uploadPath.resolve(fileName);
        try (InputStream inputStream = part.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 4. Return web-accessible path (always use forward slashes for URLs)
        return "/resources/images/system/" + subfolder + "/" + fileName;
    }

    private String generateUniqueFilename(Part part) {
        String originalName = part.getSubmittedFileName();
        return System.currentTimeMillis() + "_" + 
              (originalName != null ? originalName : "user.png");
    }

    public boolean isImageFile(Part part) {
        return part != null && 
               part.getContentType() != null && 
               part.getContentType().startsWith("image/");
    }
}