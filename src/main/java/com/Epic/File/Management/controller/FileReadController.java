package com.Epic.File.Management.controller;

import com.Epic.File.Management.dto.fileRead.UpdateContentDTO;
import com.Epic.File.Management.dto.fileRead.fileReadDTO;
import com.Epic.File.Management.service.FileReadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/read")
public class FileReadController {

    private final FileReadService service;

    public FileReadController(FileReadService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFiles() {
        try {
            List<fileReadDTO> files = service.getAllFileReads();
            return ResponseEntity.ok(files);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.internalServerError().body("Error retrieving files.");
        }
    }

    @PatchMapping("/update-content/{readId}")
    public ResponseEntity<?> updateFileContent(
            @Valid
            @PathVariable Long readId,
            @RequestBody UpdateContentDTO dto
    ) {
        try {
            fileReadDTO updated = service.updateFileReadContent(readId, dto.getContent());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error updating content.");
        }
    }

}
