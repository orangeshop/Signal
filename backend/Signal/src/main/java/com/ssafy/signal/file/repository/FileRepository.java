package com.ssafy.signal.file.repository;

import com.ssafy.signal.file.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
