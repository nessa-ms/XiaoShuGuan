package com.vanessaduldier.xiaoshuguan.service;

import com.vanessaduldier.xiaoshuguan.model.Book;

import java.io.File;

/**
 * Use XiaoShuGuan to load books into your kobo/tolino via usb connection
 * @author Vanessa Duldier
 */
public class TolinoService {
    private final String pathToEreader;
    private final FileStorageService fileStorageService;

    public TolinoService(String path) {
        this.pathToEreader = path;
        this.fileStorageService = new FileStorageService();
    }

    /**
     * Load epub onto kobo/tolino
     */
    public void importBook(Book book) {
        File epubFile = fileStorageService.getEpubFile(book);
    }
}
