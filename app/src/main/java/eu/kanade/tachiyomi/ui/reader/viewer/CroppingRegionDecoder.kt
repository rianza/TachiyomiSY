package eu.kanade.tachiyomi.ui.reader.viewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Point
import android.graphics.Rect
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder
import com.davemorrissey.labs.subscaleview.provider.InputProvider
import java.io.InputStream

/**
 * A custom region decoder that crops the image based on a static crop rectangle.
 *
 * This implementation is tailored for the `tachiyomiorg/subsampling-scale-image-view` fork.
 * Due to API limitations (requiring a no-arg constructor), the crop rectangle is passed
 * via a static companion object property before the decoder is instantiated.
 */
class CroppingRegionDecoder : ImageRegionDecoder {

    private lateinit var decoder: BitmapRegionDecoder
    private val decoderLock = Any()

    private val crop: Rect = cropRect ?: Rect(0, 0, 0, 0)

    companion object {
        /**
         * Static property to hold the crop rectangle.
         * This is a workaround for the library's decoder instantiation process.
         */
        var cropRect: Rect? = null
    }

    override fun init(context: Context, provider: InputProvider): Point {
        var inputStream: InputStream? = null
        try {
            inputStream = provider.openStream()
                ?: error("Failed to open InputStream from provider")
            this.decoder = BitmapRegionDecoder.newInstance(inputStream, false)!!
            return Point(crop.width(), crop.height())
        } finally {
            inputStream?.close()
        }
    }

    override fun decodeRegion(sRect: Rect, sampleSize: Int): Bitmap {
        synchronized(decoderLock) {
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }

            val translatedRect = Rect(
                sRect.left + crop.left,
                sRect.top + crop.top,
                sRect.right + crop.left,
                sRect.bottom + crop.top,
            )

            translatedRect.intersect(Rect(0, 0, decoder.width, decoder.height))

            val bitmap = decoder.decodeRegion(translatedRect, options)
                ?: error("Failed to decode region $translatedRect")
            return bitmap
        }
    }

    override fun isReady(): Boolean {
        return ::decoder.isInitialized && !decoder.isRecycled
    }

    override fun recycle() {
        if (::decoder.isInitialized) {
            decoder.recycle()
        }
    }
}
