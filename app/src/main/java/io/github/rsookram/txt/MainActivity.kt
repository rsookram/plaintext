package io.github.rsookram.txt

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.rsookram.txt.reader.ReaderViewModel
import io.github.rsookram.txt.reader.view.ReaderView

@AndroidEntryPoint
class MainActivity : ComponentActivity(R.layout.view_reader) {

    private val vm: ReaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.enableImmersiveMode()

        val uri = intent.data
        if (uri == null) {
            finish()
            return
        }

        val view = ReaderView(
            findViewById(R.id.line_list),
            findViewById(R.id.progress)
        ) { (progress, offset) ->
            if (progress != null) {
                vm.onProgressChanged(progress, offset)
            }
        }

        val book = Book(uri)
        vm.load(book)

        vm.contents.observe(this, view::setContent)

        vm.seeks.observe(this, view::seekTo)

        vm.progressChanges.observe(this) { (offset, length) ->
            view.bindProgress(offset, length)
        }
    }

    override fun onStop() {
        super.onStop()
        vm.saveProgress()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            window.enableImmersiveMode()
        }
    }
}

private fun Window.enableImmersiveMode() {
    WindowCompat.setDecorFitsSystemWindows(this, false)

    WindowCompat.getInsetsController(this, decorView).apply {
        systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.systemBars())
    }
}
