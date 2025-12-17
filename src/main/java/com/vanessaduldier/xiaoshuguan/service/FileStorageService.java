package com.vanessaduldier.xiaoshuguan.service;

import com.vanessaduldier.xiaoshuguan.model.Book;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileStorageService {
    private static final String LIBRARY_DIR = "xiaoshuguan_library";

    public FileStorageService() {
        // Library-Verzeichnis erstellen
        File libraryDir = new File(LIBRARY_DIR);
        if (!libraryDir.exists()) {
            libraryDir.mkdirs();
        }
    }

    /**
     * Speichert eine EPUB-Datei im Library-Ordner
     */
    public File storeEpub(File epubFile, Book book) throws IOException {
        // Erstelle einen sicheren Dateinamen
        String safeFileName = createSafeFileName(book);
        Path targetPath = Paths.get(LIBRARY_DIR, safeFileName);

        // Kopiere die Datei
        Files.copy(epubFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toFile();
    }

    private String createSafeFileName(Book book) {
        // Erstelle einen Dateinamen aus Titel und Autor
        String title = book.getTitle().replaceAll("[^a-zA-Z0-9äöüÄÖÜß\\s-]", "");
        String author = book.getAuthorName().replaceAll("[^a-zA-Z0-9äöüÄÖÜß\\s-]", "");

        // Kürze falls zu lang
        if (title.length() > 50) {
            title = title.substring(0, 50);
        }
        if (author.length() > 30) {
            author = author.substring(0, 30);
        }

        return author + " - " + title + ".epub";
    }

    /**
     * Holt die gespeicherte EPUB-Datei
     */
    public File getEpubFile(Book book) {
        if (book.getFilePath() == null) {
            return null;
        }
        return new File(book.getFilePath());
    }
}