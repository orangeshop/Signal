package com.ssafy.signal.board.controller;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.repository.TagRepository;
import com.ssafy.signal.board.service.TagService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.common.reflection.qual.GetClass;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TagController {

    private final TagService tagService;

    @GetMapping("/tag/recent")
    public List<BoardDto> getBoardByTagRecent(
            @RequestParam("tag") String tag,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit)
    {
        List<BoardDto> boardtag = tagService.getBoardByTagRecent(tag,page,limit);
//        log.info("tag recent : {}", boardtag);
        return boardtag;
    }

    @GetMapping("/tag/hot")
    public List<BoardDto> getBoardByTagHot(
            @RequestParam("tag") String tag,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit)
    {
        return tagService.getBoardByTagHot(tag,page,limit);
    }
}
