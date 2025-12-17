package com.vanessaduldier.xiaoshuguan.service;

import com.vanessaduldier.xiaoshuguan.dao.BookDao;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.parser.EpubParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Import Epub into App
 */
public class BookImportService {
    private final EpubParser epubParser;
    private final BookDao bookDao;
    private final FileStorageService fileStorageService;

    public BookImportService() {
        this.epubParser = new EpubParser();
        this.bookDao = new BookDao();
        this.fileStorageService = new FileStorageService();
    }

    /**
     * Importiert eine EPUB-Datei in die Bibliothek
     */
    public Book importEpub(File epubFile) throws ImportException {
        try {
            System.out.println("Importiere: " + epubFile.getName());

            // 1. EPUB parsen
            Book book = epubParser.parse(epubFile);
            System.out.println("Geparst: " + book.getTitle());

            // 2. Datei in Library-Ordner kopieren
            File storedFile = fileStorageService.storeEpub(epubFile, book);
            book.setFilePath(storedFile.getAbsolutePath());

            // 3. In Datenbank speichern
            Long bookId = bookDao.insert(book);
            book.setId(bookId);

            System.out.println("Import erfolgreich: " + book.getTitle() + " (ID: " + bookId + ")");

            return book;

        } catch (Exception e) {
            throw new ImportException("Fehler beim Import von " + epubFile.getName(), e);
        }
    }

    public static class ImportException extends Exception {
        public ImportException(String message) {
            super(message);
        }

        public ImportException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}