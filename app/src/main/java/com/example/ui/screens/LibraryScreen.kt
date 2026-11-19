package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ArticleEntity
import com.example.data.local.ArticleStatus
import com.example.data.local.BookmarkEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.CollectionEntity
import com.example.data.local.PersonalNoteEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.ArticleSummaryCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.WarmCream
import com.example.ui.theme.crispFor

@Composable
fun LibraryScreen(
    language: AppLanguage,
    currentUser: UserEntity?,
    savedArticles: List<ArticleEntity>,
    offlineArticles: List<ArticleEntity>,
    bookmarks: List<BookmarkEntity>,
    notes: List<PersonalNoteEntity>,
    collections: List<CollectionEntity>,
    readingHistory: List<ReadingProgressEntity>,
    allApprovedArticles: List<ArticleEntity>,
    myArticles: List<ArticleEntity>,
    followedAuthors: List<UserEntity>,
    categories: List<CategoryEntity>,
    lowDataMode: Boolean,
    onOpenArticle: (String) -> Unit,
    onEditArticle: (ArticleEntity) -> Unit,
    onOpenAuthor: (String) -> Unit,
    onDeleteBookmark: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCreateCollection: (String, String) -> Unit,
    onRequireAuth: () -> Unit
) {
    val strings = AppStrings.get(language)
    val categoryMap = categories.associateBy { it.slug }
    val articleMap = allApprovedArticles.associateBy { it.id }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCollectionDialog by remember { mutableStateOf(false) }
    var colName by remember { mutableStateOf("") }
    var colDesc by remember { mutableStateOf("") }

    val tabs = listOf(
        strings.savedTab,
        strings.offlineTab,
        strings.bookmarksTab,
        strings.historyTab,
        strings.notesTab,
        strings.collectionsTab,
        strings.mySubmissionsTab,
        strings.followedAuthorsTab
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs.size) { index ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(tabs[index]) }
                )
            }
        }

        if (currentUser == null && selectedTab != 1) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, IslamicGold)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "ব্যক্তিগত লাইব্রেরি ব্যবহার করতে লগইন করুন" else "Sign in for Personal Library",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = onRequireAuth,
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicNavy, contentColor = WarmCream)
                        ) {
                            Text(strings.signInButton)
                        }
                    }
                }
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    if (savedArticles.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(savedArticles, key = { it.id }) { article ->
                        ArticleSummaryCard(article, categoryMap[article.categorySlug], language, lowDataMode, { onOpenArticle(article.id) })
                    }
                }
                1 -> {
                    if (offlineArticles.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(offlineArticles, key = { it.id }) { article ->
                        ArticleSummaryCard(article, categoryMap[article.categorySlug], language, lowDataMode, { onOpenArticle(article.id) })
                    }
                }
                2 -> {
                    if (bookmarks.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(bookmarks, key = { it.id }) { bm ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenArticle(bm.articleId) },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        bm.articleTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            .crispFor(bm.articleTitle, language.code)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "\"${bm.excerpt}\"",
                                        style = MaterialTheme.typography.bodySmall.crispFor(bm.excerpt, language.code)
                                    )
                                }
                                IconButton(onClick = { onDeleteBookmark(bm.id) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete bookmark")
                                }
                            }
                        }
                    }
                }
                3 -> {
                    if (readingHistory.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(readingHistory, key = { "${it.userId}_${it.articleId}" }) { prog ->
                        val article = articleMap[prog.articleId]
                        if (article != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenArticle(article.id) },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            article.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                                .crispFor(article.title, article.language),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text("${prog.progressPercent}%", color = IslamicGold, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { prog.progressPercent / 100f },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = IslamicGold
                                    )
                                }
                            }
                        }
                    }
                }
                4 -> {
                    if (notes.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(notes, key = { it.id }) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenArticle(note.articleId) },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        note.articleTitle,
                                        style = MaterialTheme.typography.labelLarge.copy(color = IslamicGold)
                                            .crispFor(note.articleTitle, language.code)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        note.noteText,
                                        style = MaterialTheme.typography.bodyMedium.crispFor(note.noteText, language.code)
                                    )
                                }
                                IconButton(onClick = { onDeleteNote(note.id) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete note")
                                }
                            }
                        }
                    }
                }
                5 -> {
                    item {
                        OutlinedButton(
                            onClick = { showCollectionDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, IslamicGold)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                            Text(if (language == AppLanguage.BN) "নতুন সংগ্রহ তৈরি করুন" else "Create New Collection")
                        }
                    }
                    if (collections.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(collections, key = { "${it.userId}_${it.name}" }) { col ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    col.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        .crispFor(col.name, language.code)
                                )
                                if (col.description.isNotBlank()) {
                                    Text(
                                        col.description,
                                        style = MaterialTheme.typography.bodySmall.crispFor(col.description, language.code)
                                    )
                                }
                            }
                        }
                    }
                }
                6 -> {
                    if (myArticles.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(myArticles, key = { it.id }) { art ->
                        val status = art.parsedStatus()
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusPill(status, language)
                                    if (status == ArticleStatus.DRAFT || status == ArticleStatus.REJECTED) {
                                        TextButton(onClick = { onEditArticle(art) }) {
                                            Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Text(if (language == AppLanguage.BN) "সম্পাদনা / পুনরায় জমা" else "Edit & Resubmit")
                                        }
                                    } else if (status == ArticleStatus.APPROVED) {
                                        TextButton(onClick = { onOpenArticle(art.id) }) {
                                            Text(if (language == AppLanguage.BN) "পড়ুন" else "Read")
                                        }
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    art.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        .crispFor(art.title, art.language)
                                )
                                Text(
                                    "${strings.viewsLabel}: ${art.viewsCount} • ${strings.likesLabel}: ${art.likesCount}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                if (!art.rejectionReason.isNullOrBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    val reasonText = "${if (language == AppLanguage.BN) "সম্পাদকের মন্তব্য" else "Editor Feedback"}: ${art.rejectionReason}"
                                    Text(
                                        text = reasonText,
                                        style = MaterialTheme.typography.bodySmall.copy(color = StatusRejected)
                                            .crispFor(reasonText, language.code)
                                    )
                                }
                            }
                        }
                    }
                }
                7 -> {
                    if (followedAuthors.isEmpty()) item { EmptyLibraryCard(language) }
                    else items(followedAuthors, key = { it.uid }) { author ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenAuthor(author.uid) },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    author.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        .crispFor(author.fullName, language.code)
                                )
                                Text("@${author.username} • ${author.userId}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCollectionDialog) {
        AlertDialog(
            onDismissRequest = { showCollectionDialog = false },
            title = { Text(if (language == AppLanguage.BN) "নতুন সংগ্রহ" else "New Collection") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = colName,
                        onValueChange = { colName = it },
                        label = { Text(if (language == AppLanguage.BN) "সংগ্রহের নাম" else "Collection Name") }
                    )
                    OutlinedTextField(
                        value = colDesc,
                        onValueChange = { colDesc = it },
                        label = { Text(if (language == AppLanguage.BN) "বিবরণ" else "Description") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onCreateCollection(colName, colDesc)
                    colName = ""
                    colDesc = ""
                    showCollectionDialog = false
                }) { Text(if (language == AppLanguage.BN) "তৈরি করুন" else "Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCollectionDialog = false }) {
                    Text(if (language == AppLanguage.BN) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyLibraryCard(language: AppLanguage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (language == AppLanguage.BN) "এখনো কোনো তথ্য সংরক্ষিত নেই।" else "Nothing saved here yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
