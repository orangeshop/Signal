package com.ssafy.signal.file.repository;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findAllByUser(Member userId);
    List<FileEntity> findByBoardId(Long boardId);
    Optional<FileEntity> findByUser_UserId(Long userId);
    @Modifying
    @Query("DELETE FROM FileEntity f WHERE f.fileUrl = :fileUrl")
    void deleteByFileUrl(@Param("fileUrl") String fileUrl);
}
