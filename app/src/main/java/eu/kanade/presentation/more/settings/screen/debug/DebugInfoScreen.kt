package eu.kanade.presentation.more.settings.screen.debug

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.DebugInfoScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class DebugInfoScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DebugInfoScreenModel() }
        DebugInfoScreen(
            navigateUp = navigator::pop,
            state = screenModel.state,
            onShare = screenModel::shareDebugInfo,
        )
    }
}
