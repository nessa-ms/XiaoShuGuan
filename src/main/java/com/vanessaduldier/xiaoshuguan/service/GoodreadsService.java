package com.vanessaduldier.xiaoshuguan.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Get additional Metadata about Books from Goodreads
 */
public class GoodreadsService {

    public GoodreadsService() {

    }

    public List<String> fetchGenres(String goodreadsUrl) throws IOException {
        Document doc = Jsoup.connect(goodreadsUrl)
                .userAgent("Mozilla/5.0") // sehr wichtig!
                .timeout(10_000)
                .get();

        List<String> genres = new ArrayList<>();

        // Selektor für die Genre-Buttons
        Elements genreElements = doc.select(
                "div[data-testid=genresList] a.Button--tag span.Button__labelItem"
        );

        for (Element el : genreElements) {
            String genre = el.text();
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
            if (genres.size() == 4) break; // max 4 Genres
        }

        return genres;
    }

    /**
     * Read Webpage and output content in terminal
     * @param urlString Goodreads url of Book
     * @throws IOException Webpage cant be read
     * @source <a href="https://stackoverflow.com/questions/6159118/using-java-to-pull-data-from-a-webpage">...</a>
     */
    public void readFromWeb(String urlString) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);  // convert String url into URL object
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
        } catch (MalformedURLException e) {
            throw new MalformedURLException("URL is malformed: " + e);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
