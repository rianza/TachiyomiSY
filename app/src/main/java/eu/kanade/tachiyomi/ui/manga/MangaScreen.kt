package eu.kanade.tachiyomi.ui.manga

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.domain.manga.model.hasCustomCover
import eu.kanade.presentation.category.components.ChangeCategoryDialog
import eu.kanade.presentation.manga.ChapterSettingsDialog
import eu.kanade.presentation.manga.CoverOldDialog
import eu.kanade.presentation.manga.DeleteChaptersDialog
import eu.kanade.presentation.manga.DuplicateMangaDialog
import eu.kanade.presentation.manga.EditCoverAction
import eu.kanade.presentation.manga.MangaScreen
import eu.kanade.presentation.manga.components.ChapterHeader
import eu.kanade.presentation.manga.components.MangaBottomActionMenu
import eu.kanade.presentation.manga.components.MangaChapterListItem
import eu.kanade.presentation.manga.components.MangaToolbar
import eu.kanade.presentation.util.AssistContentScreen
import eu.kanade.presentation.util.isTabletUi
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.DownloadService
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.isLocalOrStub
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.ui.base.screen.ParcelableScreen
import eu.kanade.tachiyomi.ui.browse.migration.search.MigrateSearchScreen
import eu.kanade.tachiyomi.ui.browse.source.browse.BrowseSourceScreen
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.GlobalSearchScreen
import eu.kanade.tachiyomi.ui.category.CategoryScreen
import eu.kanade.tachiyomi.ui.home.HomeScreen
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import eu.kanade.tachiyomi.util.system.copyToClipboard
import eu.kanade.tachiyomi.util.system.toShareIntent
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.manga.model.Manga
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.screens.LoadingScreen

