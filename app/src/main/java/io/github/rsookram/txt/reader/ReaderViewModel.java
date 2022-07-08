package io.github.rsookram.txt.reader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.rsookram.txt.Book;
import io.github.rsookram.txt.BookContent;
import io.github.rsookram.txt.Line;

public class ReaderViewModel {

    private final ProgressStore progressStore;
    private final ContentLoader loader;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Future<?>> cancellables = new ArrayList<>();

    private Consumer<BookContent> onContent = bookContent -> {};
    private Consumer<Integer> onSeek = progress -> {};
    private BiConsumer<Integer, Integer> onProgress = (offset, length) -> {};

    private BookContent content;
    private Pair<Integer, Integer> progress;
    private Integer currentLine;

    private Book book;

    public ReaderViewModel(Context context) {
        this.progressStore = new ProgressStore(context);
        this.loader = new ContentLoader(context.getContentResolver());
    }

    public void load(Book book) {
        if (content != null) {
            return;
        }

        this.book = book;

        Future<Void> future = CompletableFuture.supplyAsync(() -> loader.load(book))
                .thenAccept(content -> {
                    Integer progress = progressStore.get(book);
                    handler.post(() -> {
                        ReaderViewModel.this.content = content;
                        onContent.accept(content);
                        onSeek.accept(progress != null ? progress : 0);
                    });
                });
        cancellables.add(future);
    }

    public void onProgressChanged(int progress, int offsetInLine) {
        currentLine = progress;

        if (content == null) {
            return;
        }

        if (progress >= content.lines.size()) {
            return;
        }

        Line line = content.lines.get(progress);

        int totalOffset = line.offset + offsetInLine;
        ReaderViewModel.this.progress = new Pair<>(totalOffset, content.length);
        onProgress.accept(totalOffset, content.length);
    }

    public void saveProgress() {
        if (currentLine == null) {
            return;
        }

        progressStore.set(book, currentLine);
    }

    public void setOnContent(Consumer<BookContent> onContent) {
        if (content != null) {
            onContent.accept(content);
        }
        this.onContent = onContent;
    }

    public void setOnSeek(Consumer<Integer> onSeek) {
        this.onSeek = onSeek;
    }

    public void setOnProgress(BiConsumer<Integer, Integer> onProgress) {
        if (progress != null) {
            onProgress.accept(progress.first, progress.second);
        }
        this.onProgress = onProgress;
    }

    public void onCleared() {
        for (Future<?> cancellable : cancellables) {
            cancellable.cancel(false);
        }
        cancellables.clear();

        handler.removeCallbacksAndMessages(null);
    }
}
