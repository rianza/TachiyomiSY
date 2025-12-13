
package eu.kanade.tachiyomi.ui.browse.source

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmartSearchConfig(val origTitle: String, val origMangaId: Long? = null) : Parcelable
