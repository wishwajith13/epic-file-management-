package com.Epic.File.Management.repo;

import com.Epic.File.Management.entity.fileValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileValueRepository extends JpaRepository<fileValue, Long> {

    Optional<fileValue> findByReadId(Long readId);
}
