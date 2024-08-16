package com.ssafy.signal.board.repository;

import com.ssafy.signal.board.domain.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    List<BoardEntity> findByTitleContaining(String keyword);
    @Query("SELECT b FROM BoardEntity b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    List<BoardEntity> searchByTitleOrContent(@Param("keyword") String keyword);
}