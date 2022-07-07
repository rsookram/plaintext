package io.github.rsookram.txt.reader.view

import android.content.Context
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import io.github.rsookram.txt.Line
import io.github.rsookram.txt.R

class ReaderAdapter(
    context: Context,
    private val lines: List<Line>,
) : ArrayAdapter<Line>(context, R.layout.item_line, lines) {

    private val actionModeCallback = object : ActionMode.Callback2() {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.removeItem(android.R.id.shareText)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.removeItem(android.R.id.selectAll)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

        override fun onDestroyActionMode(mode: ActionMode?) = Unit
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView as? TextView ?: createView(parent)

        view.customSelectionActionModeCallback = actionModeCallback
        view.text = lines[position].text

        return view
    }

    private fun createView(parent: ViewGroup): TextView {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_line, parent, false) as TextView

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                // Workaround for losing text selection ability, see:
                // https://issuetracker.google.com/issues/37095917
                view.isEnabled = false
                view.isEnabled = true
            }

            override fun onViewDetachedFromWindow(v: View?) = Unit
        })

        return view
    }
}
