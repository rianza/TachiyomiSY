package eu.kanade.presentation.more.settings.screen.data

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.CreateBackupScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.parcelize.Parcelize
import tachiyomi.i18n.MR

@Parcelize
class CreateBackupScreen : ParcelableScreen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CreateBackupScreenModel() }
        val state by screenModel.state.collectAsState()

        val chooseBackupDir = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        ) {
            if (it != null) {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
                screenModel.createBackup(context, it)
            }
        }

        CreateBackupScreen(
            navigateUp = navigator::pop,
            state = state,
            onBackup = {
                if (!screenModel.isBackingUp()) {
                    chooseBackupDir.launch(CreateBackupScreenModel.getInitialBackupFileName())
                }
            },
            toggleInformation = screenModel::toggleInformation,
            toggleServices = screenModel::toggleServices,
            toggleAppPreferences = screenModel::toggleAppPreferences,
            toggleSourcePreferences = screenModel::toggleSourcePreferences,
        )

        LaunchedEffect(state.error) {
            if (state.error != null) {
                context.toast(state.error)
                screenModel.error = null
            }
        }

        LaunchedEffect(state.isSuccessful) {
            if (state.isSuccessful) {
                navigator.pop()
                context.toast(MR.strings.backup_created)
            }
        }
    }
}
