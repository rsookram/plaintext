package io.github.rsookram.txt.reader.view

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LineLayoutManager(context: Context) : LinearLayoutManager(context) {

    // The TextView will normally request focus when tapped on. When this
    // happens, it will ask its parent (the RecyclerView) to scroll it into
    // view. By returning true here, the default scroll behaviour is
    // suppressed.
    //
    // This fixes the problem where text is selected from one TextView, then
    // from scrolling when text is selected in another TextView.
    override fun onRequestChildFocus(
        parent: RecyclerView,
        state: RecyclerView.State,
        child: View,
        focused: View?
    ) = true

    override fun requestChildRectangleOnScreen(
        parent: RecyclerView,
        child: View,
        rect: Rect,
        immediate: Boolean
    ) = false
}