@Parcelize
data class MangaScreen(
    private val mangaId: Long,
    val fromSource: Boolean = false,
) : ParcelableScreen(), AssistContentScreen {

    override fun onProvideAssistUrl() = (screenModel?.source as? HttpSource)?.getMangaUrl(screenModel!!.manga)

    private var screenModel: MangaScreenModel? = null

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        screenModel = rememberScreenModel { MangaScreenModel(mangaId) }

        val state by screenModel!!.state.collectAsState()

        if (state.isLoading) {
            LoadingScreen()
            return
        }

        val successState = state as MangaScreenModel.State.Success
        val isHttpSource = remember { successState.source !is Source.Stub }

        MangaScreen(
            state = successState,
            snackbarHostState = screenModel!!.snackbarHostState,
            isTabletUi = isTabletUi(),
            onBackClicked = navigator::pop,
            onCloseClicked = { navigator.popUntilRoot() },
            onMangaClicked = { openManga(navigator, successState.manga.source) },
            onChapterClicked = { openChapter(context, it) },
            onDownloadChapter = screenModel!!::runChapterDownloadActions,
            onAddToLibraryClicked = screenModel!!::toggleFavorite,
            onWebViewClicked = { openMangaInWebView(context, screenModel) },
            onWebViewLongClicked = { copyMangaUrl(context, screenModel) },
            onTrackingClicked = screenModel!!::showTrackDialog,
            onTagSearch = { scope ->
                val screen = when (scope) {
                    is MangaScreenModel.TagSearch.Genre -> GlobalSearchScreen(successState.manga.genreToQuery())
                    is MangaScreenModel.TagSearch.Author -> GlobalSearchScreen(successState.manga.authorToQuery())
                    is MangaScreenModel.TagSearch.Artist -> GlobalSearchScreen(successState.manga.artistToQuery())
                }
                navigator.push(screen)
            },
            onFilterButtonClicked = screenModel!!::showSettingsDialog,
            onRefresh = screenModel!!::fetchAllFromSource,
            onContinueReading = {
                screenModel!!.getNextUnreadChapter()?.let {
                    openChapter(context, it)
                }
            },
            onSearch = { query, global ->
                val screen = if (global) {
                    GlobalSearchScreen(query)
                } else {
                    BrowseSourceScreen(successState.source.id, query)
                }
                navigator.push(screen)
            },
            onCoverClicked = screenModel!!::showCoverDialog,
            onShareClicked = { shareManga(context, screenModel) },
            onDownloadActionClicked = screenModel!!::runDownloadAction,
            onEditCategoryClicked = screenModel!!::showChangeCategoryDialog,
            onMigrateClicked = { navigator.push(MigrateSearchScreen(successState.manga.id)) },
            onEditInfoClicked = screenModel!!::showEditMangaInfoDialog,
            onRecommendClicked = { navigator.push(BrowseSourceScreen(successState.source.id, "rec:${successState.manga.title}")) },
            onMergedSettingsClicked = screenModel!!::showMergeSettingsDialog,
            onAboutThisVersionClicked = screenModel!!::showAboutThisVersionDialog,
            onChapterHeaderClick = screenModel!!::resetFilter,
            onSelectAll = screenModel!!::selectAllChapters,
            onInvertSelection = screenModel!!::invertSelection,
            onChapterSelected = screenModel!!::selectChapter,
            onMultiBookmarkClicked = screenModel!!::bookmarkChapters,
            onMultiMarkAsReadClicked = screenModel!!::markChaptersRead,
            onMarkPreviousAsReadClicked = screenModel!!::markPreviousChapterRead,
            onMultiDeleteClicked = screenModel!!::showDeleteChapterDialog,
        )

        val onDismissRequest = { screenModel!!.dismissDialog() }
        when (val dialog = successState.dialog) {
            null -> {}
            is MangaScreenModel.Dialog.ChangeCategory -> {
                ChangeCategoryDialog(
                    initialSelection = dialog.initialSelection,
                    onDismissRequest = onDismissRequest,
                    onEditCategories = { navigator.push(CategoryScreen) },
                    onConfirm = { include, _ ->
                        screenModel!!.moveMangaToCategories(include)
                    },
                )
            }
            is MangaScreenModel.Dialog.DeleteChapters -> {
                DeleteChaptersDialog(
                    onDismissRequest = onDismissRequest,
                    onConfirm = {
                        screenModel!!.deleteChapters(dialog.chapters, it)
                    },
                )
            }
            is MangaScreenModel.Dialog.DuplicateManga -> {
                DuplicateMangaDialog(
                    onDismissRequest = onDismissRequest,
                    onConfirm = {
                        screenModel!!.toggleFavorite(
                            onRemoved = {
                                screenModel!!.onDuplicateManga(dialog.duplicate, true)
                            },
                        )
                    },
                    onOpenManga = {
                        navigator.push(MangaScreen(dialog.duplicate.id))
                    },
                    duplicateFrom = successState.source.name,
                )
            }
            MangaScreenModel.Dialog.Track -> {
                // TODO: Show modal/sheet instead
                navigator.push(
                    MangaScreen(
                        successState.manga.id,
                        fromSource = fromSource,
                    ),
                    // options = TabNavigator.Action.SHOW_AS_DIALOG,
                )
            }
            MangaScreenModel.Dialog.FullCover -> {
                val hasCustomCover = remember(successState.manga) { successState.manga.hasCustomCover() }
                CoverOldDialog(
                    manga = successState.manga,
                    isCustomCover = hasCustomCover,
                    onShareClick = { shareCover(context, screenModel) },
                    onSaveClick = { saveCover(context, screenModel) },
                    onEditClick = if (successState.manga.favorite) {
                        {
                            onDismissRequest()
                            screenModel!!.showEditCoverDialog()
                        }
                    } else {
                        null
                    },
                    onDismissRequest = onDismissRequest,
                )
            }
            MangaScreenModel.Dialog.EditCover -> {
                var isSaving by remember { mutableStateOf(false) }
                Scaffold(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
                    topBar = {
                        MangaToolbar(
                            title = successState.manga.title,
                            onBackClicked = {
                                onDismissRequest()
                            },
                        )
                    },
                    bottomBar = {
                        MangaBottomActionMenu(
                            onEdit = {
                                screenModel!!.showEditCoverDialog(it)
                            },
                        )
                    },
                ) { contentPadding ->
                    Box(modifier = Modifier.padding(contentPadding), contentAlignment = Alignment.Center) {
                    }
                }
            }
            MangaScreenModel.Dialog.ChapterSettings -> {
                ChapterSettingsDialog(
                    onDismissRequest = onDismissRequest,
                    manga = successState.manga,
                    onDownloadFilterChanged = screenModel!!::setDownloadedFilter,
                    onUnreadFilterChanged = screenModel!!::setUnreadFilter,
                    onBookmarkedFilterChanged = screenModel!!::setBookmarkedFilter,
                    onSortModeChanged = screenModel!!::setSorting,
                    onDisplayModeChanged = screenModel!!::setDisplayMode,
                    onSetAsDefault = screenModel!!::setCurrentSettingsAsDefault,
                )
            }
        }

        LaunchedEffect(Unit) {
            screenModel!!.events.collectLatest {
                when (it) {
                    is MangaScreenModel.Event.OpenInWebView -> openMangaInWebView(context, screenModel)
                    is MangaScreenModel.Event.OpenChapter -> openChapter(context, it.chapter)
                }
            }
        }
    }

    private fun openManga(navigator: Navigator, sourceId: Long) {
        navigator.push(BrowseSourceScreen(sourceId))
    }

    private fun openMangaInWebView(context: Context, screenModel: MangaScreenModel?) {
        val source = screenModel?.source as? HttpSource ?: return
        val url = try {
            source.getMangaUrl(screenModel.manga)
        } catch (e: Exception) {
            return
        }
        context.openInBrowser(url)
    }

    private fun shareManga(context: Context, screenModel: MangaScreenModel?) {
        val source = screenModel?.source as? HttpSource ?: return
        try {
            val url = source.getMangaUrl(screenModel.manga)
            val intent = url.toShareIntent(context, type = "text/plain")
            context.startActivity(intent)
        } catch (e: Exception) {
            context.toast(e.message)
        }
    }

    private fun copyMangaUrl(context: Context, screenModel: MangaScreenModel?) {
        val source = screenModel?.source as? HttpSource ?: return
        val url = source.getMangaUrl(screenModel.manga)
        context.copyToClipboard(url, url)
    }

    private fun openChapter(context: Context, chapter: Chapter) {
        context.startActivity(ReaderActivity.newIntent(context, chapter.mangaId, chapter.id))
    }
}

private fun Manga.genreToQuery(): String {
    return "genre:${genre.joinToString(",")}"
}
private fun Manga.authorToQuery(): String {
    return "author:$author"
}
private fun Manga.artistToQuery(): String {
    return "artist:$artist"
}
