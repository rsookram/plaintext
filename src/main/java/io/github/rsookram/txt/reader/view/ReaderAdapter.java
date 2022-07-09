package io.github.rsookram.txt.reader.view;

import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.rsookram.txt.Line;
import io.github.rsookram.txt.R;

public class ReaderAdapter extends ArrayAdapter<Line> {

    private final List<Line> lines;

    private final ActionMode.Callback2 actionModeCallback = new ActionMode.Callback2() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.removeItem(android.R.id.shareText);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.removeItem(android.R.id.selectAll);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    };

    public ReaderAdapter(Context context, List<Line> objects) {
        super(context, -1 /* unused */, objects);
        lines = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = ((TextView) convertView);
        if (view == null) {
            view = createView(parent);
        }

        view.setCustomSelectionActionModeCallback(actionModeCallback);
        view.setText(lines.get(position).text);

        return view;
    }

    private TextView createView(ViewGroup parent) {
        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_line, parent, false);

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                // Workaround for losing text selection ability, see:
                // https://issuetracker.google.com/issues/37095917
                view.setEnabled(false);
                view.setEnabled(true);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
            }
        });

        return view;
    }
}
