
package eu.kanade.tachiyomi.ui.browse.extension.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.ExtensionDetailsScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.parcelize.Parcelize
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Parcelize
data class ExtensionDetailsScreen(private val pkgName: String) : ParcelableScreen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ExtensionDetailsScreenModel(pkgName = pkgName) }
        val state by screenModel.state.collectAsState()

        ExtensionDetailsScreen(
            navigateUp = navigator::pop,
            state = state,
            onClickUninstall = screenModel::uninstallExtension,
            onClickAppInfo = { screenModel.openInAppInfo(context) },
            onClickClearCookies = screenModel::clearCookies,
            onClickToggleObsolete = screenModel::toggleObsolete,
            onClickToggleInApp = screenModel::toggleInApp,
            onClickSource = {
                if (it.isUsed) {
                    val source = Injekt.get<ExtensionManager>().getSource(it.id)
                    source?.let { navigator.push(SourcePreferencesScreen(source.id)) }
                } else {
                    context.toast(R.string.source_not_installed)
                }
            },
        )
    }
}
