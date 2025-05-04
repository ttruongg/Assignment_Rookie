package com.assignment.ecommerce_rookie.controller.admin;

import com.assignment.ecommerce_rookie.service.impl.AwsS3ServiceImpl;
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
@RequestMapping("/api/v1/admin/images")
public class ImageUploadController {

    private final AwsS3ServiceImpl awsS3Service;

    public ImageUploadController(AwsS3ServiceImpl awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @PostMapping
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> imgUrls = Arrays.stream(files)
                .map(awsS3Service::saveImageToS3)
                .collect(Collectors.toList());

        return new ResponseEntity(imgUrls, HttpStatus.OK);
    }
}
