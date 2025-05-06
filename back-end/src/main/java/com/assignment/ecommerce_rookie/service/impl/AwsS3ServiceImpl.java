package com.assignment.ecommerce_rookie.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.assignment.ecommerce_rookie.exception.APIException;
import com.assignment.ecommerce_rookie.service.IAwsS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


@Service
public class AwsS3ServiceImpl implements IAwsS3Service {

    private final String bucketName = "ecommerce-rookie";
    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Override
    public String saveImageToS3(MultipartFile photo) {
        String s3LocationImage = null;

        try {
            String fileName = photo.getOriginalFilename();
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();

            byte[] bytes = photo.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setContentLength(bytes.length);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            putObjectRequest.getRequestClientOptions().setReadLimit(bytes.length);
            s3Client.putObject(putObjectRequest);
            return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new APIException("Unable to upload image to s3 bucket: " + ex.getMessage());
        }

    }

}
