package io.github.rsookram.txt.reader.view

import android.widget.AbsListView
import android.widget.TextView
import io.github.rsookram.txt.BookContent

class ReaderView(
    private val list: LineListView,
    private val progress: TextView,
    onMoveListener: (Pair<Int?, Int>) -> Unit,
) {

    init {
        list.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) = Unit

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                onMoveListener(getProgress() to list.getLineOffset())
            }
        })

        // Take touches to prevent conflicts with bottom gesture area
        progress.setOnTouchListener { _, _ -> true }
    }

    fun setContent(content: BookContent) {
        list.adapter = ReaderAdapter(list.context, content.lines)
    }

    fun seekTo(lineIndex: Int) {
        list.seekTo(lineIndex)
    }

    fun getProgress(): Int? = list.getProgress()

    fun bindProgress(offset: Int, length: Int) {
        progress.text = "$offset / $length"
    }
}
