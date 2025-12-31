package exh.ui.metadata

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.MetadataViewScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize
import tachiyomi.presentation.core.screens.LoadingScreen

@Parcelize
class MetadataViewScreen(private val mangaId: Long, private val sourceId: Long) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MetadataViewScreenModel(mangaId, sourceId) }
        val state = screenModel.state

        if (state == null) {
            LoadingScreen()
            return
        }

        MetadataViewScreen(
            navigateUp = navigator::pop,
            state = state,
        )
    }
}
