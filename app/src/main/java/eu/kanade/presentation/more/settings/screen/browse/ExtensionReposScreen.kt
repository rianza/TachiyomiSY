package eu.kanade.presentation.more.settings.screen.browse

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.ExtensionReposScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class ExtensionReposScreen(private val repoUrl: String?) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ExtensionReposScreenModel() }
        ExtensionReposScreen(
            navigator = navigator,
            screenModel = screenModel,
            repoUrl = repoUrl,
        )
    }
}
