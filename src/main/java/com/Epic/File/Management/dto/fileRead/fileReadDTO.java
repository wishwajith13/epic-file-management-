package com.Epic.File.Management.dto.fileRead;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class fileReadDTO {
    private Long readId;
    private Long fileId;
    private String content;
    private String status;
}

