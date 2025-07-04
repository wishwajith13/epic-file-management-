package com.Epic.File.Management.repo;

import com.Epic.File.Management.entity.fileRead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileReadRepository extends JpaRepository<fileRead, Long> {
}