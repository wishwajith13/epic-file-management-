package com.Epic.File.Management.service;

import com.Epic.File.Management.dto.fileValue.fileValueDTO;
import com.Epic.File.Management.entity.fileRead;
import com.Epic.File.Management.entity.fileValue;
import com.Epic.File.Management.entity.filesUploade;
import com.Epic.File.Management.repo.FileManagementRepository;
import com.Epic.File.Management.repo.FileReadRepository;
import com.Epic.File.Management.repo.FileValueRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileValueService {

    private final FileValueRepository fileValueRepository;
    private final FileReadRepository fileReadRepository;
    private final FileManagementRepository fileManagementRepository;

    public FileValueService(
            FileValueRepository fileValueRepository,
            FileReadRepository fileReadRepository,
            FileManagementRepository fileManagementRepository
    ) {
        this.fileValueRepository = fileValueRepository;
        this.fileReadRepository = fileReadRepository;
        this.fileManagementRepository = fileManagementRepository;
    }

    @Async("fileProcessorExecutor")
    public void processFileValues(Long fileId) {
        var records = fileReadRepository.findByFileId(fileId);
        System.out.println("Found " + records.size() + " fileRead records for fileId=" + fileId);

        for (fileRead read : records) {
            processSingleRecord(read);
        }

        updateFileUploadCounts(fileId);
    }

    @Transactional
    public void processSingleFileRead(fileRead read) {
        processSingleRecord(read);
        updateFileUploadCounts(read.getFileId());
    }

    private void processSingleRecord(fileRead read) {
        try {
            String[] parts = read.getContent().split(",");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Invalid content format: expected exactly 5 fields, found " + parts.length);
            }

            fileValue value = fileValueRepository.findByReadId(read.getReadId())
                    .orElse(new fileValue());

            value.setFileId(read.getFileId());
            value.setReadId(read.getReadId());
            value.setStudentId(parts[0].trim());
            value.setName(parts[1].trim());
            value.setEmail(parts[2].trim());
            value.setAddress(parts[3].trim());
            value.setGpa(parts[4].trim());

            fileValueRepository.save(value);

            read.setStatus("1"); // success
            fileReadRepository.save(read);

        } catch (Exception ex) {
            read.setStatus("0"); // failure
            fileReadRepository.save(read);
        }
    }

    @Transactional
    public void updateFileUploadCounts(Long fileId) {
        long success = fileReadRepository.countByFileIdAndStatus(fileId, "1");
        long failure = fileReadRepository.countByFileIdAndStatus(fileId, "0");
        long total = success + failure;

        filesUploade fileRecord = fileManagementRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with id=" + fileId));

        fileRecord.setNumberOfRecords((int) total);
        fileRecord.setSuccessCount(String.valueOf(success));
        fileRecord.setFailureCount(String.valueOf(failure));

        if (total == 0) {
            fileRecord.setStatus("ERROR"); // nothing processed
        } else if (success == total) {
            fileRecord.setStatus("COMP");
        } else if (failure == total) {
            fileRecord.setStatus("ERROR");
        } else {
            fileRecord.setStatus("ERROR");
        }

        fileManagementRepository.save(fileRecord);
    }

    public List<fileValueDTO> getAllFileValues() {
        List<fileValue> records = fileValueRepository.findAll();

        if (records.isEmpty()) {
            throw new IllegalStateException("No file value records found.");
        }

        return records.stream()
                .map(value -> new fileValueDTO(
                        value.getValueId(),
                        value.getFileId(),
                        value.getReadId(),
                        value.getStudentId(),
                        value.getName(),
                        value.getEmail(),
                        value.getAddress(),
                        value.getGpa()
                ))
                .collect(Collectors.toList());
    }
}
