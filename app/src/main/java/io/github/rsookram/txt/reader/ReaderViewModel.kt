package io.github.rsookram.txt.reader

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.rsookram.txt.Book
import io.github.rsookram.txt.BookContent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val progressDao: ProgressDao,
    private val loader: ContentLoader,
) : ViewModel() {

    private val content = MutableLiveData<BookContent>()
    val contents: LiveData<BookContent> = content

    private val seekEvent = eventLiveData<Int>()
    val seeks: LiveData<Int> = seekEvent

    private val progress = MutableLiveData<Pair<Int, Int>>()
    val progressChanges: LiveData<Pair<Int, Int>> = progress

    private var currentLine: Int? = null

    private lateinit var book: Book

    fun load(book: Book) {
        if (content.value != null) {
            return
        }

        this.book = book

        viewModelScope.launch {
            val contentAsync = async { loader.load(book) }
            val progressAsync = async { progressDao.get(book) }
            val content = contentAsync.await()
            val progress = progressAsync.await()

            seekEvent.setValue(progress ?: 0)
            this@ReaderViewModel.content.value = content
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
        GlobalScope.launch {
            progressDao.set(book, progress)
        }
    }
}

private fun <T : Any> eventLiveData() = object : MutableLiveData<T>() {

    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, { t ->
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        })
    }

    override fun setValue(value: T) {
        pending = true
        super.setValue(value)
    }
}
