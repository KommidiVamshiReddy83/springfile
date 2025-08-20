package com.vamshi.fileuploader.service;

import com.vamshi.fileuploader.exception.FileValidationException;
import com.vamshi.fileuploader.model.UploadedFile;
import com.vamshi.fileuploader.repo.UploadedFileRepository;
import com.vamshi.fileuploader.util.FileTypeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

@Service
public class FileStorageService {
    private final UploadedFileRepository repository;

    @Value("{file.storage.root:uploads}")
    private String storageRoot;

    public FileStorageService(UploadedFileRepository repository) {
        this.repository = repository;
    }

    public List<UploadedFile> listAll() {
        return repository.findAll();
    }

    @Transactional
    public UploadedFile store(MultipartFile file) {
        try {
            if (file.isEmpty()) throw new FileValidationException("File is empty.");
            if (!FileTypeUtil.isAllowed(file)) throw new FileValidationException("Only PDF, XLS, or XLSX files are allowed.");

            Path root = Paths.get(storageRoot).toAbsolutePath().normalize();
            Files.createDirectories(root);

            String safeName = Path.of(file.getOriginalFilename()).getFileName().toString();
            String timestamp = String.valueOf(System.currentTimeMillis());
            Path destination = root.resolve(timestamp + "_" + safeName);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            String checksum = sha256(Files.readAllBytes(destination));

            UploadedFile meta = new UploadedFile();
            meta.setOriginalFilename(safeName);
            meta.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
            meta.setSize(file.getSize());
            meta.setStoragePath(destination.toString());
            meta.setChecksum(checksum);
            meta.setUploadedAt(Instant.now());
            meta.setUpdatedAt(Instant.now());

            return repository.save(meta);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Transactional
    public UploadedFile relocate(Long id, String newDirectory) {
        UploadedFile meta = repository.findById(id).orElseThrow(() -> new FileValidationException("File id not found: " + id));
        try {
            Path currentPath = Paths.get(meta.getStoragePath());
            if (!Files.exists(currentPath)) throw new FileValidationException("Current file not found on disk.");

            Path newDir = Paths.get(newDirectory).toAbsolutePath().normalize();
            Files.createDirectories(newDir);
            Path newPath = newDir.resolve(currentPath.getFileName());

            Files.move(currentPath, newPath, StandardCopyOption.REPLACE_EXISTING);

            meta.setStoragePath(newPath.toString());
            meta.setUpdatedAt(Instant.now());
            return repository.save(meta);
        } catch (IOException e) {
            throw new RuntimeException("Failed to relocate file: " + e.getMessage(), e);
        }
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            return null;
        }
    }
}
