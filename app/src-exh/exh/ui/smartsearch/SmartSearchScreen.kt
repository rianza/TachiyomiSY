package exh.ui.smartsearch

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.SmartSearchScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class SmartSearchScreen(private val sourceId: Long, private val smartSearchConfig: SmartSearchConfig) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SmartSearchScreenModel(sourceId, smartSearchConfig) }
        SmartSearchScreen(
            navigateUp = navigator::pop,
            screenModel = screenModel,
        )
    }
}
