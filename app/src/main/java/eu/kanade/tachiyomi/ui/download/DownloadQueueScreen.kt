package eu.kanade.tachiyomi.ui.download

import androidx.compose.runtime.Composable
import eu.kanade.presentation.download.DownloadQueueScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
object DownloadQueueScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        DownloadQueueScreen()
    }
}
