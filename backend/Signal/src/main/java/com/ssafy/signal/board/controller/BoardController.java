package com.ssafy.signal.board.controller;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.LikeEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.LikeRepository;
import com.ssafy.signal.board.service.BoardService;
import com.ssafy.signal.board.service.DuplicateService;
import com.ssafy.signal.file.service.S3Uploader;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private DuplicateService duplicateService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private LikeRepository likeRepository;

    /* 게시글 목록 조회(오늘의 시그널) */
    @GetMapping("/board")
    public ResponseEntity<List<BoardDto>> listToday(@RequestParam(value="page", defaultValue = "0") Integer pageNum, @RequestParam(value="limit", defaultValue = "3") int limit) {
        List<BoardDto> boardList = duplicateService.getBoardList(pageNum, limit);
        return ResponseEntity.ok().body(boardList);
    }

    /* 게시글 목록 조회(화제의 시그널) */
    @GetMapping("/board/liked")

    public ResponseEntity<List<BoardDto>> listLiked(@RequestParam(value="page", defaultValue = "0") Integer pageNum, @RequestParam(value = "limit",defaultValue = "3") int limit) {
        List<BoardDto> boardList = duplicateService.getBoardListLiked(pageNum, limit);
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
        List<BoardDto> boardDtoList = duplicateService.searchPosts(keyword);
        return ResponseEntity.ok().body(boardDtoList);
    }

    @Transactional
    @PostMapping("/board/{boardId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestParam(value = "userId") Long userId, @PathVariable("boardId") Long boardId) {
        // Member 객체와 BoardEntity 객체를 가져옵니다.
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 좋아요 레코드를 조회합니다.
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndBoardEntity(user, board);

        boolean isLiked;
        if (existingLike.isPresent()) {
            // 좋아요가 이미 존재하면 삭제
            likeRepository.delete(existingLike.get()); // 좋아요 삭제
            board.decrementLiked();  // 좋아요 수 감소
            isLiked = false;  // 좋아요 상태를 false로 설정
        } else {
            // 좋아요가 존재하지 않으면 새로 추가
            LikeEntity like = new LikeEntity(user, board);
            likeRepository.save(like); // 좋아요 추가
            board.incrementLiked();  // 좋아요 수 증가
            isLiked = true;  // 좋아요 상태를 true로 설정
        }

        boardRepository.save(board); // 게시글의 좋아요 수를 갱신

        // 응답 데이터 준비
        Map<String, Object> response = new HashMap<>();
        response.put("likedCount", board.getLiked());  // 최신 좋아요 수
        response.put("isLiked", isLiked);  // 현재 좋아요 상태

        return ResponseEntity.ok(response);  // 좋아요 수와 상태를 함께 반환
    }


}
