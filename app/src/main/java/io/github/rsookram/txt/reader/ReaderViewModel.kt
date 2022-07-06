package io.github.rsookram.txt.reader

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.github.rsookram.txt.Book
import io.github.rsookram.txt.BookContent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class ReaderViewModel(context: Context) {

    private val content = MutableLiveData<BookContent>()
    val contents: LiveData<BookContent> = content

    private val seekEvent = eventLiveData<Int>()
    val seeks: LiveData<Int> = seekEvent

    private val progress = MutableLiveData<Pair<Int, Int>>()
    val progressChanges: LiveData<Pair<Int, Int>> = progress

    private var currentLine: Int? = null

    private val progressDao = ProgressDao(context)
    private val loader = ContentLoader(context.contentResolver)

    private val cancellables = mutableListOf<Future<*>>()

    private lateinit var book: Book

    fun load(book: Book) {
        if (content.value != null) {
            return
        }

        this.book = book

        cancellables += CompletableFuture.supplyAsync {
            loader.load(book)
        }.thenAccept { content ->
            val progress = progressDao.get(book)
            this@ReaderViewModel.content.postValue(content)
            seekEvent.postValue(progress ?: 0)
        }
    }

    fun onProgressChanged(progress: Int, offsetInLine: Int) {
        currentLine = progress

        val content = content.value ?: return
        val line = content.lines.getOrNull(progress) ?: return

        val totalOffset = line.offset + offsetInLine
        this.progress.value = totalOffset to content.length
    }

    fun saveProgress() {
        val progress = currentLine ?: return
        progressDao.set(book, progress)
    }

    fun onCleared() {
        cancellables.forEach { it.cancel(false) }
        cancellables.clear()
    }
}

private fun <T : Any> eventLiveData() = object : MutableLiveData<T>() {

    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        }
    }

    override fun setValue(value: T) {
        pending = true
        super.setValue(value)
    }
}
