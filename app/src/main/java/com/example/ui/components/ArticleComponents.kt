package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.ArticleEntity
import com.example.data.local.ArticleStatus
import com.example.data.local.CategoryEntity
import com.example.i18n.AppLanguage
import com.example.ui.theme.AdorsholipiFontFamily
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGeometricWatermark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.IslamicNavyContainer
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.RahabarLogoBadge
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.StatusDraft
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.WarmCream
import com.example.ui.theme.containsBengali
import com.example.ui.theme.smartFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatPublicationDate(timestamp: Long?, language: AppLanguage): String {
    val ts = timestamp ?: System.currentTimeMillis()
    val locale = if (language == AppLanguage.BN) Locale.forLanguageTag("bn-BD") else Locale.ENGLISH
    return SimpleDateFormat("dd MMM yyyy", locale).format(Date(ts))
}

fun coverGradientForIndex(index: Int): Brush = when (index % 4) {
    0 -> Brush.linearGradient(listOf(IslamicNavyDeep, IslamicNavyContainer, IslamicGold.copy(alpha = 0.7f)))
    1 -> Brush.linearGradient(listOf(IslamicEmeraldDeep, IslamicEmerald, IslamicGold.copy(alpha = 0.65f)))
    2 -> Brush.linearGradient(listOf(Color(0xFF2B1E16), Color(0xFF4A3520), IslamicGold))
    else -> Brush.linearGradient(listOf(IslamicNavy, Color(0xFF234B5E), IslamicGoldLight))
}

@Composable
fun RahabarTopHeader(
    language: AppLanguage,
    unreadNotifications: Int,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    Surface(
        color = IslamicNavyDeep,
        contentColor = WarmCream,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RahabarLogoBadge(size = 42.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (language == AppLanguage.BN) "মাসিক রাহাবার" else "MONTHLY RAHABAR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = if (language == AppLanguage.BN) AdorsholipiFontFamily else MaterialTheme.typography.titleMedium.fontFamily,
                        color = IslamicGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (language == AppLanguage.BN) "ইসলামিক সাহিত্য ম্যাগাজিন" else "Islamic Literary Magazine",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = if (language == AppLanguage.BN) AdorsholipiFontFamily else MaterialTheme.typography.labelSmall.fontFamily,
                        color = WarmCream.copy(alpha = 0.92f),
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("header_search_button")
            ) {
                Icon(Icons.Outlined.Search, contentDescription = "Search", tint = WarmCream)
            }
            Surface(
                onClick = onLanguageToggle,
                shape = RoundedCornerShape(20.dp),
                color = IslamicGold.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.75f)),
                modifier = Modifier.testTag("header_language_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Language,
                        contentDescription = "Switch Language",
                        tint = IslamicGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (language == AppLanguage.BN) "বাং" else "EN",
                        style = MaterialTheme.typography.labelMedium.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.testTag("header_notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotifications > 0) {
                            Badge(containerColor = IslamicGold, contentColor = IslamicNavyDeep) {
                                Text(if (unreadNotifications > 9) "9+" else unreadNotifications.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = WarmCream)
                }
            }
        }
    }
}

@Composable
fun ArticleCoverArt(
    article: ArticleEntity,
    lowDataMode: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(coverGradientForIndex(article.coverThemeIndex))
    ) {
        if (!article.coverUri.isNullOrBlank() && !lowDataMode) {
            AsyncImage(
                model = Uri.parse(article.coverUri),
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            IslamicGeometricWatermark(
                modifier = Modifier.fillMaxSize(),
                tint = IslamicGold.copy(alpha = 0.24f),
                stepDp = 42.dp
            )
            Icon(
                imageVector = Icons.Outlined.MenuBook,
                contentDescription = null,
                tint = IslamicGold.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(34.dp)
            )
        }
    }
}

@Composable
fun ArticleSummaryCard(
    article: ArticleEntity,
    category: CategoryEntity?,
    language: AppLanguage,
    lowDataMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = category?.let { if (language == AppLanguage.BN) it.nameBn else it.nameEn } ?: article.categorySlug
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("article_card_${article.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArticleCoverArt(
                article = article,
                lowDataMode = lowDataMode,
                modifier = Modifier
                    .size(width = 96.dp, height = 108.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = IslamicGold.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = smartFontFamily(categoryName, language.code),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                    if (article.isEditorsChoice) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            contentDescription = "Editor's Choice",
                            tint = IslamicGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (article.isDownloadedOffline) {
                        Icon(
                            Icons.Rounded.CloudDone,
                            contentDescription = "Offline Available",
                            tint = IslamicEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = smartFontFamily(article.title, article.language),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (containsBengali(article.title)) 0.sp else MaterialTheme.typography.titleMedium.letterSpacing
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                val authorLine = if (language == AppLanguage.BN) "লেখক: ${article.authorName}" else "By ${article.authorName}"
                Text(
                    text = authorLine,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = smartFontFamily(authorLine, article.language),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(3.dp))
                        Text("${article.viewsCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(3.dp))
                        Text("${article.likesCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = "${article.readingTimeMinutes} ${if (language == AppLanguage.BN) "মি." else "min"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: ArticleStatus, language: AppLanguage) {
    val (label, bg) = when (status) {
        ArticleStatus.APPROVED -> (if (language == AppLanguage.BN) "অনুমোদিত" else "Approved") to StatusApproved
        ArticleStatus.PENDING -> (if (language == AppLanguage.BN) "অপেক্ষমাণ" else "Pending") to StatusPending
        ArticleStatus.REJECTED -> (if (language == AppLanguage.BN) "প্রত্যাখ্যাত" else "Rejected") to StatusRejected
        ArticleStatus.DRAFT -> (if (language == AppLanguage.BN) "খসড়া" else "Draft") to StatusDraft
    }
    Surface(
        color = bg.copy(alpha = 0.14f),
        shape = CircleShape,
        border = BorderStroke(1.dp, bg.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = bg, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
