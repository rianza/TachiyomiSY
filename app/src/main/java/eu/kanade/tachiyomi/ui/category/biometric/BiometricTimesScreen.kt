
package eu.kanade.tachiyomi.ui.category.biometric

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.category.BiometricTimesScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data object BiometricTimesScreen : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BiometricTimesScreenModel() }
        val state by screenModel.state.collectAsState()
        BiometricTimesScreen(
            navigateUp = navigator::pop,
            state = state,
            onClick = screenModel::showDialog,
        )
    }
}
