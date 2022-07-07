package io.github.rsookram.txt

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import io.github.rsookram.txt.reader.ReaderViewModel
import io.github.rsookram.txt.reader.view.ReaderView

class MainActivity : Activity() {

    private lateinit var vm: ReaderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.view_reader)

        vm = lastNonConfigurationInstance as? ReaderViewModel
            ?: ReaderViewModel(applicationContext)

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

        vm.onContent = view::setContent
        vm.onSeek = view::seekTo
        vm.onProgress = view::bindProgress
    }

    override fun onRetainNonConfigurationInstance(): Any = vm

    override fun onStop() {
        super.onStop()
        vm.saveProgress()
    }

    override fun onDestroy() {
        super.onDestroy()

        vm.onContent = {}
        vm.onSeek = {}
        vm.onProgress = { _, _ -> }

        if (isFinishing) {
            vm.onCleared()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            window.enableImmersiveMode()
        }
    }
}

private fun Window.enableImmersiveMode() {
    setDecorFitsSystemWindows(false)

    insetsController?.apply {
        systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsets.Type.systemBars())
    }
}
