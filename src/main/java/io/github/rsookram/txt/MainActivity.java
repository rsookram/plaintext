package io.github.rsookram.txt;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import io.github.rsookram.txt.reader.ReaderViewModel;
import io.github.rsookram.txt.reader.view.ReaderView;

public class MainActivity extends Activity {

    private ReaderViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_reader);

        vm = (ReaderViewModel) getLastNonConfigurationInstance();
        if (vm == null) {
            vm = new ReaderViewModel(getApplicationContext());
        }

        enableImmersiveMode();

        Uri uri = getIntent().getData();
        if (uri == null) {
            finish();
            return;
        }

        ReaderView view = new ReaderView(findViewById(R.id.line_list), findViewById(R.id.progress), (progress, offset) -> {
            if (progress != null) {
                vm.onProgressChanged(progress, offset);
            }
        });

        vm.load(uri);

        vm.setOnTextLoad(view::setText);
        vm.setOnSeek(view::seekTo);
        vm.setOnProgress(view::bindProgress);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return vm;
    }

    @Override
    protected void onStop() {
        super.onStop();
        vm.saveProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        vm.setOnTextLoad(text -> {});
        vm.setOnSeek(lineIndex -> {});
        vm.setOnProgress((offset, length) -> {});

        if (isFinishing()) {
            vm.onCleared();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    private void enableImmersiveMode() {
        getWindow().setDecorFitsSystemWindows(false);

        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController == null) {
            return;
        }

        insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        insetsController.hide(WindowInsets.Type.systemBars());
    }
}
