package exh.pagepreview

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.PagePreviewScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class PagePreviewScreen(private val mangaId: Long) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { PagePreviewScreenModel(mangaId) }
        PagePreviewScreen(
            navigateUp = navigator::pop,
            screenModel = screenModel,
        )
    }
}
