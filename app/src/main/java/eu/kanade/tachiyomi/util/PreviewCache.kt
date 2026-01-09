package eu.kanade.tachiyomi.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Simple disk-backed preview cache for small thumbnails used to show an instant preview
 * for a chapter page (e.g. last read page).
 *
 * Stored under: <cacheDir>/page_previews/
 * File name pattern: <chapterId>_<pageIndex>.jpg
 *
 * Use thumbnail generation/resizing to ensure files stay small.
 */
object PreviewCache {
    private fun previewDir(context: Context): File {
        val dir = File(context.applicationContext.cacheDir, "page_previews")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun previewFile(context: Context, chapterId: Long, pageIndex: Int): File {
        return File(previewDir(context), "${chapterId}_$pageIndex.jpg")
    }

    suspend fun savePreview(context: Context, chapterId: Long, pageIndex: Int, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val file = previewFile(context, chapterId, pageIndex)
            FileOutputStream(file).use { out ->
                // quality tuned for reasonable size
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
                out.flush()
            }
        }
    }

    suspend fun loadPreviewBitmap(context: Context, chapterId: Long, pageIndex: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            val file = previewFile(context, chapterId, pageIndex)
            if (!file.exists()) return@withContext null
            FileInputStream(file).use { fis ->
                BitmapFactory.decodeStream(fis)
            }
        }
    }

    suspend fun previewExists(context: Context, chapterId: Long, pageIndex: Int): Boolean {
        return withContext(Dispatchers.IO) {
            previewFile(context, chapterId, pageIndex).exists()
        }
    }

    // Optional: methods to remove previews for a chapter or clear cache could be added.
}
