package com.ssafy.signal.board.controller;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.service.BoardService;
import com.ssafy.signal.board.service.DuplicateService;
import com.ssafy.signal.file.service.S3Uploader;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private DuplicateService duplicateService;
    @Autowired
    private final S3Uploader s3Uploader;

    /* 게시글 목록 조회(오늘의 시그널) */
    @GetMapping("/board")
    public ResponseEntity<List<BoardDto>> listToday(@RequestParam(value="page", defaultValue = "0") Integer pageNum, @RequestParam(defaultValue = "3") int limit) {
        List<BoardDto> boardList = boardService.getBoardList(pageNum, limit);
        Integer[] pageList = boardService.getPageList(pageNum);

        return ResponseEntity.ok().body(boardList);
    }

    /* 게시글 목록 조회(화제의 시그널) */
    @GetMapping("/board/liked")
    public ResponseEntity<List<BoardDto>> listLiked(@RequestParam(value="page", defaultValue = "0") Integer pageNum, @RequestParam(defaultValue = "3") int limit) {
        List<BoardDto> boardList = boardService.getBoardListLiked(pageNum, limit);
        Integer[] pageList = boardService.getPageList(pageNum); // 페이지 목록 필요시 사용할 수 있습니다.

        return ResponseEntity.ok().body(boardList);
    }
    /* 게시글 상세 */
    @GetMapping("/board/{no}")
    public BoardDto detail(@PathVariable("no") Long no) {
        boardService.incrementReferenceCount(no);
        BoardDto boardDto = duplicateService.getPost(no);
        return boardDto;
    }


    /* 게시글 쓰기 */
    @PostMapping("/post")
    public BoardDto write(@RequestBody BoardDto boardDto) {
        return boardService.savePost(boardDto);
    }

    /* 게시글 수정 */
    @PutMapping("/board/update/{no}")
    public BoardDto update(@PathVariable Long no, @RequestBody  BoardDto boardDto) {
        return boardService.updatePost(no, boardDto);
    }

    /* 게시글 삭제 */
    @DeleteMapping("/delete/{no}")
    public ResponseEntity<String> delete(@PathVariable("no") Long no) {
        boardService.deletePost(no);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
    }

    /* 게시글 검색 */
    @GetMapping("/board/search")
    public ResponseEntity<List<BoardDto>> search(@RequestParam(value="keyword") String keyword) {
        List<BoardDto> boardDtoList = boardService.searchPosts(keyword);
        return ResponseEntity.ok().body(boardDtoList);
    }

    /* 좋아요 버튼 클릭 */
    @PostMapping("/board/{no}/like")
    public ResponseEntity<Long> likePost(@PathVariable("no") Long no) {
        Long likedCount = boardService.incrementLikedCount(no);  // 좋아요 카운트 증가 및 반환
        return ResponseEntity.ok(likedCount);  // 증가된 좋아요 수 반환
    }



}
