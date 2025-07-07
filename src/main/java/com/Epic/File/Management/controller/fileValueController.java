package com.Epic.File.Management.controller;

import com.Epic.File.Management.dto.fileValue.fileValueDTO;
import com.Epic.File.Management.service.FileValueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/value")
public class fileValueController {
    private final FileValueService service;

    public fileValueController(FileValueService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFileValues() {
        try {
            List<fileValueDTO> values = service.getAllFileValues();
            return ResponseEntity.ok(values);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
