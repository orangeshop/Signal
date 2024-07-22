package com.ssafy.signal.board.repository;

import com.ssafy.signal.board.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByBoardId(Long boardId);

    Optional<CommentEntity> findByBoardIdAndId(Long boardId, Long id);

    void deleteByBoardIdAndId(Long boardId, Long id);

}
