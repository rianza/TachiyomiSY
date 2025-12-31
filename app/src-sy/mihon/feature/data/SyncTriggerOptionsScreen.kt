package eu.kanade.presentation.more.settings.screen.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.SyncTriggerOptionsScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class SyncTriggerOptionsScreen : ParcelableScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SyncTriggerOptionsScreenModel() }
        val state by screenModel.state.collectAsState()

        SyncTriggerOptionsScreen(
            navigateUp = navigator::pop,
            state = state,
            onClick = screenModel::toggle,
        )
    }
}
