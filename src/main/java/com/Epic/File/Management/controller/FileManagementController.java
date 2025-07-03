package com.Epic.File.Management.controller;

import com.Epic.File.Management.dto.fileUpload.fileRecodeDTO;
import com.Epic.File.Management.dto.fileUpload.fileUploadDTO;
import com.Epic.File.Management.service.FileManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileManagementController {

    private final FileManagementService service;

    public FileManagementController(FileManagementService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<fileUploadDTO> responses = service.storeFiles(files);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error uploading files.");
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        try {
            String message = service.deleteFile(fileName);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error deleting file.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFiles() {
        List<fileRecodeDTO> files = service.getAllFiles();
        return ResponseEntity.ok(files);
    }

}
