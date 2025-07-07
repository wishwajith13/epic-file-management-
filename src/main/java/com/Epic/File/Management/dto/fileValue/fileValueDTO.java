package com.Epic.File.Management.dto.fileValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class fileValueDTO {
    private Long valueId;
    private Long fileId;
    private Long readId;
    private String studentId;
    private String name;
    private String email;
    private String address;
    private String gpa;
}
