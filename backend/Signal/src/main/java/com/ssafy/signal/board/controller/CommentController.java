package com.ssafy.signal.board.controller;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/comment/{boardId}")
    public List<CommentDto> getCommentsByBoardId(@PathVariable Long boardId) {
        return commentService.getCommentsByBoardId(boardId);
    }

    @PostMapping("/comment/{boardId}")
    public Long createComment(CommentDto commentDto) {

        return commentService.saveComment(commentDto);
    }

    @PutMapping("/comment/{boardId}/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long boardId,
                                                    @PathVariable Long id,
                                                    CommentDto updatedCommentDto) {
        try {
            CommentDto result = commentService.updateComment(boardId, id, updatedCommentDto);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 또는 다른 적절한 처리 방법
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 또는 다른 적절한 처리 방법
        }
    }



    @DeleteMapping("comment/{boardId}/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long boardId, @PathVariable Long id) {
       return commentService.deleteComment(boardId, id);
    }
}
