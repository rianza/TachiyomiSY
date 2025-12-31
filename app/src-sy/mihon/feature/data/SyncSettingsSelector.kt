package eu.kanade.presentation.more.settings.screen.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.SyncSettingsSelectorScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.parcelize.Parcelize

@Parcelize
class SyncSettingsSelectorScreen : ParcelableScreen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SyncSettingsSelectorScreenModel() }
        val state by screenModel.state.collectAsState()

        SyncSettingsSelectorScreen(
            navigateUp = navigator::pop,
            state = state,
            onClick = screenModel::toggle,
            onSync = {
                screenModel.sync()
                navigator.pop()
            },
        )
    }
}
