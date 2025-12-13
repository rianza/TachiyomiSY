
package eu.kanade.tachiyomi.ui.browse.source.browse

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.domain.library.model.LibraryDisplayMode
import eu.kanade.domain.manga.model.Manga
import eu.kanade.domain.manga.model.MangaCover
import eu.kanade.presentation.browse.BrowseSourceContent
import eu.kanade.presentation.browse.BrowseSourceScreen
import eu.kanade.presentation.browse.components.BrowseSourceSimpleToolbar
import eu.kanade.presentation.browse.components.BrowseSourceToolbar
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.GlobalSearchScreen
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.parcelize.Parcelize
import tachiyomi.presentation.core.components.material.Scaffold
import kotlin.math.max

@Parcelize
data class BrowseSourceScreen(
    private val sourceId: Long,
    private val smartSearchConfig: SmartSearchConfig? = null,
) : ParcelableScreen, AssistContentScreen {

    @delegate:Transient
    private val screenModel by lazy {
        BrowseSourceScreenModel(sourceId, smartSearchConfig?.origTitle)
    }

    override fun onProvideAssistUrl() = runBlocking {
        (screenModel.source as? HttpSource)?.baseUrl
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val state by produceState<BrowseSourceScreenState>(initialValue = BrowseSourceScreenState.Loading) {
            screenModel.state.filterNotNull().collectLatest {
                value = if (it.source == null) {
                    BrowseSourceScreenState.Error
                } else {
                    BrowseSourceScreenState.Success(
                        source = it.source,
                        searchQuery = it.searchQuery,
                        listing = it.listing,
                        filters = screenModel.filters,
                        mangaDisplayMode = screenModel.displayMode,
                        mangaList = screenModel.mangaPager,
                        dialog = it.dialog,
                    )
                }
            }
        }

        BrowseSourceScreen(
            state = state,
            onSearch = screenModel::search,
            onSearchQueryChange = screenModel::setSearchQuery,
            onFilterClick = screenModel::openFilterSheet,
            onWebViewClick = screenModel::openWebView,
            onHelpClick = { navigator.push(HelpScreen) },
            onLocalHelpClick = screenModel::openLocalSourceHelp,
            onMangaClick = {
                val mangaScreen = if (smartSearchConfig?.origMangaId != null) {
                    MangaScreen(it.id, true, smartSearchConfig.origMangaId)
                } else {
                    MangaScreen(it.id, true)
                }
                navigator.push(mangaScreen)
            },
            onMangaLongClick = { manga ->
                // Delegate to parent screen
            },
            onDisplayModeChange = screenModel::setDisplayMode,
            onBack = navigator::pop,
            onRandomClick = { screenModel.openRandomManga(navigator) },
            // Filter sheet
            onDismissFilterSheet = screenModel::closeFilterSheet,
            onFilterReset = screenModel::resetFilters,
            onFilter = screenModel::searchWithFilters,
            onUpdateFilter = screenModel::setFilter,
            // Change cover
            onChangeCover = screenModel::changeCover,
            // Other dialogs
            onConfirmRemoveManga = {
                screenModel.removeMangaFromLibrary()
                screenModel.closeDialog()
            },
            onDismissDialog = screenModel::closeDialog,
        )
    }

    @Composable
    private fun BrowseSourceScreen(
        state: BrowseSourceScreenState,
        onSearch: (String?) -> Unit,
        onSearchQueryChange: (String?) -> Unit,
        onFilterClick: () -> Unit,
        onWebViewClick: (() -> Unit)?,
        onHelpClick: () -> Unit,
        onLocalHelpClick: () -> Unit,
        onMangaClick: (Manga) -> Unit,
        onMangaLongClick: (Manga) -> Unit,
        onDisplayModeChange: (LibraryDisplayMode) -> Unit,
        onBack: () -> Unit,
        onRandomClick: () -> Unit,

        // Filter sheet
        onDismissFilterSheet: () -> Unit,
        onFilterReset: () -> Unit,
        onFilter: (String) -> Unit,
        onUpdateFilter: (Any) -> Unit,

        // Change cover
        onChangeCover: (Manga) -> Unit,

        // Other dialogs
        onConfirmRemoveManga: () -> Unit,
        onDismissDialog: () -> Unit,
    ) {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                if (state is BrowseSourceScreenState.Success) {
                    val navigateToGlobalSearch = { query: String ->
                        navigator.push(GlobalSearchScreen(query))
                    }
                    if (state.listing == Listing.SEARCH) {
                        BrowseSourceToolbar(
                            searchQuery = state.searchQuery,
                            onSearchQueryChange = onSearchQueryChange,
                            onSearch = onSearch,
                            onSearchInGlobal = navigateToGlobalSearch,
                            onBack = onBack,
                            onFilterClick = onFilterClick,
                        )
                    } else {
                        BrowseSourceSimpleToolbar(
                            title = state.source.name,
                            onSearch = { onSearch(null) },
                            onSearchInGlobal = navigateToGlobalSearch,
                            onBack = onBack,
                        )
                    }
                }
            },
        ) { paddingValues ->
            when (state) {
                is BrowseSourceScreenState.Loading -> {}
                is BrowseSourceScreenState.Error -> {}
                is BrowseSourceScreenState.Success -> {
                    val configuration = LocalConfiguration.current
                    val columnCount = max(1, (configuration.screenWidthDp.dp / 128.dp).toInt())

                    BrowseSourceContent(
                        source = state.source,
                        mangaList = state.mangaList,
                        columns = GridCells.Fixed(columnCount),
                        displayMode = state.mangaDisplayMode,
                        onDisplayModeChange = onDisplayModeChange,
                        dialog = state.dialog,
                        onWebViewClick = onWebViewClick,
                        onHelpClick = onHelpClick,
                        onLocalHelpClick = onLocalHelpClick,
                        onMangaClick = onMangaClick,
                        onMangaLongClick = onMangaLongClick,
                        onFilterClick = onFilterClick,
                        onRandomClick = onRandomClick,
                        paddingValues = paddingValues,
                        // Filter sheet
                        filters = state.filters,
                        onDismissFilterSheet = onDismissFilterSheet,
                        onFilterReset = onFilterReset,
                        onFilter = onFilter,
                        onUpdateFilter = onUpdateFilter,
                        // Change cover
                        onChangeCover = onChangeCover,
                        // Other dialogs
                        onConfirmRemoveManga = onConfirmRemoveManga,
                        onDismissDialog = onDismissDialog,
                    )
                }
            }
        }
    }
}

@Immutable
sealed class BrowseSourceScreenState {
    @Immutable
    object Loading : BrowseSourceScreenState()

    @Immutable
    object Error : BrowseSourceScreenState()

    @Immutable
    data class Success(
        val source: CatalogueSource,
        val searchQuery: String?,
        val listing: Listing,
        val filters: List<Filter<*>>,
        val mangaDisplayMode: State<LibraryDisplayMode>,
        val mangaList: Flow<PagingData<Manga>> = emptyFlow(),
        val dialog: BrowseSourceScreenModel.Dialog?,
    ) : BrowseSourceScreenState() {
        val isUserQuery: Boolean
            get() = listing == Listing.SEARCH && !searchQuery.isNullOrEmpty()
    }
}
