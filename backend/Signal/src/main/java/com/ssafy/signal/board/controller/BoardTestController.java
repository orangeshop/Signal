package com.ssafy.signal.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardTestController {

    @GetMapping("/board")
    public String getBoard()
    {
        return "Hello World";
    }
}
