package com.vamshi.fileuploader.repo;

import com.vamshi.fileuploader.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
