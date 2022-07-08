package io.github.rsookram.txt;

import java.util.List;

public class BookContent {

    public final List<Line> lines;
    public final int length;

    public BookContent(List<Line> lines, int length) {
        this.lines = lines;
        this.length = length;
    }
}
