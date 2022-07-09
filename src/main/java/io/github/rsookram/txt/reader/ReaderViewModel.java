package io.github.rsookram.txt.reader;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.rsookram.txt.Line;
import io.github.rsookram.txt.Text;

/**
 * The main ViewModel which contains business logic. It behaves like an androidx ViewModel, and is
 * similarly cleared in {@link #onCleared()}.
 */
public class ReaderViewModel {

    private final ProgressStore progressStore;
    private final TextLoader loader;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Future<?>> cancellables = new ArrayList<>();

    private Consumer<Text> onTextLoad = text -> {};
    private Consumer<Integer> onSeek = progress -> {};
    private BiConsumer<Integer, Integer> onProgress = (offset, length) -> {};

    private Text text;
    private Pair<Integer, Integer> progress;
    private Integer currentLine;

    private Uri uri;

    public ReaderViewModel(Context context) {
        this.progressStore = new ProgressStore(context);
        this.loader = new TextLoader(context.getContentResolver());
    }

    public void load(Uri uri) {
        if (text != null) {
            return;
        }

        this.uri = uri;

        Future<Void> future = CompletableFuture.supplyAsync(() -> loader.load(uri))
                .thenAccept(text -> {
                    Integer progress = progressStore.get(uri);
                    handler.post(() -> {
                        ReaderViewModel.this.text = text;
                        onTextLoad.accept(text);
                        onSeek.accept(progress != null ? progress : 0);
                    });
                });
        cancellables.add(future);
    }

    /**
     * @param progress The index of the first {@link Line} on-screen.
     */
    public void onProgressChanged(int progress, int offsetInLine) {
        currentLine = progress;

        if (text == null) {
            return;
        }

        if (progress >= text.lines.size()) {
            return;
        }

        Line line = text.lines.get(progress);

        int totalOffset = line.offset + offsetInLine;
        ReaderViewModel.this.progress = new Pair<>(totalOffset, text.length);
        onProgress.accept(totalOffset, text.length);
    }

    public void saveProgress() {
        if (currentLine == null) {
            return;
        }

        progressStore.set(uri, currentLine);
    }

    public void setOnTextLoad(Consumer<Text> onTextLoad) {
        if (text != null) {
            onTextLoad.accept(text);
        }
        this.onTextLoad = onTextLoad;
    }

    /**
     * Sets a listener to be notified when the text should be scrolled to make the {@link Line} at
     * the given index visible.
     */
    public void setOnSeek(Consumer<Integer> onSeek) {
        this.onSeek = onSeek;
    }

    /**
     * Sets a listener to be notified when the progress through the text changes. The listener is
     * passed the character offset of the start of the first rendered line on-screen, and the length
     * of the entire text.
     */
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
