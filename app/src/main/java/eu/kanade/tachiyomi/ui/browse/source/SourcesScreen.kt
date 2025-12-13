package eu.kanade.tachiyomi.ui.browse.source

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.BrowseTabWrapper
import eu.kanade.tachiyomi.ui.base.screen.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourcesScreen(private val smartSearchConfig: SmartSearchConfig?) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        BrowseTabWrapper(sourcesTab(smartSearchConfig), onBackPressed = navigator::pop)
    }

    @Parcelize
    data class SmartSearchConfig(val origTitle: String, val origMangaId: Long? = null) : Parcelable
}
