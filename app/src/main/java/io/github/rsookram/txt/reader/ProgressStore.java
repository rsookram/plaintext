package io.github.rsookram.txt.reader;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.rsookram.txt.Book;

class ProgressStore {

    private final SharedPreferences prefs;

    public ProgressStore(Context context) {
        prefs = context.getSharedPreferences("txt", Context.MODE_PRIVATE);
    }

    public Integer get(Book book) {
        String key = key(book);
        if (key == null) {
            return null;
        }

        int value = prefs.getInt(key, -1);
        return value >= 0 ? value : null;
    }

    public void set(Book book, int progress) {
        String key = key(book);
        if (key == null) {
            return;
        }

        prefs.edit()
                .putInt(key, progress)
                .apply();
    }

    private String key(Book book) {
        return book.uri.getPath();
    }
}
