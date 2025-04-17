package com.assignment.ecommerce_rookie.controller;

import com.assignment.ecommerce_rookie.service.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/upload")
public class ImageUploadController {

    private final AwsS3Service awsS3Service;

    public ImageUploadController(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @PostMapping("/images")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> imgUrls = Arrays.stream(files)
                .map(awsS3Service::saveImageToS3)
                .collect(Collectors.toList());

        return new ResponseEntity(imgUrls, HttpStatus.OK);
    }
}
