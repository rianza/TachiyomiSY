package eu.kanade.tachiyomi.ui.browse.source.feed

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.SourceFeedScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class SourceFeedScreen(val sourceId: Long) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SourceFeedScreenModel(sourceId = sourceId) }
        SourceFeedScreen(
            navigator = navigator,
            screenModel = screenModel,
        )
    }
}
