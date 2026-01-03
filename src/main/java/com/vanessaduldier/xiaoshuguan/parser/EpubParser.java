package com.vanessaduldier.xiaoshuguan.parser;

import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.model.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpubParser {

    public EpubParser() {}

    public Book parse(File epubFile) {
        try (InputStream is = new FileInputStream(epubFile)) {
            // EPUB mit epub4j parsen
            EpubReader epubReader = new EpubReader();
            nl.siegmann.epublib.domain.Book epub = epubReader.readEpub(is);

            // Metadaten extrahieren
            String title = extractTitle(epub);
            List<Author> authors = extractAuthors(epub);
            String description = extractDescription(epub);
            String publisher = extractPublisher(epub);
            String isbn = extractISBN(epub);
            String language = extractLanguage(epub);
            String coverImage = extractCoverImage(epub);

            // Genre extrahieren (wenn in Metadaten vorhanden)
            List<String> genres = extractGenres(epub);

            // Book-Objekt erstellen
            Book book = new Book(
                    generateId(),
                    title,
                    authors,
                    genres
            );

            // Zusätzliche Metadaten setzen
            book.setDescription(description);
            book.setPublisher(publisher);
            book.setIsbn(isbn);
            book.setLanguage(language);
            book.setCoverImage(coverImage);
            book.setFilePath(epubFile.getAbsolutePath());

            return book;

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Parsen der EPUB: " + epubFile.getName(), e);
        }
    }

    private Long generateId() {
        return null;
    }

    private String extractTitle(nl.siegmann.epublib.domain.Book epub) {
        String title = epub.getTitle();
        return title != null ? title : "Unbekannter Titel";
    }

    private List<Author> extractAuthors(nl.siegmann.epublib.domain.Book epub) {
        List<Author> authors = new ArrayList<>();
        List<nl.siegmann.epublib.domain.Author> epubAuthors = epub.getMetadata().getAuthors();

        for (nl.siegmann.epublib.domain.Author epubAuthor : epubAuthors) {
            Long authorId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
            authors.add(new Author(authorId, epubAuthor.getFirstname() + " " + epubAuthor.getLastname()));
        }

        if (authors.isEmpty()) {
            authors.add(new Author(generateId(), "Unbekannter Autor"));
        }

        return authors;
    }

    private List<String> extractGenres(nl.siegmann.epublib.domain.Book epub) {
        List<String> genres = new ArrayList<>();
        List<String> subjects = epub.getMetadata().getSubjects();

        if (subjects != null && !subjects.isEmpty()) {
            // Nehme bis zu 4 Subjects als Genres
            int maxGenres = Math.min(subjects.size(), 4);
            for (int i = 0; i < maxGenres; i++) {
                genres.add(subjects.get(i));
            }
        }

        return genres;
    }

    /**
     * Extract and clean description
     * @param epub Epub File
     * @return clean description
     */
    private String extractDescription(nl.siegmann.epublib.domain.Book epub) {
        String description = epub.getMetadata().getDescriptions().isEmpty()
                ? null
                : epub.getMetadata().getDescriptions().get(0);
        if (description == null || description.isEmpty()) {
            return "No description available";
        }
        // return description without html tags
        return description.replaceAll("<[^>]*>", " ");
    }

    private String extractPublisher(nl.siegmann.epublib.domain.Book epub) {
        List<String> publishers = epub.getMetadata().getPublishers();
        return publishers.isEmpty() ? null : publishers.get(0);
    }

    private String extractISBN(nl.siegmann.epublib.domain.Book epub) {
        for (Identifier identifier : epub.getMetadata().getIdentifiers()) {

            String value = identifier.getValue();
            String scheme = identifier.getScheme();

            if (scheme != null && scheme.equalsIgnoreCase("ISBN")) {
                return value.replace("ISBN:", "").trim();
            }

            if (value != null && value.matches("\\d{10}|\\d{13}")) {
                return value;
            }
        }
        return null;
    }

    private String extractLanguage(nl.siegmann.epublib.domain.Book epub) {
        String language = epub.getMetadata().getLanguage();
        return language != null ? language : "de";
    }

    private String extractCoverImage(nl.siegmann.epublib.domain.Book epub) {
        // Cover als Base64 oder Pfad extrahieren
        // Für erste Version: nur als Pfad speichern
        try {
            nl.siegmann.epublib.domain.Resource coverResource = epub.getCoverImage();
            if (coverResource != null) {
                return "cover_present"; // Später: Base64 oder Dateipfad
            }
        } catch (Exception e) {
            // Cover nicht vorhanden oder nicht lesbar
        }
        return null;
    }
}
