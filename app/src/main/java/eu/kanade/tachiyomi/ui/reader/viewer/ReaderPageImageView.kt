package eu.kanade.tachiyomi.ui.reader.viewer

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import coil3.BitmapImage
import coil3.asDrawable
import coil3.dispose
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import coil3.size.Size
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.EASE_IN_OUT_QUAD
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.EASE_OUT_QUAD
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE
import com.github.chrisbanes.photoview.PhotoView
import eu.kanade.domain.base.BasePreferences
import eu.kanade.tachiyomi.data.coil.cropBorders
import eu.kanade.tachiyomi.data.coil.customDecoder
import eu.kanade.tachiyomi.ui.reader.viewer.webtoon.WebtoonSubsamplingImageView
import eu.kanade.tachiyomi.util.system.animatorDurationScale
import eu.kanade.tachiyomi.util.view.isVisibleOnScreen
import okio.BufferedSource
import tachiyomi.core.common.util.system.ImageUtil
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * A wrapper view for showing page image.
 * OPTIMIZED VERSION - Text clarity + Memory efficient + No flicker
 */
open class ReaderPageImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttrs: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    private val isWebtoon: Boolean = false,
) : FrameLayout(context, attrs, defStyleAttrs, defStyleRes) {

    private val basePreferences: BasePreferences by lazy { Injekt.get() }
    
    // Gunakan SSIV decoder untuk strip panjang (FIX TEXT BLUR)
    private val alwaysDecodeLongStripWithSSIV: Boolean by lazy {
        basePreferences.alwaysDecodeLongStripWithSSIV().get()
    }

    private var pageView: View? = null
    private var config: Config? = null
    
    // Pre-set background untuk mencegah flicker
    private var isBackgroundSet = false

    var onImageLoaded: (() -> Unit)? = null
    var onImageLoadError: ((Throwable?) -> Unit)? = null
    var onScaleChanged: ((newScale: Float) -> Unit)? = null
    var onViewClicked: (() -> Unit)? = null

    var pageBackground: Drawable? = null

    init {
        // Set layer type untuk mencegah flicker saat tap
        setLayerType(LAYER_TYPE_HARDWARE, null)
        
        // Set default background untuk mencegah bayangan hitam
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }

    @CallSuper
    open fun onImageLoaded() {
        onImageLoaded?.invoke()
        if (!isBackgroundSet) {
            background = pageBackground
            isBackgroundSet = true
        }
    }

    @CallSuper
    open fun onImageLoadError(error: Throwable?) {
        onImageLoadError?.invoke(error)
    }

    @CallSuper
    open fun onScaleChanged(newScale: Float) {
        onScaleChanged?.invoke(newScale)
    }

    @CallSuper
    open fun onViewClicked() {
        onViewClicked?.invoke()
    }

    open fun onPageSelected(forward: Boolean) {
        (pageView as? SubsamplingScaleImageView)?.let { view ->
            if (view.isReady) {
                landscapeZoom(view, forward)
            } else {
                view.setOnImageEventListener(
                    object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
                        override fun onReady() {
                            setupZoom(view, config)
                            landscapeZoom(view, forward)
                            this@ReaderPageImageView.onImageLoaded()
                        }

                        override fun onImageLoadError(e: Exception) {
                            onImageLoadError(e)
                        }
                    },
                )
            }
        }
    }

    private fun landscapeZoom(view: SubsamplingScaleImageView, forward: Boolean) {
        val cfg = config ?: return
        if (cfg.landscapeZoom &&
            cfg.minimumScaleType == SCALE_TYPE_CENTER_INSIDE &&
            view.sWidth > view.sHeight &&
            view.scale == view.minScale
        ) {
            view.handler?.postDelayed({
                val point = when (cfg.zoomStartPosition) {
                    ZoomStartPosition.LEFT -> if (forward) PointF(0F, 0F) else PointF(view.sWidth.toFloat(), 0F)
                    ZoomStartPosition.RIGHT -> if (forward) PointF(view.sWidth.toFloat(), 0F) else PointF(0F, 0F)
                    ZoomStartPosition.CENTER -> view.center ?: return@postDelayed
                }

                val targetScale = view.height.toFloat() / view.sHeight.toFloat()
                view.animateScaleAndCenter(targetScale, point)
                    ?.withDuration(500)
                    ?.withEasing(EASE_IN_OUT_QUAD)
                    ?.withInterruptible(true)
                    ?.start()
            }, 500)
        }
    }

    fun setImage(drawable: Drawable, config: Config) {
        this.config = config
        this.isBackgroundSet = false
        
        // Pre-set background sebelum load image untuk mencegah flicker
        pageBackground?.let { background = it }
        
        if (drawable is Animatable) {
            prepareAnimatedImageView()
            setAnimatedImage(drawable, config)
        } else {
            prepareNonAnimatedImageView()
            setNonAnimatedImage(drawable, config)
        }
    }

    fun setImage(source: BufferedSource, isAnimated: Boolean, config: Config) {
        this.config = config
        this.isBackgroundSet = false
        
        // Pre-set background sebelum load image untuk mencegah flicker
        pageBackground?.let { background = it }
        
        if (isAnimated) {
            prepareAnimatedImageView()
            setAnimatedImage(source, config)
        } else {
            prepareNonAnimatedImageView()
            setNonAnimatedImage(source, config)
        }
    }

    fun recycle() {
        pageView?.let { view ->
            when (view) {
                is SubsamplingScaleImageView -> {
                    view.recycle()
                }
                is AppCompatImageView -> {
                    view.dispose()
                    view.setImageDrawable(null)
                }
            }
            view.isVisible = false
        }
        isBackgroundSet = false
    }

    fun canPanLeft(): Boolean = canPan { it.left }
    fun canPanRight(): Boolean = canPan { it.right }

    private fun canPan(fn: (RectF) -> Float): Boolean {
        return (pageView as? SubsamplingScaleImageView)?.let { view ->
            RectF().also { view.getPanRemaining(it) }.let { fn(it) > 1 }
        } ?: false
    }

    fun panLeft() {
        pan { center, view -> center.apply { x -= view.width / view.scale } }
    }

    fun panRight() {
        pan { center, view -> center.apply { x += view.width / view.scale } }
    }

    private fun pan(fn: (PointF, SubsamplingScaleImageView) -> PointF) {
        (pageView as? SubsamplingScaleImageView)?.let { view ->
            val center = view.center ?: return
            view.animateCenter(fn(center, view))
                ?.withEasing(EASE_OUT_QUAD)
                ?.withDuration(250)
                ?.withInterruptible(true)
                ?.start()
        }
    }

    /**
     * Menghitung tile size optimal berdasarkan device capability
     */
    private fun getOptimalTileSize(): Int {
        val threshold = ImageUtil.hardwareBitmapThreshold
        return when {
            threshold >= 8192 -> 2048  // High-end device
            threshold >= 4096 -> 1536  // Mid-range device
            else -> 1024               // Low-end device
        }
    }

    private fun prepareNonAnimatedImageView() {
        if (pageView is SubsamplingScaleImageView) return
        removeView(pageView)

        pageView = if (isWebtoon) {
            WebtoonSubsamplingImageView(context)
        } else {
            SubsamplingScaleImageView(context)
        }.apply {
            // ============ FIX TEXT BLUR - CRITICAL SETTINGS ============
            
            // Tile size optimal untuk text clarity
            setMaxTileSize(getOptimalTileSize())
            
            // PENTING: DPI tinggi untuk text yang tajam
            // Naik dari 180 ke 260 untuk text clarity
            setMinimumTileDpi(260)
            
            // ============ END FIX TEXT BLUR ============
            
            setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
            setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE)
            
            setOnStateChangedListener(
                object : SubsamplingScaleImageView.OnStateChangedListener {
                    override fun onScaleChanged(newScale: Float, origin: Int) {
                        this@ReaderPageImageView.onScaleChanged(newScale)
                    }
                    override fun onCenterChanged(newCenter: PointF?, origin: Int) {}
                },
            )
            setOnClickListener { this@ReaderPageImageView.onViewClicked() }
        }
        addView(pageView, MATCH_PARENT, MATCH_PARENT)
    }

    private fun setupZoom(view: SubsamplingScaleImageView, config: Config?) {
        view.maxScale = view.scale * MAX_ZOOM_SCALE
        view.setDoubleTapZoomScale(view.scale * 2)

        when (config?.zoomStartPosition) {
            ZoomStartPosition.LEFT -> view.setScaleAndCenter(view.scale, PointF(0F, 0F))
            ZoomStartPosition.RIGHT -> view.setScaleAndCenter(view.scale, PointF(view.sWidth.toFloat(), 0F))
            ZoomStartPosition.CENTER -> view.setScaleAndCenter(view.scale, view.center)
            null -> {}
        }
    }

    private fun setNonAnimatedImage(
        data: Any,
        config: Config,
    ) {
        val view = pageView as? SubsamplingScaleImageView ?: return
        
        view.apply {
            setDoubleTapZoomDuration(config.zoomDuration.getSystemScaledDuration())
            setMinimumScaleType(config.minimumScaleType)
            setMinimumDpi(1)
            setCropBorders(config.cropBorders)
            
            setOnImageEventListener(
                object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
                    override fun onReady() {
                        setupZoom(this@apply, config)
                        if (isVisibleOnScreen()) landscapeZoom(this@apply, true)
                        this@ReaderPageImageView.onImageLoaded()
                    }

                    override fun onImageLoadError(e: Exception) {
                        onImageLoadError(e)
                    }
                },
            )

            when (data) {
                is BitmapDrawable -> {
                    setImage(ImageSource.bitmap(data.bitmap))
                    isVisible = true
                }
                is BufferedSource -> {
                    // SELALU gunakan SSIV untuk webtoon dengan strip panjang
                    // Ini adalah kunci untuk text clarity
                    if (!isWebtoon || alwaysDecodeLongStripWithSSIV) {
                        setHardwareConfig(ImageUtil.canUseHardwareBitmap(data))
                        setImage(ImageSource.inputStream(data.inputStream()))
                        isVisible = true
                        return@apply
                    }

                    // Fallback ke Coil dengan EXACT precision untuk text clarity
                    loadWithCoilExact(data, config)
                }
                else -> {
                    throw IllegalArgumentException("Not implemented for class ${data::class.simpleName}")
                }
            }
        }
    }

    /**
     * Load image dengan Coil menggunakan EXACT precision untuk text clarity
     */
    private fun SubsamplingScaleImageView.loadWithCoilExact(
        data: BufferedSource,
        config: Config
    ) {
        val request = ImageRequest.Builder(context)
            .data(data)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .target(
                onSuccess = { result ->
                    val image = result as BitmapImage
                    setImage(ImageSource.bitmap(image.bitmap))
                    isVisible = true
                },
            )
            .listener(
                onError = { _, result ->
                    onImageLoadError(result.throwable)
                      
                    
                },
            )
            // ============ FIX TEXT BLUR ============
            .size(Size.ORIGINAL) // Gunakan ukuran original, tidak di-downscale
            .precision(Precision.EXACT) // EXACT precision, bukan INEXACT
            // ============ END FIX ============
            .cropBorders(config.cropBorders)
            .customDecoder(true)
            .crossfade(false) // Disable untuk mencegah flicker
            .build()
        
        context.imageLoader.enqueue(request)
    }

    private fun prepareAnimatedImageView() {
        if (pageView is AppCompatImageView) return
        removeView(pageView)

        pageView = if (isWebtoon) {
            AppCompatImageView(context)
        } else {
            PhotoView(context)
        }.apply {
            adjustViewBounds = true
            
            // Set layer type untuk smooth rendering
            setLayerType(LAYER_TYPE_HARDWARE, null)

            if (this is PhotoView) {
                setScaleLevels(1F, 2F, MAX_ZOOM_SCALE)
                setOnDoubleTapListener(
                    object : GestureDetector.SimpleOnGestureListener() {
                        override fun onDoubleTap(e: MotionEvent): Boolean {
                            if (scale > 1F) {
                                setScale(1F, e.x, e.y, true)
                            } else {
                                setScale(2F, e.x, e.y, true)
                            }
                            return true
                        }

                        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                            this@ReaderPageImageView.onViewClicked()
                            return true
                        }
                    },
                )
                setOnScaleChangeListener { _, _, _ ->
                    this@ReaderPageImageView.onScaleChanged(scale)
                }
            }
        }
        addView(pageView, MATCH_PARENT, MATCH_PARENT)
    }

    private fun setAnimatedImage(
        data: Any,
        config: Config,
    ) {
        val view = pageView as? AppCompatImageView ?: return
        
        view.apply {
            if (this is PhotoView) {
                setZoomTransitionDuration(config.zoomDuration.getSystemScaledDuration())
            }

            val request = ImageRequest.Builder(context)
                .data(data)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .target(
                    onSuccess = { result ->
                        val drawable = result.asDrawable(context.resources)
                        setImageDrawable(drawable)
                        (drawable as? Animatable)?.start()
                        isVisible = true
                        this@ReaderPageImageView.onImageLoaded()
                    },
                )
                .listener(
                    onError = { _, result ->
                        onImageLoadError(result.throwable)
                      
                    
                    },
                )
                .crossfade(false) // Disable crossfade untuk mencegah flicker
                .build()
            
            context.imageLoader.enqueue(request)
        }
    }

    private fun Int.getSystemScaledDuration(): Int {
        return (this * context.animatorDurationScale).toInt().coerceAtLeast(1)
    }

    /**
     * Config untuk image loading
     */
    data class Config(
        val zoomDuration: Int,
        val minimumScaleType: Int = SCALE_TYPE_CENTER_INSIDE,
        val cropBorders: Boolean = false,
        val zoomStartPosition: ZoomStartPosition = ZoomStartPosition.CENTER,
        val landscapeZoom: Boolean = false,
    )

    enum class ZoomStartPosition {
        LEFT,
        CENTER,
        RIGHT,
    }
    
    companion object {
        private const val MAX_ZOOM_SCALE = 5F
    }
}
