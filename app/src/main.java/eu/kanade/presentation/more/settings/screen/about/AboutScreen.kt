package eu.kanade.presentation.more.settings.screen.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.LinkIcon
import eu.kanade.presentation.components.ScrollbarLazyColumn
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.updater.AppUpdateChecker
import eu.kanade.tachiyomi.ui.more.NewUpdateScreen
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import tachiyomi.domain.release.interactor.GetApplicationRelease
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource

@Parcelize
data object AboutScreen : ParcelableScreen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        var isCheckingForUpdate by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(MR.strings.pref_category_about),
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { contentPadding ->
            ScrollbarLazyColumn(
                contentPadding = contentPadding,
            ) {
                item {
                    LogoHeader()
                }
                item {
                    AboutScreenManga(
                        isCheckingForUpdate = isCheckingForUpdate,
                        onClickVersion = {
                            if (!isCheckingForUpdate) {
                                scope.launch {
                                    isCheckingForUpdate = true
                                    try {
                                        val result = AppUpdateChecker().checkForUpdate(context, true)
                                        if (result is GetApplicationRelease.Result.NewUpdate) {
                                            navigator.push(
                                                NewUpdateScreen(
                                                    versionName = result.release.version,
                                                    changelogInfo = result.release.info,
                                                    releaseLink = result.release.releaseLink,
                                                    downloadLink = result.release.getDownloadLink(),
                                                ),
                                            )
                                        } else if (result is GetApplicationRelease.Result.NoNewUpdate) {
                                            context.toast(MR.strings.update_check_no_new_updates)
                                        }
                                    } catch (e: Exception) {
                                        context.toast(e.message)
                                    } finally {
                                        isCheckingForUpdate = false
                                    }
                                }
                            }
                        },
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        LinkIcon(
                            label = stringResource(MR.strings.website),
                            painter = rememberVectorPainter(Icons.Outlined.Public),
                            url = "https://mihon.app",
                        )
                        LinkIcon(
                            label = "Discord",
                            painter = painterResource(R.drawable.ic_discord_24dp),
                            url = "https://discord.gg/mihon",
                        )
                        LinkIcon(
                            label = "GitHub",
                            painter = painterResource(R.drawable.ic_github_24dp),
                            url = "https://github.com/mihonapp/mihon",
                        )
                        LinkIcon(
                            label = "X",
                            painter = painterResource(R.drawable.ic_x_24dp),
                            url = "https://x.com/mihonapp",
                        )
                    }
                }
            }
        }
    }
}
