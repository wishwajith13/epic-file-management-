package com.Epic.File.Management.service;

import com.Epic.File.Management.entity.fileRead;
import com.Epic.File.Management.entity.fileValue;
import com.Epic.File.Management.repo.FileReadRepository;
import com.Epic.File.Management.repo.FileValueRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FileValueService {

    private final FileValueRepository fileValueRepository;
    private final FileReadRepository fileReadRepository;

    public FileValueService(FileValueRepository fileValueRepository, FileReadRepository fileReadRepository) {
        this.fileValueRepository = fileValueRepository;
        this.fileReadRepository = fileReadRepository;
    }

    @Async("fileProcessorExecutor")
    public void processFileValues(Long fileId) {
        var records = fileReadRepository.findByFileId(fileId);
        System.out.println("Found " + records.size() + " fileRead records for fileId=" + fileId);

        for (fileRead read : records) {
            try {
                String[] parts = read.getContent().split(",");
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Invalid content format");
                }

                fileValue value = new fileValue();
                value.setFileId(fileId);
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
    }

    public void processSingleFileRead(fileRead read) {
        try {
            String[] parts = read.getContent().split(",");
            if (parts.length < 5) {
                throw new IllegalArgumentException("Invalid content format");
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
}
