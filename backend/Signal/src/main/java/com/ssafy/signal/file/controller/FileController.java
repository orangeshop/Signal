package com.ssafy.signal.file.controller;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.service.BoardService;
import com.ssafy.signal.file.domain.FileDto;
import com.ssafy.signal.file.service.FileService;
import com.ssafy.signal.file.service.S3Uploader;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class FileController {
    @Autowired
    private final S3Uploader s3Uploader;

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileService fileService;


    // 게시판 파일 업로드
    @PostMapping("/board/{boardId}/upload")
    public String uploadBoardFile(@RequestParam("file") MultipartFile[] multipartFile,
                             @PathVariable("boardId") Long boardId) throws IOException {

        // FileService를 통해 파일 업로드 및 URL 반환
        return fileService.uploadBoardFile(multipartFile, boardId);
    }

    // 프로필 이미지 업로드
    @PostMapping("/user/{userId}/upload")
    public FileDto uploadProfileFile(@RequestParam("file") MultipartFile multipartFile,
                                     @PathVariable("userId") Long userId) throws IOException {

        return fileService.uploadProfileFile(multipartFile, userId);
    }

    // 파일 삭제
    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam("fileId") Long id) throws IOException {
        fileService.deleteFile(id);
        return "File deleted successfully";
    }

    // 모든 파일 조회 API
    @GetMapping("/files")
    public ResponseEntity<List<FileDto>> getAllFiles() {
        List<FileDto> files = fileService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // 프로필 이미지 수정
    @PutMapping("/user/{userId}/upload")
    public FileDto updateProfileFile(@RequestParam("file") MultipartFile multipartFile,
                                     @PathVariable("userId") Long userId) throws IOException {

        return fileService.updateProfileFile(multipartFile, userId);
    }


    @PutMapping("/board/{boardId}/upload")
    public String updateBoardFile(@RequestParam("file") MultipartFile[] multipartFile,
                                  @PathVariable("boardId") Long boardId) throws IOException {
        
        return fileService.updateBoardFile(multipartFile, boardId);
    }
}
