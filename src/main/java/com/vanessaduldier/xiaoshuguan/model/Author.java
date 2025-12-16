package com.vanessaduldier.xiaoshuguan.model;

/**
 * @author Vanessa Duldier
 */
public class Author {
    private final Long id;
    private final String name;

    public Author(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
