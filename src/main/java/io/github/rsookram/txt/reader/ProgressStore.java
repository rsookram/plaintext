package io.github.rsookram.txt.reader;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

class ProgressStore {

    private final SharedPreferences prefs;

    public ProgressStore(Context context) {
        prefs = context.getSharedPreferences("txt", Context.MODE_PRIVATE);
    }

    public Integer get(Uri uri) {
        String key = key(uri);
        if (key == null) {
            return null;
        }

        int value = prefs.getInt(key, -1);
        return value >= 0 ? value : null;
    }

    public void set(Uri uri, int progress) {
        String key = key(uri);
        if (key == null) {
            return;
        }

        prefs.edit()
                .putInt(key, progress)
                .apply();
    }

    private String key(Uri uri) {
        return uri.getPath();
    }
}
