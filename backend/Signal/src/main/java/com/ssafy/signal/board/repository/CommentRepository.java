package com.ssafy.signal.board.repository;

import com.ssafy.signal.board.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("SELECT c FROM CommentEntity c WHERE c.boardEntity.id = :boardId")
    List<CommentEntity> findByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT c FROM CommentEntity c WHERE c.boardEntity.id = :boardId AND c.id = :id")
    Optional<CommentEntity> findByBoardIdAndId(@Param("boardId") Long boardId, @Param("id") Long id);

    @Modifying
    @Query("DELETE FROM CommentEntity c WHERE c.boardEntity.id = :boardId AND c.id = :id")
    void deleteByBoardIdAndId(@Param("boardId") Long boardId, @Param("id") Long id);

}
