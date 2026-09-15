package me.dizzykitty3.androidtoolkitty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import me.dizzykitty3.androidtoolkitty.ui.home.HomeScreen
import me.dizzykitty3.androidtoolkitty.utils.clearClipboard
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtils.showSnackbar
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var windowFocusContinuation: Continuation<Unit>? = null
    private var clipboardClearJob: Job? = null
    private val windowFocusContinuationNotResumed = AtomicBoolean(true)
    private var isAutoClearClipboard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        enableEdgeToEdge()
        setContent {
            HomeScreen { isAutoClearClipboard = it }
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        windowFocusContinuationNotResumed.set(true)
        clipboardClearJob?.cancel()
        clipboardClearJob = lifecycleScope.launch {
            suspendCancellableCoroutine { cont ->
                windowFocusContinuation = cont
            }
            if (isAutoClearClipboard && clearClipboard()) {
                window.decorView.showSnackbar(R.string.clipboard_cleared_automatically)
                Timber.i("Clipboard cleared automatically")
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Timber.d("onWindowFocusChanged")
        if (hasFocus && windowFocusContinuationNotResumed.get()) {
            try {
                Timber.d("continuation resume start")
                windowFocusContinuation?.resume(Unit)
            } catch (e: IllegalStateException) {
                Timber.e(e)
            } finally {
                Timber.i("continuation resumed")
                windowFocusContinuationNotResumed.set(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        clipboardClearJob?.cancel()
        clipboardClearJob = null
        windowFocusContinuation = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }
}
