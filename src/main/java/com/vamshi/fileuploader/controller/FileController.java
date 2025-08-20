package com.vamshi.fileuploader.controller;

import com.vamshi.fileuploader.exception.FileValidationException;
import com.vamshi.fileuploader.model.UploadedFile;
import com.vamshi.fileuploader.service.FileStorageService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@Validated
public class FileController {
    private final FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String uploadForm(Model model) {
        List<UploadedFile> files = storageService.listAll();
        model.addAttribute("files", files);
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        storageService.store(file);
        model.addAttribute("success", "File uploaded successfully!");
        model.addAttribute("files", storageService.listAll());
        return "upload";
    }

    @PostMapping("/relocate")
    public String relocate(@RequestParam("id") Long id, @RequestParam("newDirectory") @NotBlank String newDirectory, Model model) {
        storageService.relocate(id, newDirectory);
        model.addAttribute("success", "File moved and path updated!");
        model.addAttribute("files", storageService.listAll());
        return "upload";
    }
}
