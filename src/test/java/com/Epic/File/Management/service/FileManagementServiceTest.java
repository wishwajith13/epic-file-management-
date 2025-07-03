package com.Epic.File.Management.service;

import com.Epic.File.Management.dto.fileUpload.fileRecodeDTO;
import com.Epic.File.Management.dto.fileUpload.fileUploadDTO;
import com.Epic.File.Management.entity.filesUploade;
import com.Epic.File.Management.repo.FileManagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FileManagementServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private FileManagementRepository repository;

    @InjectMocks
    private FileManagementService service;

    @BeforeEach
    void setUp() {
        // Override the injected value of uploadDir
        service = new FileManagementService(repository);
        service.uploadDir = tempDir.toString();
    }

    @Test
    void storeFiles_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("testFile.txt");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("Hello".getBytes()));

        when(repository.findByFileName("testFile.txt")).thenReturn(Optional.empty());
        filesUploade savedEntity = new filesUploade("testFile.txt");
        savedEntity.setFileId(1L);
        when(repository.save(any(filesUploade.class))).thenReturn(savedEntity);

        List<fileUploadDTO> result = service.storeFiles(new MultipartFile[]{file});

        assertEquals(1, result.size());
        fileUploadDTO dto = result.get(0);
        assertEquals(1L, dto.getFileId());
        assertEquals("testFile.txt", dto.getFileName());
        assertEquals("SUCCESS", dto.getStatus());
        assertTrue(Files.exists(tempDir.resolve("testFile.txt")));
    }

    @Test
    void storeFiles_duplicateFile_throwsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("duplicate.txt");
        when(repository.findByFileName("duplicate.txt"))
                .thenReturn(Optional.of(new filesUploade("duplicate.txt")));

        List<fileUploadDTO> result = service.storeFiles(new MultipartFile[]{file});

        assertEquals(1, result.size());
        assertEquals("FAILED", result.get(0).getStatus());
        assertTrue(result.get(0).getMessage().contains("Duplicate file"));
    }

    @Test
    void deleteFile_success() throws IOException {
        String filename = "toDelete.txt";
        Files.write(tempDir.resolve(filename), "data".getBytes());

        filesUploade entity = new filesUploade(filename);
        when(repository.findByFileName(filename)).thenReturn(Optional.of(entity));

        String result = service.deleteFile(filename);

        assertEquals("File and database record deleted successfully.", result);
        assertFalse(Files.exists(tempDir.resolve(filename)));
        verify(repository).delete(entity);
    }

    @Test
    void deleteFile_fileNotFoundOnDisk() {
        String filename = "missing.txt";

        filesUploade entity = new filesUploade(filename);
        when(repository.findByFileName(filename)).thenReturn(Optional.of(entity));

        String result = service.deleteFile(filename);

        assertEquals("File record deleted from database. File not found on disk.", result);
        verify(repository).delete(entity);
    }

    @Test
    void deleteFile_nullFilename_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteFile(null)
        );
        assertEquals("File name must be provided.", ex.getMessage());
    }

    @Test
    void deleteFile_fileNotFoundInDb_throwsException() {
        when(repository.findByFileName("notfound.txt")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteFile("notfound.txt")
        );
        assertEquals("No file found with name: notfound.txt", ex.getMessage());
    }

    @Test
    void getAllFiles_success() {
        filesUploade file1 = new filesUploade("file1.txt");
        file1.setFileId(1L);
        filesUploade file2 = new filesUploade("file2.txt");
        file2.setFileId(2L);

        when(repository.findAll()).thenReturn(List.of(file1, file2));

        List<fileRecodeDTO> result = service.getAllFiles();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getFileId());
        assertEquals("file1.txt", result.get(0).getFileName());
        assertEquals(2L, result.get(1).getFileId());
        assertEquals("file2.txt", result.get(1).getFileName());
    }
}
