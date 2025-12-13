
package eu.kanade.tachiyomi.ui.browse.source.feed

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.SourceFeedScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.ui.browse.source.browse.BrowseSourceScreen
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceFeedScreen(
    private val sourceId: Long,
    private val query: String? = null,
) : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SourceFeedScreenModel(sourceId) }

        SourceFeedScreen(
            state = screenModel.state,
            onMangaClick = { navigator.push(MangaScreen(it.id)) },
            onBrowseSource = { navigator.push(BrowseSourceScreen(sourceId, query)) },
        )
    }
}
