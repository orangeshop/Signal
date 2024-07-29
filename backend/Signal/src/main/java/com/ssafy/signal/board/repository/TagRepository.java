package com.ssafy.signal.board.repository;

import com.ssafy.signal.board.domain.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
    TagEntity findByTagName(String tagName);



}
