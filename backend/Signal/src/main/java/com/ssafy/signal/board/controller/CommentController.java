package com.ssafy.signal.board.controller;

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

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
