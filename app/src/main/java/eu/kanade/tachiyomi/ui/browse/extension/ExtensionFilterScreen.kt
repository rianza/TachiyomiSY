
package eu.kanade.tachiyomi.ui.browse.extension

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.ExtensionFilterScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
object ExtensionFilterScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ExtensionFilterScreenModel() }
        ExtensionFilterScreen(
            navigateUp = navigator::pop,
            screenModel = screenModel,
        )
    }
}
