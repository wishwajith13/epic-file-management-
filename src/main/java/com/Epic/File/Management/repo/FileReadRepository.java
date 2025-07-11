package com.Epic.File.Management.repo;

import com.Epic.File.Management.entity.fileRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileReadRepository extends JpaRepository<fileRead, Long> {
    List<fileRead> findByFileId(Long fileId);

    long countByFileIdAndStatus(Long fileId, String status);
}
