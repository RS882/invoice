package com.billing.invoice.services;

import com.billing.invoice.domain.model.InvoiceDataForFile;
import com.billing.invoice.exception_handler.exceptions.server_exception.ServerIOException;
import com.billing.invoice.exception_handler.exceptions.server_exception.exceptions.MinioException;
import com.billing.invoice.services.interfaces.DataStorageService;
import com.billing.invoice.services.interfaces.PdfGeneratorService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataStorageServiceImpl implements DataStorageService {

    private final MinioClient minioClient;
    private final PdfGeneratorService pdfGeneratorService;

    @Value("${bucket.name}")
    private String bucketName;

    @PostConstruct
    public void initialize() {
        checkAndCreateBucket(bucketName);
    }

    @Override
    public String uploadFile(InvoiceDataForFile data, Long customerId) {
        try (InputStream pdfStream = pdfGeneratorService.generatePdf(data)) {

            String filePath = String.format("%d/%d/%s.pdf", customerId, data.getInvoiceNumber(), UUID.randomUUID());
            byte[] pdfBytes;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = pdfStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                pdfBytes = outputStream.toByteArray();
            }

            putFile(pdfBytes,filePath);

            return getFileUrl(filePath);

        } catch (IOException e) {
            log.error("I/O error while generating or uploading PDF", e);
            throw new ServerIOException("Error processing PDF: "+ e.getMessage());
        }
    }

    private void putFile(byte[] pdfBytes, String filePath){
        try (InputStream pdfInputStream = new ByteArrayInputStream(pdfBytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .stream(pdfInputStream, pdfBytes.length, -1)
                            .contentType(MediaType.APPLICATION_PDF_VALUE)
                            .build());

            log.info("File uploaded: {}", filePath);

        } catch (Exception e) {
            log.error("Error uploading invoice to MinIO", e);
            throw new MinioException("Error uploading file: "+ e.getMessage());
        }
    }

    private String getFileUrl(String filePath) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filePath)
                            .build());
        } catch (Exception e) {
            log.error("Failed to generate URL for file: {}", filePath, e);
            throw new MinioException("Error generating file URL: "+ e.getMessage());
        }
    }

    private boolean isBucketExist(String bucketName) {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
        } catch (Exception e) {
            log.error("Error checking bucket existence: {}", bucketName, e);
            throw new MinioException("Error checking bucket existence: "+ e.getMessage());
        }
    }

    private void checkAndCreateBucket(String bucketName) {
        if (!isBucketExist(bucketName)) {
            try {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build());
                log.info("Bucket created: {}", bucketName);
            } catch (Exception e) {
                log.error("Failed to create bucket: {}", bucketName, e);
                throw new MinioException("Error creating bucket: "+ e.getMessage());
            }
        }
    }
}