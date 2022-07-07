package io.github.rsookram.txt.reader.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import android.widget.TextView

class LineListView(context: Context, attrs: AttributeSet) : ListView(context, attrs) {

    fun seekTo(lineIndex: Int) {
        setSelection(lineIndex)
    }

    fun getProgress(): Int? = if (childCount > 0) firstVisiblePosition else null

    fun getLineOffset(): Int {
        val child = getChildAt(0) as? TextView ?: return 0

        val layout = child.layout
        val lineOffset = -child.top
        val line = layout.getLineForVertical(lineOffset)

        if (layout.lineCount <= 1) {
            return 0
        }

        return layout.getLineStart(line) + 1
    }
}
