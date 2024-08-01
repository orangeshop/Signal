package com.ssafy.signal.file.repository;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findAllByUser(Member userId);
    List<FileEntity> findByBoardId(Long boardId);
}
