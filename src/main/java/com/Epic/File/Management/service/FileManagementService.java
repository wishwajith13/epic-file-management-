package com.Epic.File.Management.service;

import com.Epic.File.Management.dto.fileUpload.fileRecodeDTO;
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
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FileManagementService {

    @Value("${file.upload-dir}")
    String uploadDir;

    private final FileManagementRepository repository;
    private final FileReadService fileReadService;

    private static final Pattern VALID_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");

    public FileManagementService(FileManagementRepository repository, FileReadService fileReadService) {
        this.repository = repository;
        this.fileReadService = fileReadService;
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
        String originalName = Path.of(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

        if (repository.findByFileName(originalName).isPresent()) {
            throw new IllegalArgumentException("Duplicate file: " + originalName);
        } else if (!VALID_FILENAME_PATTERN.matcher(originalName).matches()) {
            throw new IllegalArgumentException("Invalid file name: " + originalName);
        }

        Path targetPath = dirPath.resolve(originalName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        filesUploade saved = repository.save(new filesUploade(originalName));

        // Start async processing
        fileReadService.processFile(saved.getFileId(), saved.getFileName());

        return new fileUploadDTO(
                saved.getFileId(),
                saved.getFileName(),
                "SUCCESS",
                "File uploaded successfully. Processing started."
        );
    }

    public String deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name must be provided.");
        }

        filesUploade fileRecord = repository.findByFileName(fileName)
                .orElseThrow(() -> new IllegalArgumentException("No file found with name: " + fileName));

        Path filePath = Paths.get(uploadDir).resolve(fileName);
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                repository.delete(fileRecord);
                return "File record deleted from database. File not found on disk.";
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file from disk: " + e.getMessage(), e);
        }

        repository.delete(fileRecord);

        return "File and database record deleted successfully.";
    }

    public List<fileRecodeDTO> getAllFiles() {
        return repository.findAll()
                .stream()
                .map(file -> new fileRecodeDTO(
                        file.getFileId(),
                        file.getFileName(),
                        file.getStatus(),
                        file.getNumberOfRecords(),
                        file.getSuccessCount(),
                        file.getFailureCount(),
                        file.getCreatedDate(),
                        file.getLastModifiedDate()
                ))
                .collect(Collectors.toList());
    }
}
