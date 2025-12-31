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
import tachiyomi.presentation.core.screens.LoadingScreen

@Parcelize
class BiometricTimesScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { BiometricTimesScreenModel() }

        val state by screenModel.state.collectAsState()

        if (state.isLoading) {
            LoadingScreen()
            return
        }

        BiometricTimesScreen(
            state = state,
            onClickCreate = { screenModel.showDialog(BiometricTimesDialog.Create) },
            onClickDelete = { screenModel.showDialog(BiometricTimesDialog.Delete(it)) },
            navigateUp = navigator::pop,
        )

        when (val dialog = state.dialog) {
            null -> {}
            BiometricTimesDialog.Create -> {
                BiometricTimesCreateDialog(
                    onDismissRequest = screenModel::dismissDialog,
                    onCreate = screenModel::create,
                )
            }
            is BiometricTimesDialog.Delete -> {
                BiometricTimesDeleteDialog(
                    onDismissRequest = screenModel::dismissDialog,
                    onDelete = { screenModel.delete(dialog.time) },
                )
            }
        }
    }
}
