package com.Epic.File.Management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "uploaded_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class filesUploade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(name = "file_name", unique = true, nullable = false)
    private String fileName;

    private String status;

    private int numberOfRecords;

    private String successCount;

    private String failureCount;

    // ✅ Constructor that sets fileName
    public filesUploade(String fileName) {
        this.fileName = fileName;
    }
}
