package eu.kanade.tachiyomi.ui.more

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.NewUpdateScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class NewUpdateScreen(
    private val versionName: String,
    private val changelogInfo: String,
    private val releaseLink: String,
    private val downloadLink: String,
) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        NewUpdateScreen(
            versionName = versionName,
            changelogInfo = changelogInfo,
            releaseLink = releaseLink,
            downloadLink = downloadLink,
            onUpToDate = navigator::pop,
        )
    }
}
