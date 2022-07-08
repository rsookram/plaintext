package io.github.rsookram.txt.reader;

import android.content.ContentResolver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.rsookram.txt.Book;
import io.github.rsookram.txt.BookContent;
import io.github.rsookram.txt.Line;

public class ContentLoader {

    private final ContentResolver contentResolver;

    public ContentLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public BookContent load(Book book) {
        List<Line> lines = getLines(book);
        Line lastLine = lines.isEmpty() ? null : lines.get(lines.size() - 1);
        int length = lastLine != null ? lastLine.offset + lastLine.text.length() : 0;
        return new BookContent(lines, length);
    }

    private List<Line> getLines(Book book) {
        InputStream stream;
        try {
            stream = contentResolver.openInputStream(book.uri);
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        List<Line> lines = new ArrayList<>();

        try {
            bufferedReader.lines()
                    .map(String::trim)
                    .forEach(str -> {
                        Line prevLine = lines.isEmpty() ? null : lines.get(lines.size() - 1);
                        int offset = prevLine != null ? prevLine.offset + prevLine.text.length() : 0;
                        lines.add(new Line(str, offset));
                    });
        } catch (Exception e) {
            return Collections.emptyList();
        }

        return lines;
    }
}
