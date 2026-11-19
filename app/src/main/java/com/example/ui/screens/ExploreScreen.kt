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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ArticleEntity
import com.example.data.local.ArticleStatus
import com.example.data.local.CategoryEntity
import com.example.data.local.EditionEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.ArticleSummaryCard
import com.example.ui.components.formatPublicationDate
import com.example.ui.theme.AdorsholipiFontFamily
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGeometricWatermark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.WarmCream
import com.example.ui.theme.crispFor
import com.example.ui.viewmodel.SortOption
import androidx.compose.ui.unit.sp

@Composable
fun ExploreScreen(
    language: AppLanguage,
    searchQuery: String,
    selectedLanguageFilter: String,
    selectedCategoryFilter: String,
    sortOption: SortOption,
    searchResults: List<ArticleEntity>,
    categories: List<CategoryEntity>,
    editions: List<EditionEntity>,
    lowDataMode: Boolean,
    onSearchChange: (String) -> Unit,
    onLanguageFilterChange: (String) -> Unit,
    onCategoryFilterChange: (String) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onOpenArticle: (String) -> Unit,
    onOpenEdition: (String) -> Unit,
    onOpenQuranStudio: () -> Unit
) {
    val strings = AppStrings.get(language)
    val categoryMap = categories.associateBy { it.slug }
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Search Articles, 1 = Editions Archive

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(selectedTabIndex = activeTab) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text(if (language == AppLanguage.BN) "প্রবন্ধ অনুসন্ধান" else "Search Articles") }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text(strings.editionsArchive) }
            )
        }

        if (activeTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text(strings.searchHint) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Outlined.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("explore_search_input")
                    )
                }

                // Language Filter + Sort
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedLanguageFilter == "ALL",
                                onClick = { onLanguageFilterChange("ALL") },
                                label = { Text(strings.allLanguages) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedLanguageFilter == "bn",
                                onClick = { onLanguageFilterChange("bn") },
                                label = {
                                    Text(
                                        "বাংলা",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontFamily = AdorsholipiFontFamily,
                                            letterSpacing = 0.sp
                                        )
                                    )
                                }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedLanguageFilter == "en",
                                onClick = { onLanguageFilterChange("en") },
                                label = { Text("English") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == SortOption.LATEST,
                                onClick = { onSortChange(SortOption.LATEST) },
                                label = { Text(if (language == AppLanguage.BN) "সর্বশেষ" else "Latest") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == SortOption.MOST_READ,
                                onClick = { onSortChange(SortOption.MOST_READ) },
                                label = { Text(if (language == AppLanguage.BN) "সর্বাধিক পঠিত" else "Most Read") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == SortOption.MOST_LIKED,
                                onClick = { onSortChange(SortOption.MOST_LIKED) },
                                label = { Text(if (language == AppLanguage.BN) "সর্বাধিক পছন্দ" else "Most Liked") }
                            )
                        }
                    }
                }

                // Category Chips
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedCategoryFilter == "ALL",
                                onClick = { onCategoryFilterChange("ALL") },
                                label = { Text(strings.allCategories) }
                            )
                        }
                        items(categories, key = { it.slug }) { cat ->
                            val catName = if (language == AppLanguage.BN) cat.nameBn else cat.nameEn
                            FilterChip(
                                selected = selectedCategoryFilter == cat.slug,
                                onClick = { onCategoryFilterChange(cat.slug) },
                                label = { Text(catName, style = MaterialTheme.typography.labelLarge.crispFor(catName, language.code)) }
                            )
                        }
                    }
                }

                // Quick banner to Quran Recitation Studio
                item {
                    Surface(
                        onClick = onOpenQuranStudio,
                        shape = RoundedCornerShape(14.dp),
                        color = IslamicEmeraldDeep,
                        border = BorderStroke(1.dp, IslamicGold)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = IslamicGold)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = strings.quranRecitationCardTitle,
                                style = MaterialTheme.typography.titleSmall.copy(color = WarmCream, fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.weight(1f)
                            )
                            Text("▶", color = IslamicGold)
                        }
                    }
                }

                if (searchResults.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Rounded.AutoStories, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(10.dp))
                                Text(strings.noResultsFound, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                } else {
                    items(searchResults, key = { it.id }) { article ->
                        ArticleSummaryCard(
                            article = article,
                            category = categoryMap[article.categorySlug],
                            language = language,
                            lowDataMode = lowDataMode,
                            onClick = { onOpenArticle(article.id) }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(editions, key = { it.id }) { edition ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenEdition(edition.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = IslamicNavyDeep),
                        border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.65f))
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IslamicGeometricWatermark(
                                modifier = Modifier.matchParentSize(),
                                tint = IslamicGold.copy(alpha = 0.12f),
                                stepDp = 50.dp
                            )
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(color = IslamicGold, shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = edition.editionType,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = IslamicNavyDeep,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = formatPublicationDate(edition.publishedAt, language),
                                        style = MaterialTheme.typography.labelSmall.copy(color = IslamicGoldLight)
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                val edTitle = if (language == AppLanguage.BN) edition.titleBn else edition.titleEn
                                val edDesc = if (language == AppLanguage.BN) edition.descriptionBn else edition.descriptionEn
                                Text(
                                    text = edTitle,
                                    style = MaterialTheme.typography.titleLarge.copy(color = WarmCream, fontWeight = FontWeight.Bold)
                                        .crispFor(edTitle, language.code)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = edDesc,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = WarmCream.copy(alpha = 0.94f))
                                        .crispFor(edDesc, language.code)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditionDetailScreen(
    edition: EditionEntity?,
    articles: List<ArticleEntity>,
    categories: List<CategoryEntity>,
    language: AppLanguage,
    lowDataMode: Boolean,
    onOpenArticle: (String) -> Unit,
    onBack: () -> Unit
) {
    val categoryMap = categories.associateBy { it.slug }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = edition?.let { if (language == AppLanguage.BN) it.titleBn else it.titleEn } ?: "Edition"
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge.crispFor(titleText, language.code)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicNavyDeep,
                    titleContentColor = WarmCream,
                    navigationIconContentColor = WarmCream
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            edition?.let { ed ->
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = IslamicNavyDeep),
                        border = BorderStroke(1.dp, IslamicGold)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val edTitle = if (language == AppLanguage.BN) ed.titleBn else ed.titleEn
                            val edDesc = if (language == AppLanguage.BN) ed.descriptionBn else ed.descriptionEn
                            Text(
                                text = edTitle,
                                style = MaterialTheme.typography.headlineSmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                                    .crispFor(edTitle, language.code)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = edDesc,
                                style = MaterialTheme.typography.bodyMedium.copy(color = WarmCream)
                                    .crispFor(edDesc, language.code)
                            )
                        }
                    }
                }
            }
            items(articles, key = { it.id }) { article ->
                ArticleSummaryCard(
                    article = article,
                    category = categoryMap[article.categorySlug],
                    language = language,
                    lowDataMode = lowDataMode,
                    onClick = { onOpenArticle(article.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorProfileScreen(
    author: UserEntity?,
    authorArticles: List<ArticleEntity>,
    categories: List<CategoryEntity>,
    followersCount: Int,
    isFollowing: Boolean,
    language: AppLanguage,
    lowDataMode: Boolean,
    onToggleFollow: () -> Unit,
    onOpenArticle: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = AppStrings.get(language)
    val categoryMap = categories.associateBy { it.slug }
    val approved = authorArticles.filter { it.status == ArticleStatus.APPROVED.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(author?.fullName ?: "Author") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicNavyDeep,
                    titleContentColor = WarmCream,
                    navigationIconContentColor = WarmCream
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            author?.let { user ->
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = IslamicNavyDeep),
                        border = BorderStroke(1.dp, IslamicGold)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(shape = CircleShape, color = IslamicGold, modifier = Modifier.size(64.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        user.fullName.take(1).uppercase(),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            color = IslamicNavyDeep,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                user.fullName,
                                style = MaterialTheme.typography.titleLarge.copy(color = WarmCream)
                                    .crispFor(user.fullName, language.code)
                            )
                            Text(
                                "@${user.username} • ${user.userId}",
                                style = MaterialTheme.typography.labelMedium.copy(color = IslamicGoldLight)
                            )
                            if (user.bio.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    user.bio,
                                    style = MaterialTheme.typography.bodySmall.copy(color = WarmCream.copy(alpha = 0.94f))
                                        .crispFor(user.bio, language.code)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    text = "${approved.size} ${if (language == AppLanguage.BN) "প্রবন্ধ" else "Articles"}",
                                    style = MaterialTheme.typography.labelLarge.copy(color = IslamicGold)
                                )
                                Text(
                                    text = "$followersCount ${if (language == AppLanguage.BN) "অনুসারী" else "Followers"}",
                                    style = MaterialTheme.typography.labelLarge.copy(color = IslamicGold)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onToggleFollow,
                                border = BorderStroke(1.dp, IslamicGold)
                            ) {
                                Text(
                                    text = if (isFollowing) strings.following else strings.follow,
                                    color = IslamicGold
                                )
                            }
                        }
                    }
                }
            }
            items(approved, key = { it.id }) { article ->
                ArticleSummaryCard(
                    article = article,
                    category = categoryMap[article.categorySlug],
                    language = language,
                    lowDataMode = lowDataMode,
                    onClick = { onOpenArticle(article.id) }
                )
            }
        }
    }
}
