package com.vanessaduldier.xiaoshuguan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vanessa Duldier
 */
public class Book {
    private Long id;
    private String title;
    private List<Author> authors;
    private List<String> genres;
    private String goodreadsLink;

    private String description;
    private String publisher;
    private String isbn;
    private String language;
    private String coverImage;
    private String filePath;
    private Integer pageCount;
    private String publishedDate;

    public Book(Long id, String title, List<Author> authors, List<String> genres) {
        this.id = id;
        this.title = title;
        this.authors = authors != null ? authors : new ArrayList<>();
        this.genres = genres != null ? genres : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getAuthorName() {
        if (authors == null || authors.isEmpty()) {
            return "Unbekannt";
        }
        return authors.get(0).getName(); // Ersten Autor als Hauptautor
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGoodreadsLink() {
        if (goodreadsLink == null || goodreadsLink.isEmpty()) {
            return goodreadsLink;
        }
        return "no link has been added";
    }

    public void setGoodreadsLink(String goodreadsLink) {
        this.goodreadsLink = goodreadsLink;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author=" + getAuthorName() +
                ", genres=" + genres +
                '}';
    }
}