package io.github.rsookram.txt.reader.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LineRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    private val lm = LineLayoutManager(context)

    init {
        layoutManager = lm
    }

    fun seekTo(lineIndex: Int) {
        lm.scrollToPositionWithOffset(lineIndex, 0)
    }

    fun getProgress(): Int? {
        // findFirstVisibleItemPosition can't be used because it doesn't account
        // for padding properly when clipToPadding is true
        val parentEnd = height

        var firstChild: View? = null
        for (i in 0 until lm.childCount) {
            val child = lm.getChildAt(i)!!
            val childStart = lm.getDecoratedTop(child)
            val childEnd = lm.getDecoratedBottom(child)

            if (childStart in 0 until parentEnd || childEnd in 1 until parentEnd) {
                firstChild = child
                break
            }
        }

        firstChild ?: return null

        return lm.getPosition(firstChild)
    }

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
