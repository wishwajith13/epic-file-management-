package com.Epic.File.Management.repo;

import com.Epic.File.Management.entity.filesUploade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileManagementRepository extends JpaRepository<filesUploade, Long> {
    Optional<filesUploade> findByFileName(String fileName);
    //Returns an Optional to safely handle cases where no file is found
}
