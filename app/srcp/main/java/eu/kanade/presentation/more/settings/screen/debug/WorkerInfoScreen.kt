package eu.kanade.presentation.more.settings.screen.debug

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.WorkerInfoScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class WorkerInfoScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { Model(context) }
        WorkerInfoScreen(
            navigateUp = navigator::pop,
            state = screenModel.state,
            onPrune = screenModel::prune,
        )
    }

    private class Model(context: Context) : ScreenModel {
        // ... (rest of the model implementation)
    }
}
