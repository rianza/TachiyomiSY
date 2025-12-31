package eu.kanade.presentation.more.settings.screen.debug

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.BackupSchemaScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class BackupSchemaScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BackupSchemaScreenModel() }
        BackupSchemaScreen(
            navigateUp = navigator::pop,
            state = screenModel.state,
        )
    }
}
