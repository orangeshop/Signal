package com.ssafy.signal.file.controller;

import com.ssafy.signal.file.service.S3Uploader;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class FileController {
    @Autowired
    private final S3Uploader s3Uploader;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile,
                             @RequestParam("dirName") String dirName) throws IOException {
        return s3Uploader.upload(multipartFile, dirName);
    }
}
