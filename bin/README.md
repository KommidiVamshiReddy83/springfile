# Spring Boot MVC: PDF/Excel Upload with MySQL

Features:
- Upload only **PDF, XLS, XLSX** with client + server validation.
- Files are saved to disk; **absolute storage path** is saved in MySQL (`uploaded_files.storage_path`).
- **Relocate** a stored file to a new directory and **update path in DB**.
- MVC layering: **Controller**, **Service**, **Repository**, **Model (Entity)**.
- Tech: Spring Boot 3, Thymeleaf, Spring Data JPA, MySQL.

## Quick start (IntelliJ IDEA / Eclipse)
1. Ensure Java 17+ and Maven are installed.
2. Create a MySQL DB:
   ```sql
   CREATE DATABASE fileuploader CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Update `src/main/resources/application.properties` with your MySQL username/password.
4. (Optional) Change `file.storage.root=uploads` to an absolute path if you want.
5. Run:
   ```bash
   mvn spring-boot:run
   ```
6. Open http://localhost:8080 and upload a PDF/XLS/XLSX.

## API Notes
- `POST /upload` (multipart): stores file on disk and inserts row with the storage path.
- `POST /relocate` (form params: `id`, `newDirectory`): moves existing file to a new directory and updates its path in DB.

## Why both client and server validation?
- Client-side prevents accidental wrong files.
- Server-side checks extension **and** basic file signatures (PDF magic, XLSX zip header, legacy XLS).

## Project Structure
```
controller/ FileController.java
service/    FileStorageService.java
repo/       UploadedFileRepository.java
model/      UploadedFile.java
util/       FileTypeUtil.java
templates/  upload.html
```
