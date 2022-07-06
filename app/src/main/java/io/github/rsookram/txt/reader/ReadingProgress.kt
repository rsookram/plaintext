package io.github.rsookram.txt.reader

import android.content.Context
import io.github.rsookram.txt.Book

class ProgressDao(context: Context) {

    private val prefs = context.getSharedPreferences("txt", Context.MODE_PRIVATE)

    fun get(book: Book): Int? {
        val key = key(book) ?: return null

        val value = prefs.getInt(key, -1)
        return if (value >= 0) value else null
    }

    fun set(book: Book, progress: Int) {
        val key = key(book) ?: return

        prefs.edit()
            .putInt(key, progress)
            .apply()
    }

    private fun key(book: Book): String? = book.uri.path
}
