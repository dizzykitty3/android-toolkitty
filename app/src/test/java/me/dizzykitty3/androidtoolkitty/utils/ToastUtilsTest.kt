package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.ToastUtils.showToast
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ToastUtilsTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @After
    fun resetToasts() {
        ShadowToast.reset()
    }

    @Test
    fun showToast_displaysTheProvidedText() {
        context.showToast("Copied")

        assertEquals(1, ShadowToast.shownToastCount())
        assertEquals("Copied", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun showToast_resolvesStringResourcesBeforeDisplayingThem() {
        context.showToast(R.string.clipboard_cleared)

        assertEquals(1, ShadowToast.shownToastCount())
        assertEquals(context.getString(R.string.clipboard_cleared), ShadowToast.getTextOfLatestToast())
    }
}
