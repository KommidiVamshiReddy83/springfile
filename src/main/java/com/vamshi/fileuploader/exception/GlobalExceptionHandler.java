package com.vamshi.fileuploader.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FileValidationException.class)
    public String handleValidation(FileValidationException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "upload";
    }

    @ExceptionHandler(Exception.class)
    public String handleOther(Exception ex, Model model) {
        model.addAttribute("error", "Unexpected error: " + ex.getMessage());
        return "upload";
    }
}
