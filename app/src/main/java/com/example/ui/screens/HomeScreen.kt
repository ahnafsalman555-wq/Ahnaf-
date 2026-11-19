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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.ArticleEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.EditionEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.ArticleCoverArt
import com.example.ui.components.ArticleSummaryCard
import com.example.ui.components.formatPublicationDate
import com.example.ui.theme.ArabicQuranFontFamily
import com.example.ui.theme.DeveloperCreditCard
import com.example.ui.theme.GoldCalligraphyDivider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGeometricWatermark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmCream
import com.example.ui.theme.crispFor
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    language: AppLanguage,
    currentUser: UserEntity?,
    approvedArticles: List<ArticleEntity>,
    categories: List<CategoryEntity>,
    editions: List<EditionEntity>,
    readingHistory: List<ReadingProgressEntity>,
    authors: List<UserEntity>,
    lowDataMode: Boolean,
    onOpenArticle: (String) -> Unit,
    onOpenQuranStudio: () -> Unit,
    onOpenEdition: (String) -> Unit,
    onOpenCategory: (String) -> Unit,
    onOpenAuthor: (String) -> Unit
) {
    val strings = AppStrings.get(language)
    val categoryMap = categories.associateBy { it.slug }

    val featured = approvedArticles.filter { it.isFeatured }.ifEmpty { approvedArticles.take(3) }
    val latest = approvedArticles.sortedByDescending { it.publishedAt ?: it.createdAt }
    val trending = approvedArticles.sortedByDescending { it.viewsCount + it.likesCount * 3 }
    val editorsChoice = approvedArticles.filter { it.isEditorsChoice }
    val mostRead = approvedArticles.sortedByDescending { it.viewsCount }
    val mostLiked = approvedArticles.sortedByDescending { it.likesCount }

    val continueItem = readingHistory.firstOrNull()?.let { prog ->
        approvedArticles.firstOrNull { it.id == prog.articleId }?.let { it to prog }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Islamic Masthead Greeting
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicNavyDeep),
                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.55f))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IslamicGeometricWatermark(
                        modifier = Modifier.matchParentSize(),
                        tint = IslamicGold.copy(alpha = 0.12f),
                        stepDp = 56.dp
                    )
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = strings.bismillah,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = ArabicQuranFontFamily,
                                color = IslamicGoldLight,
                                letterSpacing = 0.sp
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        val greetingText = if (currentUser != null) {
                            "${strings.salamGreeting}, ${currentUser.fullName}"
                        } else {
                            strings.salamGreeting
                        }
                        Text(
                            text = greetingText,
                            style = MaterialTheme.typography.titleLarge.copy(color = WarmCream).crispFor(greetingText, language.code)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = strings.appSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = WarmCream.copy(alpha = 0.94f)).crispFor(strings.appSubtitle, language.code)
                        )
                    }
                }
            }
        }

        // 2. Quranic Recitation Studio Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenQuranStudio)
                    .testTag("home_quran_recitation_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicEmeraldDeep),
                border = BorderStroke(1.dp, IslamicGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = IslamicGold.copy(alpha = 0.2f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الْقُرْآنُ الْكَرِيمُ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = ArabicQuranFontFamily,
                                color = IslamicGoldLight,
                                letterSpacing = 0.sp
                            )
                        )
                        Text(
                            text = strings.quranRecitationCardTitle,
                            style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                                .crispFor(strings.quranRecitationCardTitle, language.code)
                        )
                        Text(
                            text = strings.quranRecitationCardSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = WarmCream.copy(alpha = 0.94f))
                                .crispFor(strings.quranRecitationCardSubtitle, language.code),
                            maxLines = 2
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = IslamicGold
                    ) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "Open Quran Recitation",
                            tint = IslamicNavyDeep,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // 3. Continue Reading
        if (continueItem != null) {
            val (article, progress) = continueItem
            item {
                SectionHeader(title = strings.continueReading)
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenArticle(article.id) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AutoStories, contentDescription = null, tint = IslamicGold)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ).crispFor(article.title, article.language),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${progress.progressPercent}%",
                                style = MaterialTheme.typography.labelMedium.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = IslamicGold,
                            trackColor = IslamicGold.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        // 4. Featured Articles Carousel
        if (featured.isNotEmpty()) {
            item {
                SectionHeader(title = strings.featuredArticles)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(featured, key = { it.id }) { article ->
                        FeaturedArticleCard(
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

        // 5. Monthly & Weekly Editions
        if (editions.isNotEmpty()) {
            item {
                SectionHeader(title = strings.monthlyAndWeeklyEditions)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(editions, key = { it.id }) { edition ->
                        Card(
                            modifier = Modifier
                                .width(228.dp)
                                .clickable { onOpenEdition(edition.id) }
                                .testTag("edition_card_${edition.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = IslamicNavy),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.6f))
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                IslamicGeometricWatermark(
                                    modifier = Modifier.matchParentSize(),
                                    tint = IslamicGold.copy(alpha = 0.14f),
                                    stepDp = 44.dp
                                )
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Surface(
                                        color = IslamicGold,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = edition.editionType,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = IslamicNavyDeep,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    val editionTitle = if (language == AppLanguage.BN) edition.titleBn else edition.titleEn
                                    val editionDesc = if (language == AppLanguage.BN) edition.descriptionBn else edition.descriptionEn
                                    Text(
                                        text = editionTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(color = WarmCream, fontWeight = FontWeight.Bold)
                                            .crispFor(editionTitle, language.code),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = editionDesc,
                                        style = MaterialTheme.typography.bodySmall.copy(color = WarmCream.copy(alpha = 0.94f))
                                            .crispFor(editionDesc, language.code),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Trending Categories
        if (categories.isNotEmpty()) {
            item {
                SectionHeader(title = strings.trendingCategories)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories, key = { it.slug }) { cat ->
                        FilterChip(
                            selected = false,
                            onClick = { onOpenCategory(cat.slug) },
                            label = {
                                val catName = if (language == AppLanguage.BN) cat.nameBn else cat.nameEn
                                Text(
                                    text = catName,
                                    style = MaterialTheme.typography.labelLarge.crispFor(catName, language.code)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = false,
                                borderColor = IslamicGold.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        // 7. Curated Home Feed Sections: নতুন লেখা, ট্রেন্ডিং লেখা, এডিটরস চয়েস, সর্বাধিক পঠিত, সর্বাধিক পছন্দের লেখা
        item { SectionHeader(title = strings.latestArticles) }
        items(latest.take(3), key = { "latest_${it.id}" }) { article ->
            ArticleSummaryCard(
                article = article,
                category = categoryMap[article.categorySlug],
                language = language,
                lowDataMode = lowDataMode,
                onClick = { onOpenArticle(article.id) }
            )
        }

        item { SectionHeader(title = strings.trendingArticles) }
        items(trending.take(2), key = { "trending_${it.id}" }) { article ->
            ArticleSummaryCard(
                article = article,
                category = categoryMap[article.categorySlug],
                language = language,
                lowDataMode = lowDataMode,
                onClick = { onOpenArticle(article.id) }
            )
        }

        if (editorsChoice.isNotEmpty()) {
            item { SectionHeader(title = strings.editorsChoice) }
            items(editorsChoice.take(2), key = { "editors_${it.id}" }) { article ->
                ArticleSummaryCard(
                    article = article,
                    category = categoryMap[article.categorySlug],
                    language = language,
                    lowDataMode = lowDataMode,
                    onClick = { onOpenArticle(article.id) }
                )
            }
        }

        item { SectionHeader(title = strings.mostReadArticles) }
        items(mostRead.take(2), key = { "read_${it.id}" }) { article ->
            ArticleSummaryCard(
                article = article,
                category = categoryMap[article.categorySlug],
                language = language,
                lowDataMode = lowDataMode,
                onClick = { onOpenArticle(article.id) }
            )
        }

        item { SectionHeader(title = strings.mostLikedArticles) }
        items(mostLiked.take(2), key = { "liked_${it.id}" }) { article ->
            ArticleSummaryCard(
                article = article,
                category = categoryMap[article.categorySlug],
                language = language,
                lowDataMode = lowDataMode,
                onClick = { onOpenArticle(article.id) }
            )
        }

        // 8. Popular Authors
        if (authors.isNotEmpty()) {
            item {
                SectionHeader(title = strings.popularAuthors)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(authors, key = { it.uid }) { author ->
                        Card(
                            modifier = Modifier
                                .width(172.dp)
                                .clickable { onOpenAuthor(author.uid) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = IslamicNavy,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = author.fullName.take(1).uppercase(),
                                            color = IslamicGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = author.fullName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        .crispFor(author.fullName, language.code),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "@${author.username} • ${author.userId}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 9. Developer & Ownership Credit Footer
        item {
            Spacer(Modifier.height(6.dp))
            DeveloperCreditCard(isBengali = language == AppLanguage.BN)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Star, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(4.dp))
        GoldCalligraphyDivider()
    }
}

@Composable
private fun FeaturedArticleCard(
    article: ArticleEntity,
    category: CategoryEntity?,
    language: AppLanguage,
    lowDataMode: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(278.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.6f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            ) {
                ArticleCoverArt(
                    article = article,
                    lowDataMode = lowDataMode,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(IslamicNavyDeep.copy(alpha = 0.04f), IslamicNavyDeep.copy(alpha = 0.82f))
                            )
                        )
                )
                Surface(
                    color = IslamicGold,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    val categoryLabel = category?.let { if (language == AppLanguage.BN) it.nameBn else it.nameEn } ?: article.categorySlug
                    Text(
                        text = categoryLabel,
                        style = MaterialTheme.typography.labelSmall.copy(color = IslamicNavyDeep, fontWeight = FontWeight.Bold)
                            .crispFor(categoryLabel, language.code),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = formatPublicationDate(article.publishedAt, language),
                    style = MaterialTheme.typography.labelSmall.copy(color = WarmCream),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                )
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ).crispFor(article.title, article.language),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val authorLine = if (language == AppLanguage.BN) "লেখক: ${article.authorName}" else "By ${article.authorName}"
                    Text(
                        text = authorLine,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        ).crispFor(authorLine, article.language),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
