package io.github.rsookram.txt

import android.net.Uri

data class Book(val uri: Uri)

data class BookContent(val lines: List<Line>, val length: Int)

data class Line(val text: String, val offset: Int)
