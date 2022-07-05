package io.github.rsookram.txt.reader.view

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.rsookram.txt.BookContent

class ReaderView(
    private val list: LineRecyclerView,
    private val progress: TextView,
    onMoveListener: (Pair<Int?, Int>) -> Unit,
) {

    init {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onMoveListener(getProgress() to list.getLineOffset())
            }
        })

        // Take touches to prevent conflicts with bottom gesture area
        progress.setOnTouchListener { _, _ -> true }
    }

    fun setContent(content: BookContent) {
        list.swapAdapter(ReaderAdapter(content.lines), true)
    }

    fun seekTo(lineIndex: Int) {
        list.seekTo(lineIndex)
    }

    fun getProgress(): Int? = list.getProgress()

    fun bindProgress(offset: Int, length: Int) {
        progress.text = "$offset / $length"
    }
}
