package io.dedyn.hy.bookstore.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Could not save image file: " + fileName, ex);
        }
    }

    public static void removeAllFiles(String dir) throws IOException {
        Path dirPath = Paths.get(dir);
        if (Files.exists(dirPath)) {
            try {
                Files.list(dirPath).forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.out.println("Could not delete file: " + file);
                    }
                });
            } catch (IOException e) {
                throw new IOException("Could not list files in directory: " + dir);
            }
        }
    }

    public static void deleteDirectory(String dir) throws IOException {
        Path dirPath = Paths.get(dir);
        if (Files.exists(dirPath)) {
            try {
                removeAllFiles(dir);
                Files.delete(dirPath);
            } catch (IOException e) {
                throw new IOException("Could not list files in directory: " + dir);
            }
        }
    }
}
