package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.member.domain.Member;
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
    private final FileRepository fileRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByBoardId(Long boardId) {
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(boardId);
        List<CommentDto> commentDtoList = new ArrayList<>();


        for (CommentEntity commentEntity : commentEntities) {
            FileEntity file = fileRepository.findAllByUser(commentEntity.getUserId());
            String url =  file == null ? null : file.getFileUrl();
            commentDtoList.add(commentEntity.asCommentDto(url));
        }
        return commentDtoList;
    }

    @Transactional
    public CommentDto saveComment(CommentDto commentDto) {
        BoardEntity boardEntity = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        return commentRepository.save(commentDto.toEntity()).asCommentDto();
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

        return commentEntity.asCommentDto();
    }



    public CommentEntity getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));
    }
}
