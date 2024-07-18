package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardEntity;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.annotations.NotFound;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Transactional
    public List<CommentDto> getCommentsByBoardId(Long boardId) {
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(boardId);
        List<CommentDto> commentDtoList = new ArrayList<>();

        for (CommentEntity commentEntity : commentEntities) {
            commentDtoList.add(this.convertEntityToDto(commentEntity));
        }
        return commentDtoList;
    }

    @Transactional
    public Long saveComment(CommentDto commentDto) {
        BoardEntity boardEntity = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        CommentEntity commentEntity = commentDto.toEntity(boardEntity);
        return commentRepository.save(commentEntity).getId();
    }

    @Transactional
    public ResponseEntity<String> deleteComment(Long boardId, Long id) {
        try {
            commentRepository.deleteByBoardIdAndId(boardId, id);
            return ResponseEntity.ok("성공적으로 삭제되었습니다");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    public CommentDto updateComment(Long boardId, Long id, CommentDto updatedCommentDto) {
        Optional<CommentEntity> commentEntityOptional = commentRepository.findByBoardIdAndId(boardId, id);
        if (!commentEntityOptional.isPresent()) {
            throw new EntityNotFoundException("Comment not found with boardId: " + boardId + " and id: " + id);
        }
        CommentEntity commentEntity = commentEntityOptional.get();

        // 업데이트 메서드를 호출하여 DTO에서 받은 필드들을 업데이트
        commentEntity.updateFromDto(updatedCommentDto);

        commentRepository.save(commentEntity);

        return convertEntityToDto(commentEntity);
    }

    private CommentDto convertEntityToDto(CommentEntity commentEntity) {
        return CommentDto.builder()
                .id(commentEntity.getId())
                .boardId(commentEntity.getBoard().getId())
                .writer(commentEntity.getWriter())
                .content(commentEntity.getContent())
                .createdDate(commentEntity.getCreatedDate())
                .modifiedDate(commentEntity.getModifiedDate())
                .build();
    }
}
