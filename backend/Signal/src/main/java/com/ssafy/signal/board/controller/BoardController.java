package com.ssafy.signal.board.controller;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class BoardController {

    @Autowired
    private BoardService boardService;

    /* 게시글 목록 */
    @GetMapping("/board")
    public ResponseEntity<List<BoardDto>> list(@RequestParam(value="page", defaultValue = "1") Integer pageNum) {
        List<BoardDto> boardList = boardService.getBoardList(pageNum);
        Integer[] pageList = boardService.getPageList(pageNum);

        return ResponseEntity.ok().body(boardList);
    }

    /* 게시글 상세 */
    @GetMapping("/board/{no}")
    public BoardDto detail(@PathVariable("no") Long no) {
        BoardDto boardDto = boardService.getPost(no);
        return boardDto;
    }


    /* 게시글 쓰기 */
    @GetMapping("/post")
    public String write() {
        return "";
    }

    @PostMapping("/post")
    public BoardDto write(BoardDto boardDto) {
        return boardService.savePost(boardDto);
    }

    /* 게시글 수정 */
    @GetMapping("/board/update/{no}")
    public BoardDto update(@PathVariable("no") Long no) {
        return boardService.getPost(no);
    }

    @PutMapping("/board/update/{no}")
    public BoardDto update(@PathVariable Long no, BoardDto boardDto) {
        return boardService.updatePost(no, boardDto);

    }

    /* 게시글 삭제 */
    @DeleteMapping("/delete/{no}")
    public ResponseEntity<String> delete(@PathVariable("no") Long no) {
        boardService.deletePost(no);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
    }

    @GetMapping("/board/search")
    public String search(@RequestParam(value="keyword") String keyword, Model model) {
        List<BoardDto> boardDtoList = boardService.searchPosts(keyword);

        model.addAttribute("boardList", boardDtoList);

        return "";
    }
}
