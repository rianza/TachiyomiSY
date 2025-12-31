package eu.kanade.tachiyomi.ui.webview

import android.graphics.Bitmap
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.util.AssistContentScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.presentation.webview.WebViewScreen
import eu.kanade.presentation.webview.components.WebViewActions
import eu.kanade.tachiyomi.network.NetworkHelper
import kotlinx.parcelize.Parcelize
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Parcelize
data class WebViewScreen(
    private val url: String,
    private val initialTitle: String? = null,
    private val sourceId: Long? = null,
) : ParcelableScreen(), AssistContentScreen {

    private var assistUrl: String? = null

    override fun onProvideAssistUrl() = assistUrl

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val screenModel = rememberScreenModel {
            WebViewScreenModel(
                url = url,
                initialTitle = initialTitle,
                sourceId = sourceId,
            )
        }
        val webViewState = remember { screenModel.webViewState }

        WebViewScreen(
            onNavigateUp = navigator::pop,
            initialTitle = initialTitle,
            url = webViewState.url,
            state = screenModel.state,
            webViewState = webViewState,
            onShare = screenModel::shareWebpage,
            onOpenInBrowser = screenModel::openInBrowser,
            onClearCookies = screenModel::clearCookies,
        )
    }
}
