package eu.kanade.tachiyomi.ui.download

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.download.DownloadScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data object DownloadQueueScreen : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DownloadQueueScreenModel() }
        DownloadScreen(
            screenModel = screenModel,
            navigateUp = navigator::pop,
        )
    }
}
