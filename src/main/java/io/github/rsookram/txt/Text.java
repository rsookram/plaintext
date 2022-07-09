package io.github.rsookram.txt;

import java.util.List;

/**
 * The text to display for a single file.
 */
public class Text {

    /**
     * The {@link Line}s containing the text to display.
     */
    public final List<Line> lines;
    /**
     * The total number of characters in {@link #lines}.
     */
    public final int length;

    public Text(List<Line> lines, int length) {
        this.lines = lines;
        this.length = length;
    }
}
