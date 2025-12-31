package exh.debug

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.SettingsDebugScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class SettingsDebugScreen : ParcelableScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        SettingsDebugScreen(
            navigateUp = navigator::pop,
            onOpenInfo = { navigator.push(DebugInfoScreen()) },
            onOpenCrashLog = { navigator.push(CrashLogScreen()) },
            onOpenWorkerInfo = { navigator.push(WorkerInfoScreen()) },
            onClearCrashLog = { /* TODO */ },
            onOpenBackupSchema = { navigator.push(BackupSchemaScreen()) },
            onOpenCacheInfo = { navigator.push(CacheInfoScreen()) },
        )
    }
}
