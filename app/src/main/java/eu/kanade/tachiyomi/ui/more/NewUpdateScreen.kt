package eu.kanade.tachiyomi.ui.more

import androidx.compose.runtime.Composable
import eu.kanade.presentation.more.NewUpdateScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewUpdateScreen(
    private val versionName: String,
    private val changelogInfo: String,
    private val releaseLink: String,
    private val downloadLink: String,
) : ParcelableScreen {

    @Composable
    override fun Content() {
        NewUpdateScreen(
            versionName = versionName,
            changelogInfo = changelogInfo,
            releaseLink = releaseLink,
            downloadLink = downloadLink,
        )
    }
}
