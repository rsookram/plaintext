package io.github.rsookram.txt.reader.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.TextView;

public class LineListView extends ListView {

    public LineListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void seekTo(int lineIndex) {
        setSelection(lineIndex);
    }

    public Integer getProgress() {
        return getChildCount() > 0 ? getFirstVisiblePosition() : null;
    }

    public int getLineOffset() {
        TextView child = (TextView) getChildAt(0);
        if (child == null) {
            return 0;
        }

        Layout layout = child.getLayout();
        int lineOffset = -child.getTop();
        int line = layout.getLineForVertical(lineOffset);

        if (layout.getLineCount() <= 1) {
            return 0;
        }

        return layout.getLineStart(line) + 1;
    }
}
