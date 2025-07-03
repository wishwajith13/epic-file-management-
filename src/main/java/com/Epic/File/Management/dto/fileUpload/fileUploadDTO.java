package com.Epic.File.Management.dto.fileUpload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class fileUploadDTO {
    private Long fileId;
    private String fileName;
    private String status;
    private String message;
}
