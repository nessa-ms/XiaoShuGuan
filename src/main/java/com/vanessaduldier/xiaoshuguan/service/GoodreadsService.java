package com.vanessaduldier.xiaoshuguan.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Get additional Metadata about Books from Goodreads
 */
public class GoodreadsService {

    public GoodreadsService() {

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
