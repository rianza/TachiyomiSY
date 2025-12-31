package eu.kanade.tachiyomi.ui.browse.extension.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.ExtensionDetailsScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize
import tachiyomi.presentation.core.screens.LoadingScreen

@Parcelize
class ExtensionDetailsScreen(private val pkgName: String) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ExtensionDetailsScreenModel(pkgName = pkgName) }
        val state by screenModel.state.collectAsState()

        if (state.isLoading) {
            LoadingScreen()
            return
        }

        ExtensionDetailsScreen(
            navigateUp = navigator::pop,
            state = state,
            onClickSource = { navigator.push(SourcePreferencesScreen(it.id)) },
            onClickUninstall = screenModel::uninstall,
            onClickAppInfo = screenModel::openInSettings,
            onClickToggle = screenModel::toggle,
            onClickTogglePinned = screenModel::togglePinned,
        )
    }
}
