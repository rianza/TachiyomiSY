package eu.kanade.presentation.manga.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.MangaCover
import tachiyomi.domain.manga.model.Manga
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun SuggestionsCarousel(
    suggestions: List<Manga>,
    onMangaClick: (Manga) -> Unit,
) {
    if (suggestions.isEmpty()) {
        return
    }

    Column {
        Text(
            text = stringResource(MR.strings.suggestions),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = suggestions,
                key = { it.id },
            ) {
                MangaCover.Book(
                    data = it,
                    onClick = { onMangaClick(it) },
                )
            }
        }
    }
}
