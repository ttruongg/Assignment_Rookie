package com.assignment.ecommerce_rookie.service;

import org.springframework.web.multipart.MultipartFile;

public interface IAwsS3Service {
    String saveImageToS3(MultipartFile photo);
}
