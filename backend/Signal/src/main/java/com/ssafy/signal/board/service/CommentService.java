package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
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
