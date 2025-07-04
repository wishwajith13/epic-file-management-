package com.Epic.File.Management.service;

import com.Epic.File.Management.dto.fileRead.fileReadDTO;
import com.Epic.File.Management.entity.fileRead;
import com.Epic.File.Management.entity.filesUploade;
import com.Epic.File.Management.repo.FileManagementRepository;
import com.Epic.File.Management.repo.FileReadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileReadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileReadRepository fileReadRepository;
    private final FileManagementRepository fileManagementRepository;
    private final FileValueService fileValueService;

    public FileReadService(
            FileReadRepository fileReadRepository,
            FileManagementRepository fileManagementRepository,
            FileValueService fileValueService) {
        this.fileReadRepository = fileReadRepository;
        this.fileManagementRepository = fileManagementRepository;
        this.fileValueService = fileValueService;
    }

    @Async("fileProcessorExecutor")
    public void processFile(Long fileId, String fileName) {
        // First step: create fileRead records
        processFileReads(fileId, fileName);

        // Second step: in separate async transaction, process fileValues
        processFileValuesAsync(fileId);
    }

    @Transactional
    public void processFileReads(Long fileId, String fileName) {
        Path path = Paths.get(uploadDir).resolve(fileName);
        filesUploade fileRecord = fileManagementRepository.findById(fileId).orElseThrow();

        fileRecord.setStatus("INIT");
        fileManagementRepository.save(fileRecord);

        int total = 0;
        int success = 0;
        int failed = 0;

        fileRecord.setStatus("INPR");
        fileManagementRepository.save(fileRecord);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String header = reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                total++;
                try {
                    fileRead read = new fileRead();
                    read.setFileId(fileId);
                    read.setContent(line);
                    read.setStatus("SUCCESS");
                    fileReadRepository.save(read);
                    success++;
                } catch (Exception ex) {
                    failed++;
                }
            }
            fileRecord.setStatus("COMP");
        } catch (Exception e) {
            fileRecord.setStatus("ERROR");
        }

        fileRecord.setNumberOfRecords(total);
        fileRecord.setSuccessCount(String.valueOf(success));
        fileRecord.setFailureCount(String.valueOf(failed));
        fileManagementRepository.save(fileRecord);
    }

    @Async("fileProcessorExecutor")
    public void processFileValuesAsync(Long fileId) {
        fileValueService.processFileValues(fileId);
    }

    public List<fileReadDTO> getAllFileReads() {
        return fileReadRepository.findAll()
                .stream()
                .map(record -> new fileReadDTO(
                        record.getReadId(),
                        record.getFileId(),
                        record.getContent(),
                        record.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public fileReadDTO updateFileReadContent(Long readId, String newContent) {
        fileRead record = fileReadRepository.findById(readId)
                .orElseThrow(() -> new IllegalArgumentException("No record found with id: " + readId));

        record.setContent(newContent);
        fileRead updated = fileReadRepository.save(record);

        // Re-process single record
        fileValueService.processSingleFileRead(updated);

        return new fileReadDTO(
                updated.getReadId(),
                updated.getFileId(),
                updated.getContent(),
                updated.getStatus()
        );
    }
}
