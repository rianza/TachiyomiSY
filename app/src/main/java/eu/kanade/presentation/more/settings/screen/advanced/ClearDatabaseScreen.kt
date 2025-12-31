package eu.kanade.presentation.more.settings.screen.advanced

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.ClearDatabaseScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class ClearDatabaseScreen : ParcelableScreen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ClearDatabaseScreenModel() }
        val scope = rememberCoroutineScope()
        ClearDatabaseScreen(
            navigateUp = navigator::pop,
            state = screenModel.state,
            onClickClear = {
                scope.launch {
                    screenModel.clearDatabase(context)
                    navigator.pop()
                }
            },
        )
    }
}
