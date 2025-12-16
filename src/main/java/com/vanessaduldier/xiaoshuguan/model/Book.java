package com.vanessaduldier.xiaoshuguan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vanessa Duldier
 */
public class Book {
    private final Long id;
    private String title;
    private final List<Author> authors;
    private List<String> genres;

    public Book(Long id, String title, List<Author> authors, List<String> genres) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.genres = genres;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * In case the title needs to be changed by the User.
     * @param title String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenre(String genre) {
        if (genres == null) {
            genres = new ArrayList<String>();
        }
        if (!genres.contains(genre)) {
            genres.add(genre);
        }
    }
}
