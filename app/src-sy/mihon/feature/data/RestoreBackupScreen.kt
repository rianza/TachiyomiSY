package eu.kanade.presentation.more.settings.screen.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.RestoreBackupScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.parcelize.Parcelize
import tachiyomi.i18n.MR

@Parcelize
class RestoreBackupScreen(private val uri: String?) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { RestoreBackupScreenModel() }
        val state by screenModel.state.collectAsState()

        RestoreBackupScreen(
            navigateUp = navigator::pop,
            state = state,
            onRestore = { screenModel.restoreBackup(context, uri) },
        )

        LaunchedEffect(state.error) {
            if (state.error != null) {
                context.toast(state.error)
                screenModel.error = null
            }
        }

        LaunchedEffect(state.isSuccessful) {
            if (state.isSuccessful) {
                navigator.pop()
                context.toast(MR.strings.restore_completed)
            }
        }
    }
}
