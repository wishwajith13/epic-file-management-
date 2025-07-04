package com.Epic.File.Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "file_value")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class fileValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long valueId;

    private Long fileId;

    private Long readId; // reference to fileRead

    private String studentId;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must only contain letters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private String address;

    private String gpa;
}
