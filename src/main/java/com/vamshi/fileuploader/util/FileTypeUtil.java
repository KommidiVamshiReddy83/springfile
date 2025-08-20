package com.vamshi.fileuploader.util;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeUtil {
    public static boolean isAllowed(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (name == null) return false;
        String lower = name.toLowerCase();
        boolean extOk = lower.endsWith(".pdf") || lower.endsWith(".xlsx") || lower.endsWith(".xls");
        if (!extOk) return false;

        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(8);
            // PDF magic: 25 50 44 46 2D -> "%PDF-"
            if (header.length >= 5 &&
                header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46 && header[4] == 0x2D) {
                return true;
            }
            // XLSX begins with PK zip
            if (header.length >= 2 && header[0] == 0x50 && header[1] == 0x4B) {
                return true;
            }
            // Legacy XLS (CFB): D0 CF 11 E0
            if (header.length >= 4 && (header[0] & 0xFF) == 0xD0 && (header[1] & 0xFF) == 0xCF &&
                (header[2] & 0xFF) == 0x11 && (header[3] & 0xFF) == 0xE0) {
                return true;
            }
        }
        return false;
    }
}
