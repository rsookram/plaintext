package io.github.rsookram.txt;

import java.util.List;

public class Text {

    public final List<Line> lines;
    public final int length;

    public Text(List<Line> lines, int length) {
        this.lines = lines;
        this.length = length;
    }
}
