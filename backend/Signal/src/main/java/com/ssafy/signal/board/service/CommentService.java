package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByBoardId(Long boardId) {
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(boardId);
        List<CommentDto> commentDtoList = new ArrayList<>();

        for (CommentEntity commentEntity : commentEntities) {
            commentDtoList.add(convertEntityToDto(commentEntity));
        }
        return commentDtoList;
    }

    @Transactional
    public Long saveComment(CommentDto commentDto) {
        BoardEntity boardEntity = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        // CommentEntity를 생성하여 저장합니다.
        CommentEntity commentEntity = CommentEntity.builder()
                .boardEntity(boardEntity)
                .writer(commentDto.getWriter())
                .content(commentDto.getContent())
                .build();

        return commentRepository.save(commentEntity).getId();
    }

    @Transactional
    public ResponseEntity<String> deleteComment(Long boardId, Long id) {
        try {
            commentRepository.deleteByBoardIdAndId(boardId, id);
            return ResponseEntity.ok("Comment successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    public CommentDto updateComment(Long boardId, Long id, CommentDto updatedCommentDto) {
        Optional<CommentEntity> commentEntityOptional = commentRepository.findByBoardIdAndId(boardId, id);
        if (commentEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Comment not found with boardId: " + boardId + " and id: " + id);
        }

        CommentEntity commentEntity = commentEntityOptional.get();
        commentEntity.updateFromDto(updatedCommentDto);

        commentRepository.save(commentEntity);

        return convertEntityToDto(commentEntity);
    }

    private CommentDto convertEntityToDto(CommentEntity commentEntity) {
        return CommentDto.builder()
                .id(commentEntity.getId())
                .boardId(commentEntity.getBoardEntity().getId()) // boardEntity에서 ID를 가져옵니다.
                .writer(commentEntity.getWriter())
                .content(commentEntity.getContent())
                .createdDate(commentEntity.getCreatedDate())
                .modifiedDate(commentEntity.getModifiedDate())
                .build();
    }

    public CommentEntity getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));
    }
}
