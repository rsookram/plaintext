package io.github.rsookram.txt.reader.view;

import android.widget.AbsListView;
import android.widget.TextView;

import java.util.Locale;
import java.util.function.BiConsumer;

import io.github.rsookram.txt.Text;

public class ReaderView {

    private final LineListView list;
    private final TextView progress;

    /**
     * @param onMoveListener listener which is notified on scroll events with the index of the first
     * {@link io.github.rsookram.txt.Line} in the viewport, and the offset of the first visible
     * character in that {@link io.github.rsookram.txt.Line}.
     */
    public ReaderView(LineListView list,
                      TextView progress,
                      BiConsumer<Integer, Integer> onMoveListener) {
        this.list = list;
        this.progress = progress;

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItem,
                                 int visibleItemCount,
                                 int totalItemCount) {
                onMoveListener.accept(getProgress(), list.getLineOffset());
            }
        });

        // Take touches to prevent conflicts with bottom gesture area
        progress.setOnTouchListener((v, event) -> true);
    }

    public void setText(Text text) {
        list.setAdapter(new ReaderAdapter(list.getContext(), text.lines));
    }

    public void seekTo(int lineIndex) {
        list.seekTo(lineIndex);
    }

    public Integer getProgress() {
        return list.getProgress();
    }

    public void bindProgress(int offset, int length) {
        progress.setText(String.format(Locale.getDefault(), "%.1f%%", offset * 100.0 / length));
    }
}
