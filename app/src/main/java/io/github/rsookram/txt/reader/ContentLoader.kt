package io.github.rsookram.txt.reader

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.rsookram.txt.BgContext
import io.github.rsookram.txt.Book
import io.github.rsookram.txt.BookContent
import io.github.rsookram.txt.Line
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ContentLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    @BgContext private val ioContext: CoroutineContext,
) {

    suspend fun load(book: Book): BookContent = withContext(ioContext) {
        val lines = getLines(book)
        val lastLine = lines.lastOrNull()
        val length = if (lastLine != null) lastLine.offset + lastLine.text.length else 0
        BookContent(lines, length)
    }

    private fun getLines(book: Book): List<Line> {
        val lineStrings = getLineStrings(book)

        val lines = mutableListOf<Line>()
        var offset = 0

        lineStrings.forEach { str ->
            lines.add(Line(str, offset))
            offset += str.length
        }

        return lines
    }

    private fun getLineStrings(book: Book): Sequence<String> {
        val stream = context.contentResolver.runCatching { openInputStream(book.uri) }
            .getOrNull()
            ?: return emptySequence()

        return stream
            .bufferedReader()
            .lineSequence()
            .map(String::trim)
    }
}
