package io.github.rsookram.txt;

/**
 * A line of {@link Text} from a file. This may be long enough to be rendered across multiple lines.
 */
public class Line {

    /**
     * The text to display for this line, without any leading or trailing whitespace.
     */
    public final String text;
    /**
     * The offset (measured in characters, zero-indexed) of the first character in {@link #text}
     * relative to the start of the file containing this line.
     */
    public final int offset;

    public Line(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }
}
