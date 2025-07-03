package com.Epic.File.Management.service;

import com.Epic.File.Management.dto.fileUpload.fileUploadDTO;
import com.Epic.File.Management.entity.filesUploade;
import com.Epic.File.Management.repo.FileManagementRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FileManagementService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileManagementRepository repository;

    private static final Pattern VALID_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");

    public FileManagementService(FileManagementRepository repository) {
        this.repository = repository;
    }

    public List<fileUploadDTO> storeFiles(MultipartFile[] files) throws IOException {
        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        return Arrays.stream(files)
                .parallel()
                .map(file -> processFileSafely(file, dirPath))
                .collect(Collectors.toList());
    }

    private fileUploadDTO processFileSafely(MultipartFile file, Path dirPath) {
        try {
            return processFile(file, dirPath);
        } catch (Exception e) {
            String originalName = file.getOriginalFilename();
            return new fileUploadDTO(
                    null,
                    originalName,
                    "FAILED",
                    e.getMessage()
            );
        }
    }

    private fileUploadDTO processFile(MultipartFile file, Path dirPath) throws IOException {
        String originalName = Path.of(file.getOriginalFilename()).getFileName().toString();

        if (!VALID_FILENAME_PATTERN.matcher(originalName).matches()) {
            throw new IllegalArgumentException("Invalid file name: " + originalName);
        }

        if (repository.findByFileName(originalName).isPresent()) {
            throw new IllegalArgumentException("Duplicate file: " + originalName);
        }

        Path targetPath = dirPath.resolve(originalName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        filesUploade saved = repository.save(new filesUploade(originalName));

        return new fileUploadDTO(
                saved.getFileId(),
                saved.getFileName(),
                "SUCCESS",
                "File uploaded successfully"
        );
    }
}
