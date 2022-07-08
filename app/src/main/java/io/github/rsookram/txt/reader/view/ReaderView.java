package io.github.rsookram.txt.reader.view;

import android.widget.AbsListView;
import android.widget.TextView;

import java.util.function.BiConsumer;

import io.github.rsookram.txt.BookContent;

public class ReaderView {

    private final LineListView list;
    private final TextView progress;

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
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                onMoveListener.accept(getProgress(), list.getLineOffset());
            }
        });

        // Take touches to prevent conflicts with bottom gesture area
        progress.setOnTouchListener((v, event) -> true);
    }

    public void setContent(BookContent content) {
        list.setAdapter(new ReaderAdapter(list.getContext(), content.lines));
    }

    public void seekTo(int lineIndex) {
        list.seekTo(lineIndex);
    }

    public Integer getProgress() {
        return list.getProgress();
    }

    public void bindProgress(int offset, int length) {
        progress.setText(offset + " / " + length);
    }
}
