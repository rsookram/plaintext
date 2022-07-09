package io.github.rsookram.txt.reader;

import android.content.ContentResolver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.rsookram.txt.Line;
import io.github.rsookram.txt.Text;
import io.github.rsookram.txt.TextFile;

public class TextLoader {

    private final ContentResolver contentResolver;

    public TextLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Text load(TextFile textFile) {
        List<Line> lines = getLines(textFile);
        Line lastLine = lines.isEmpty() ? null : lines.get(lines.size() - 1);
        int length = lastLine != null ? lastLine.offset + lastLine.text.length() : 0;
        return new Text(lines, length);
    }

    private List<Line> getLines(TextFile textFile) {
        InputStream stream;
        try {
            stream = contentResolver.openInputStream(textFile.uri);
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
