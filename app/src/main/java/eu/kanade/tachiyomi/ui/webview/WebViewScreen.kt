package eu.kanade.tachiyomi.ui.webview

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.toColorInt
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.util.AssistContentScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.openInBrowser
import kotlinx.parcelize.Parcelize
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.WebView

@Parcelize
data class WebViewScreen(
    private val url: String,
    private val initialTitle: String? = null,
    private val sourceId: Long? = null,
) : ParcelableScreen, AssistContentScreen {

    private var assistUrl: String? = null

    override fun onProvideAssistUrl() = assistUrl

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { WebViewScreenModel(url, initialTitle, sourceId) }
        val state by screenModel.state

        var webView by remember { mutableStateOf<WebView?>(null) }

        Scaffold(
            topBar = {
                val title = state.title
                AppBar(
                    title = title,
                    navigationIcon = Icons.Outlined.Close,
                    onNavigationIconClick = navigator::pop,
                    actions = {
                        IconButton(onClick = { webView?.reload() }) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = stringResource(MR.strings.action_reload),
                            )
                        }
                        IconButton(onClick = { webView?.context?.openInBrowser(webView?.url) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_open_in_browser_24dp),
                                contentDescription = stringResource(MR.strings.action_open_in_browser),
                            )
                        }
                    },
                )
            },
        ) { contentPadding ->
            WebView(
                modifier = Modifier
                    .fillMaxSize(),
                // .padding(contentPadding),
                url = url,
                headers = screenModel.headers,
                onPageFinished = {
                    assistUrl = it
                    screenModel.onPageFinished(it)
                },
                onWebView = { webView = it },
            )
        }
    }
}
