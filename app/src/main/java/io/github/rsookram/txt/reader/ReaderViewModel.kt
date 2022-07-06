package io.github.rsookram.txt.reader

import android.content.Context
import android.os.Handler
import android.os.Looper
import io.github.rsookram.txt.Book
import io.github.rsookram.txt.BookContent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class ReaderViewModel(context: Context) {

    var onContent: (BookContent) -> Unit = {}
        set(value) {
            val content = content
            if (content != null) {
                value(content)
            }

            field = value
        }

    var onSeek: (Int) -> Unit = {}

    var onProgress: (Int, Int) -> Unit = { _, _ -> }
        set(value) {
            val progress = progress
            if (progress != null) {
                value(progress.first, progress.second)
            }

            field = value
        }

    private var content: BookContent? = null
    private var progress: Pair<Int, Int>? = null

    private var currentLine: Int? = null

    private val progressDao = ProgressDao(context)
    private val loader = ContentLoader(context.contentResolver)

    private val handler = Handler(Looper.getMainLooper())

    private val cancellables = mutableListOf<Future<*>>()

    private lateinit var book: Book

    fun load(book: Book) {
        if (content != null) {
            return
        }

        this.book = book

        cancellables += CompletableFuture.supplyAsync {
            loader.load(book)
        }.thenAccept { content ->
            val progress = progressDao.get(book)
            handler.post {
                this@ReaderViewModel.content = content
                onContent(content)
                onSeek(progress ?: 0)
            }
        }
    }

    fun onProgressChanged(progress: Int, offsetInLine: Int) {
        currentLine = progress

        val content = content ?: return
        val line = content.lines.getOrNull(progress) ?: return

        val totalOffset = line.offset + offsetInLine
        this@ReaderViewModel.progress = totalOffset to content.length
        onProgress(totalOffset, content.length)
    }

    fun saveProgress() {
        val progress = currentLine ?: return
        progressDao.set(book, progress)
    }

    fun onCleared() {
        cancellables.forEach { it.cancel(false) }
        cancellables.clear()

        handler.removeCallbacksAndMessages(null)
    }
}
