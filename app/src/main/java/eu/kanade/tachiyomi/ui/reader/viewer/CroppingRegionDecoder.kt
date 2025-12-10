package eu.kanade.tachiyomi.ui.reader.viewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Point
import android.graphics.Rect
import com.davemorrissey.labs.subscaleview.decoder.DecoderFactory
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder
import java.io.InputStream

/**
 * A custom region decoder that crops the image based on the provided crop rectangle.
 * The stream is provided by SSIV's init method.
 */
class CroppingRegionDecoder(
    private val cropRect: Rect,
) : ImageRegionDecoder {

    private lateinit var decoder: BitmapRegionDecoder
    private val decoderLock = Any()

    override fun init(imageStream: InputStream): Point {
        try {
            this.decoder = BitmapRegionDecoder.newInstance(imageStream, false)!!
            return Point(cropRect.width(), cropRect.height())
        } finally {
            imageStream.close()
        }
    }

    override fun decodeRegion(sRect: Rect, sampleSize: Int): Bitmap {
        synchronized(decoderLock) {
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }

            val translatedRect = Rect(
                sRect.left + cropRect.left,
                sRect.top + cropRect.top,
                sRect.right + cropRect.left,
                sRect.bottom + cropRect.top,
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

/**
 * Factory for creating [CroppingRegionDecoder] instances.
 */
class CroppingRegionDecoderFactory(
    private val cropRect: Rect,
) : DecoderFactory<ImageRegionDecoder> {
    override fun make(): ImageRegionDecoder {
        return CroppingRegionDecoder(cropRect)
    }
}
