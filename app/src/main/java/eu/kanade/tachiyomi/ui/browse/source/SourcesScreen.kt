
package eu.kanade.tachiyomi.ui.browse.source

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.SourceOptionsDialog
import eu.kanade.presentation.browse.SourcesScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.ui.browse.source.browse.BrowseSourceScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourcesScreen(
    private val smartSearchConfig: SmartSearchConfig? = null,
) : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SourcesScreenModel() }

        SourcesScreen(
            state = screenModel.state,
            onClickItem = { source, listing ->
                navigator.push(BrowseSourceScreen(source.id, smartSearchConfig))
            },
            onClickPin = screenModel::togglePin,
            onLongClickItem = { source ->
                screenModel.showSourceDialog(source)
            },
        )

        screenModel.dialog?.let { dialog ->
            val source = dialog.source
            SourceOptionsDialog(
                source = source,
                onClickPin = {
                    screenModel.togglePin(source)
                    screenModel.closeSourceDialog()
                },
                onClickDisable = {
                    screenModel.disableSource(source)
                    screenModel.closeSourceDialog()
                },
                onDismiss = screenModel::closeSourceDialog,
            )
        }
    }
}
