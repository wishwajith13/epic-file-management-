package com.Epic.File.Management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_read")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class fileRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readId;

    private Long fileId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String status;

    public Long getId() {
        return null;
    }
}