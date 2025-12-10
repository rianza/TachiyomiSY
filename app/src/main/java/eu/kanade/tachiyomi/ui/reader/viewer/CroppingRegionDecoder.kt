package eu.kanade.tachiyomi.ui.reader.viewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Point
import android.graphics.Rect
import com.davemorrissey.labs.subscaleview.decoder.DecoderFactory
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder
import com.davemorrissey.labs.subscaleview.provider.InputProvider

class CroppingRegionDecoder(
    private val cropBorders: Rect,
) : ImageRegionDecoder {

    private lateinit var decoder: BitmapRegionDecoder

    override fun init(context: Context, provider: InputProvider): Point {
        val inputStream = provider.openStream()
        try {
            decoder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                BitmapRegionDecoder.newInstance(inputStream)!!
            } else {
                @Suppress("DEPRECATION")
                BitmapRegionDecoder.newInstance(inputStream, false)!!
            }
        } finally {
            inputStream.close()
        }
        return Point(cropBorders.width(), cropBorders.height())
    }

    override fun decodeRegion(sRect: Rect, sampleSize: Int): Bitmap {
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        val translatedRect = Rect(
            sRect.left + cropBorders.left,
            sRect.top + cropBorders.top,
            sRect.right + cropBorders.left,
            sRect.bottom + cropBorders.top,
        )
        return decoder.decodeRegion(translatedRect, options)
    }

    override fun isReady(): Boolean {
        return this::decoder.isInitialized && !decoder.isRecycled
    }

    override fun recycle() {
        if (this::decoder.isInitialized) {
            decoder.recycle()
        }
    }
}

class CroppingRegionDecoderFactory(
    private val cropBorders: Rect,
) : DecoderFactory<ImageRegionDecoder> {
    override fun make(): ImageRegionDecoder {
        return CroppingRegionDecoder(cropBorders)
    }
}
