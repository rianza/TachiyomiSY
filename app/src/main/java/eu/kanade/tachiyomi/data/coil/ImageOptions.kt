package eu.kanade.tachiyomi.data.coil

import android.graphics.Bitmap.Config
import coil3.Options

object ImageOptions {
    /** Global options that every Coil request will inherit. */
    val defaultOptions = Options().apply {
        // 👉 THIS IS THE ONLY LINE THAT matters for stability
        bitmapConfig = Config.ARGB_8888
    }
}
