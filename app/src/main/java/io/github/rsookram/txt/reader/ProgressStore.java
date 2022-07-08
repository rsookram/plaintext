package io.github.rsookram.txt.reader;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.rsookram.txt.TextFile;

class ProgressStore {

    private final SharedPreferences prefs;

    public ProgressStore(Context context) {
        prefs = context.getSharedPreferences("txt", Context.MODE_PRIVATE);
    }

    public Integer get(TextFile textFile) {
        String key = key(textFile);
        if (key == null) {
            return null;
        }

        int value = prefs.getInt(key, -1);
        return value >= 0 ? value : null;
    }

    public void set(TextFile textFile, int progress) {
        String key = key(textFile);
        if (key == null) {
            return;
        }

        prefs.edit()
                .putInt(key, progress)
                .apply();
    }

    private String key(TextFile textFile) {
        return textFile.uri.getPath();
    }
}
