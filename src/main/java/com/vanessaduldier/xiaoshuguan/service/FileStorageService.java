package com.vanessaduldier.xiaoshuguan.service;

import com.vanessaduldier.xiaoshuguan.model.Book;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Save Epub files in xiaoshuguan_library folder
 * @author Vanessa Duldier
 */
public class FileStorageService {
    private static final String LIBRARY_DIR = "src/main/resources/xiaoshuguan_library";

    public FileStorageService() {
        // create library directory
        File libraryDir = new File(LIBRARY_DIR);
        if (!libraryDir.exists()) {
            libraryDir.mkdirs();
        }
    }

    /**
     * Save epub in xiaoshuguan_library folder
     */
    public File storeEpub(File epubFile, Book book) throws IOException {

        String safeFileName = createSafeFileName(book);
        Path targetPath = Paths.get(LIBRARY_DIR, safeFileName);

        Files.copy(epubFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toFile();
    }

    /**
     * Delete Epub file from library folder
     * @param book Book
     */
    public void deleteEpub(Book book) {
        if (book.getFilePath() != null) {
            File file = new File(book.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private String createSafeFileName(Book book) {

        String title = book.getTitle().replaceAll("[^a-zA-Z0-9äöüÄÖÜß\\s-]", "");
        String author = book.getAuthorName().replaceAll("[^a-zA-Z0-9äöüÄÖÜß\\s-]", "");

        if (title.length() > 50) {
            title = title.substring(0, 50);
        }
        if (author.length() > 30) {
            author = author.substring(0, 30);
        }

        return author + " - " + title + ".epub";
    }

    public File getEpubFile(Book book) {
        if (book.getFilePath() == null) {
            return null;
        }
        return new File(book.getFilePath());
    }

    /**
     * exports an Epub file to an Ereader given the book and device root (path)
     * @param book Book
     * @param deviceRoot Filepath
     * @throws IOException if the epub file cant be found
     */
    public void exportEpubToEreader(Book book, File deviceRoot) throws IOException {
        File epub = getEpubFile(book);
        if (epub == null || !epub.exists()) {
            throw new IOException("EPUB file not found");
        }

        File targetDir = new File(deviceRoot, "documents");
        if (!targetDir.exists()) {
            targetDir = deviceRoot; // fallback
        }

        Path targetPath = targetDir.toPath().resolve(epub.getName());
        Files.copy(epub.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}