package com.Epic.File.Management.dto.fileUpload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class fileRecodeDTO {
    private Long fileId;
    private String fileName;
    private String status;
    private int numberOfRecords;
    private String successCount;
    private String failureCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public fileRecodeDTO(Long fileId, String fileName) {
        this.fileId = fileId;
        this.fileName = fileName;
    }
}
