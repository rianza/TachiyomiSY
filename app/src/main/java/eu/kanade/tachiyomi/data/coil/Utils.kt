package eu.kanade.tachiyomi.data.coil

import coil3.Extras
import coil3.getExtra
import coil3.request.ImageRequest
import coil3.request.Options
import coil3.size.Dimension
import coil3.size.Scale
import coil3.size.Size
import coil3.size.isOriginal
import android.graphics.Bitmap
import coil3.size.pxOrElse

fun ImageRequest.Builder.bitmapConfig(config: Bitmap.Config) = apply {
    extras[bitmapConfigKey] = config
}

val Options.bitmapConfig: Bitmap.Config
    get() = getExtra(bitmapConfigKey)

private val bitmapConfigKey = Extras.Key(default = Bitmap.Config.RGB_565)

internal inline fun Size.widthPx(scale: Scale, original: () -> Int): Int {
    return if (isOriginal) original() else width.toPx(scale)
}

internal inline fun Size.heightPx(scale: Scale, original: () -> Int): Int {
    return if (isOriginal) original() else height.toPx(scale)
}

internal fun Dimension.toPx(scale: Scale): Int = pxOrElse {
    when (scale) {
        Scale.FILL -> Int.MIN_VALUE
        Scale.FIT -> Int.MAX_VALUE
    }
}

fun ImageRequest.Builder.cropBorders(enable: Boolean) = apply {
    extras[cropBordersKey] = enable
}

val Options.cropBorders: Boolean
    get() = getExtra(cropBordersKey)

private val cropBordersKey = Extras.Key(default = false)

fun ImageRequest.Builder.customDecoder(enable: Boolean) = apply {
    extras[customDecoderKey] = enable
}

val Options.customDecoder: Boolean
    get() = getExtra(customDecoderKey)

private val customDecoderKey = Extras.Key(default = false)
