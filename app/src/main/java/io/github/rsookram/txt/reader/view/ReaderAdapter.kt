package io.github.rsookram.txt.reader.view

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.rsookram.txt.Line
import io.github.rsookram.txt.R

class ReaderAdapter(private val lines: List<Line>) : RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_line, parent, false) as TextView

        view.customSelectionActionModeCallback = object : ActionMode.Callback2() {
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

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.text = lines[position].text
    }

    override fun getItemCount(): Int = lines.size

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)

        // Bug workaround for losing text selection ability, see:
        // https://code.google.com/p/android/issues/detail?id=208169
        holder.view.isEnabled = false
        holder.view.isEnabled = true
    }
}

class Holder(val view: TextView) : RecyclerView.ViewHolder(view)
