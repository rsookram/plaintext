package io.github.rsookram.txt.reader

import android.content.ContentResolver
import io.github.rsookram.txt.Book
import io.github.rsookram.txt.BookContent
import io.github.rsookram.txt.Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContentLoader(private val contentResolver: ContentResolver) {

    suspend fun load(book: Book): BookContent = withContext(Dispatchers.IO) {
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
        val stream = contentResolver.runCatching { openInputStream(book.uri) }
            .getOrNull()
            ?: return emptySequence()

        return stream
            .bufferedReader()
            .lineSequence()
            .map(String::trim)
    }
}
