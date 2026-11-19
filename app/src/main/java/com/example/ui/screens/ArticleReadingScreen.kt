package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ArticleEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.CommentEntity
import com.example.data.local.ReaderThemeMode
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.ArticleCoverArt
import com.example.ui.components.formatPublicationDate
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldCalligraphyDivider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextOnDarkPrimary
import com.example.ui.theme.TextOnLightPrimary
import com.example.ui.theme.WarmCream
import com.example.ui.theme.arabicQuranTextStyle
import com.example.ui.theme.crispFor
import com.example.ui.theme.smartFontFamily
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.roundToInt

sealed class BodyBlock {
    data class Heading(val text: String, val level: Int) : BodyBlock()
    data class Quote(val text: String) : BodyBlock()
    data class ArabicBlock(val text: String) : BodyBlock()
    data class BulletItem(val text: String) : BodyBlock()
    data class Paragraph(val text: String) : BodyBlock()
}

fun parseArticleBlocks(rawBody: String): List<BodyBlock> {
    val result = mutableListOf<BodyBlock>()
    val lines = rawBody.lines()
    var i = 0
    while (i < lines.size) {
        val line = lines[i].trim()
        when {
            line.isBlank() -> i++
            line.startsWith("[ARABIC]") -> {
                val sb = StringBuilder()
                val sameLineContent = line.removePrefix("[ARABIC]").removeSuffix("[/ARABIC]").trim()
                if (sameLineContent.isNotBlank()) sb.append(sameLineContent)
                if (!line.contains("[/ARABIC]")) {
                    i++
                    while (i < lines.size && !lines[i].contains("[/ARABIC]")) {
                        if (sb.isNotEmpty()) sb.append("\n")
                        sb.append(lines[i].trim())
                        i++
                    }
                }
                result.add(BodyBlock.ArabicBlock(sb.toString().trim()))
                i++
            }
            line.startsWith("## ") -> {
                result.add(BodyBlock.Heading(line.removePrefix("## ").trim(), 2))
                i++
            }
            line.startsWith("# ") -> {
                result.add(BodyBlock.Heading(line.removePrefix("# ").trim(), 1))
                i++
            }
            line.startsWith("> ") -> {
                result.add(BodyBlock.Quote(line.removePrefix("> ").trim()))
                i++
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                result.add(BodyBlock.BulletItem(line.drop(2).trim()))
                i++
            }
            else -> {
                result.add(BodyBlock.Paragraph(line))
                i++
            }
        }
    }
    return result
}

@Composable
fun RichArticleBodyContent(
    body: String,
    languageCode: String,
    fontSizeSp: Float,
    lineSpacingMultiplier: Float,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onBookmarkParagraph: ((Int, String) -> Unit)? = null
) {
    val blocks = remember(body) { parseArticleBlocks(body) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        blocks.forEachIndexed { idx, block ->
            RenderBodyBlock(
                index = idx,
                block = block,
                languageCode = languageCode,
                fontSizeSp = fontSizeSp,
                lineSpacingMultiplier = lineSpacingMultiplier,
                textColor = textColor,
                onBookmarkParagraph = onBookmarkParagraph
            )
        }
    }
}

@Composable
fun RenderBodyBlock(
    index: Int,
    block: BodyBlock,
    languageCode: String,
    fontSizeSp: Float,
    lineSpacingMultiplier: Float,
    textColor: Color,
    onBookmarkParagraph: ((Int, String) -> Unit)?
) {
    val lineHeight = (fontSizeSp * lineSpacingMultiplier).sp

    when (block) {
        is BodyBlock.ArabicBlock -> {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = IslamicGold.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = block.text,
                    style = arabicQuranTextStyle(fontSizeSp + 6f, (fontSizeSp + 6f) * 1.75f).copy(color = textColor),
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                )
            }
        }
        is BodyBlock.Heading -> {
            Text(
                text = block.text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = (fontSizeSp + if (block.level == 1) 5f else 3f).sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold
                ).crispFor(block.text, languageCode)
            )
        }
        is BodyBlock.Quote -> {
            Surface(
                color = IslamicNavy.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = block.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSizeSp.sp,
                        lineHeight = lineHeight,
                        fontStyle = FontStyle.Italic,
                        color = textColor
                    ).crispFor(block.text, languageCode),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
        is BodyBlock.BulletItem -> {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text("✦", color = IslamicGold, modifier = Modifier.padding(top = 2.dp, end = 8.dp))
                FormattedInlineText(
                    raw = block.text,
                    fontFamily = smartFontFamily(block.text, languageCode),
                    fontSizeSp = fontSizeSp,
                    lineHeight = lineHeight,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        is BodyBlock.Paragraph -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = onBookmarkParagraph != null) {
                        onBookmarkParagraph?.invoke(index, block.text)
                    },
                verticalAlignment = Alignment.Top
            ) {
                FormattedInlineText(
                    raw = block.text,
                    fontFamily = smartFontFamily(block.text, languageCode),
                    fontSizeSp = fontSizeSp,
                    lineHeight = lineHeight,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FormattedInlineText(
    raw: String,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    fontSizeSp: Float,
    lineHeight: androidx.compose.ui.unit.TextUnit,
    color: Color,
    modifier: Modifier = Modifier
) {
    val annotated = remember(raw) {
        buildAnnotatedString {
            var idx = 0
            while (idx < raw.length) {
                if (raw.startsWith("**", idx)) {
                    val end = raw.indexOf("**", idx + 2)
                    if (end != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(raw.substring(idx + 2, end))
                        }
                        idx = end + 2
                        continue
                    }
                }
                if (raw[idx] == '*' && !raw.startsWith("**", idx)) {
                    val end = raw.indexOf('*', idx + 1)
                    if (end != -1) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(raw.substring(idx + 1, end))
                        }
                        idx = end + 1
                        continue
                    }
                }
                append(raw[idx])
                idx++
            }
        }
    }
    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = fontFamily,
            fontSize = fontSizeSp.sp,
            lineHeight = lineHeight,
            letterSpacing = 0.sp,
            color = color
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleReadingScreen(
    article: ArticleEntity?,
    category: CategoryEntity?,
    language: AppLanguage,
    currentUser: UserEntity?,
    isLiked: Boolean,
    isSaved: Boolean,
    isFollowingAuthor: Boolean,
    comments: List<CommentEntity>,
    fontSizeSp: Float,
    lineSpacing: Float,
    readerThemeMode: ReaderThemeMode,
    lowDataMode: Boolean,
    initialProgress: ReadingProgressEntity?,
    onRecordView: () -> Unit,
    onSaveProgress: (Int, Int) -> Unit,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleOffline: (Boolean) -> Unit,
    onToggleFollow: () -> Unit,
    onBookmarkParagraph: (Int, String) -> Unit,
    onAddPersonalNote: (String) -> Unit,
    onPostComment: (String?, String) -> Unit,
    onReportArticle: (String) -> Unit,
    onReportComment: (CommentEntity, String) -> Unit,
    onUpdateReaderSettings: (Float, Float, ReaderThemeMode) -> Unit,
    onOpenAuthor: (String) -> Unit,
    onMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = AppStrings.get(language)
    val context = LocalContext.current

    if (article == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (language == AppLanguage.BN) "প্রবন্ধটি পাওয়া যায়নি" else "Article not found")
        }
        return
    }

    val blocks = remember(article.body) { parseArticleBlocks(article.body) }
    val listState = rememberLazyListState()
    var fullScreen by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var noteInput by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }
    var commentInput by remember { mutableStateOf("") }
    var replyTargetId by remember { mutableStateOf<String?>(null) }
    var readPercent by remember { mutableStateOf(initialProgress?.progressPercent ?: 0) }

    LaunchedEffect(article.id) {
        onRecordView()
    }

    LaunchedEffect(initialProgress?.paragraphIndex) {
        val target = initialProgress?.paragraphIndex ?: 0
        if (target > 0) {
            listState.scrollToItem((target + 1).coerceAtMost(blocks.size + 1))
        }
    }

    LaunchedEffect(listState, blocks.size) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { visibleIdx ->
                val blockIdx = (visibleIdx - 1).coerceAtLeast(0)
                val pct = if (blocks.isEmpty()) 100 else (((blockIdx + 1).toFloat() / blocks.size) * 100).roundToInt().coerceIn(5, 100)
                readPercent = pct
                onSaveProgress(blockIdx, pct)
            }
    }

    DisposableEffect(article.id) {
        onDispose {
            val blockIdx = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
            onSaveProgress(blockIdx, readPercent)
        }
    }

    val (bgColor, cardColor, bodyTextColor) = when (readerThemeMode) {
        ReaderThemeMode.CREAM -> Triple(WarmCream, PureWhite, TextOnLightPrimary)
        ReaderThemeMode.LIGHT -> Triple(PureWhite, Color(0xFFF6F7F9), TextOnLightPrimary)
        ReaderThemeMode.DARK -> Triple(DarkBackground, DarkSurface, TextOnDarkPrimary)
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            if (!fullScreen) {
                TopAppBar(
                    title = {
                        Column {
                            val catName = category?.let { if (language == AppLanguage.BN) it.nameBn else it.nameEn } ?: article.categorySlug
                            Text(
                                text = catName,
                                style = MaterialTheme.typography.labelMedium.copy(color = IslamicGold).crispFor(catName, language.code)
                            )
                            Text(
                                text = "$readPercent% • ${article.readingTimeMinutes} ${strings.minRead}",
                                style = MaterialTheme.typography.labelSmall.copy(color = WarmCream.copy(alpha = 0.94f))
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSettingsSheet = true }, modifier = Modifier.testTag("reader_settings_button")) {
                            Icon(Icons.Outlined.TextFields, contentDescription = strings.readingSettings, tint = WarmCream)
                        }
                        IconButton(onClick = { fullScreen = true }) {
                            Icon(Icons.Outlined.Fullscreen, contentDescription = strings.fullScreenMode, tint = WarmCream)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = IslamicNavyDeep,
                        titleContentColor = WarmCream,
                        navigationIconContentColor = WarmCream
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(bgColor)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LinearProgressIndicator(
                    progress = { readPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = IslamicGold,
                    trackColor = IslamicGold.copy(alpha = 0.2f)
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // UNIFIED ARTICLE HEADER: Title + Highlighted Author + Date + Cover directly before body
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            val catLabel = category?.let { if (language == AppLanguage.BN) it.nameBn else it.nameEn } ?: article.categorySlug
                            Surface(
                                color = IslamicGold.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = catLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                                        .crispFor(catLabel, language.code),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = bodyTextColor
                                ).crispFor(article.title, article.language),
                                modifier = Modifier.testTag("reading_article_title")
                            )
                            if (article.subtitle.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = article.subtitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = bodyTextColor.copy(alpha = 0.90f)
                                    ).crispFor(article.subtitle, article.language)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                color = IslamicNavyDeep,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.7f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onOpenAuthor(article.authorUserId) }
                                    ) {
                                        val authorText = "${strings.authorPrefix} ${article.authorName}"
                                        Text(
                                            text = authorText,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = IslamicGold,
                                                fontWeight = FontWeight.Bold
                                            ).crispFor(authorText, article.language)
                                        )
                                        Text(
                                            text = "${strings.publishedOn} ${formatPublicationDate(article.publishedAt, language)} • ${article.readingTimeMinutes} ${strings.minRead}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = WarmCream.copy(alpha = 0.94f))
                                        )
                                    }
                                    if (currentUser?.uid != article.authorUserId) {
                                        OutlinedButton(
                                            onClick = onToggleFollow,
                                            border = BorderStroke(1.dp, IslamicGold),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isFollowingAuthor) strings.following else strings.follow,
                                                color = IslamicGold,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            ArticleCoverArt(
                                article = article,
                                lowDataMode = lowDataMode,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .clip(RoundedCornerShape(18.dp))
                            )
                            Spacer(Modifier.height(14.dp))
                            GoldCalligraphyDivider()
                        }
                    }

                    // MAIN ARTICLE BODY PARAGRAPHS (Tap paragraph to bookmark)
                    itemsIndexed(blocks) { idx, block ->
                        Column {
                            RenderBodyBlock(
                                index = idx,
                                block = block,
                                languageCode = article.language,
                                fontSizeSp = fontSizeSp,
                                lineSpacingMultiplier = lineSpacing,
                                textColor = bodyTextColor,
                                onBookmarkParagraph = { pIdx, excerpt ->
                                    if (currentUser != null) {
                                        onBookmarkParagraph(pIdx, excerpt)
                                    }
                                }
                            )
                        }
                    }

                    // ENGAGEMENT & LIBRARY ACTION BAR
                    item {
                        Spacer(Modifier.height(8.dp))
                        GoldCalligraphyDivider()
                        Spacer(Modifier.height(12.dp))
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Visibility, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("${article.viewsCount} ${strings.viewsLabel}", style = MaterialTheme.typography.labelMedium, color = bodyTextColor)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = onToggleLike, modifier = Modifier.testTag("article_like_button")) {
                                            Icon(
                                                if (isLiked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = strings.likesLabel,
                                                tint = if (isLiked) Color(0xFFD32F2F) else IslamicGold
                                            )
                                        }
                                        Text("${article.likesCount}", style = MaterialTheme.typography.labelLarge, color = bodyTextColor)
                                        Spacer(Modifier.width(8.dp))
                                        IconButton(onClick = onToggleSave, modifier = Modifier.testTag("article_save_button")) {
                                            Icon(
                                                if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                                contentDescription = "Save Article",
                                                tint = IslamicGold
                                            )
                                        }
                                        IconButton(onClick = { onToggleOffline(!article.isDownloadedOffline) }) {
                                            Icon(
                                                if (article.isDownloadedOffline) Icons.Rounded.CloudDone else Icons.Outlined.CloudDownload,
                                                contentDescription = strings.downloadOffline,
                                                tint = if (article.isDownloadedOffline) IslamicEmerald else IslamicGold
                                            )
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showNoteDialog = true },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Outlined.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(strings.addPersonalNote, maxLines = 1)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cm.setPrimaryClip(ClipData.newPlainText(article.title, "${article.title}\n${article.authorName}\n\n${article.body}"))
                                            onMessage(if (language == AppLanguage.BN) "লেখা কপি করা হয়েছে" else "Article text copied")
                                        }
                                    ) {
                                        Icon(Icons.Outlined.ContentCopy, contentDescription = strings.copyText, modifier = Modifier.size(16.dp))
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, article.title)
                                                putExtra(Intent.EXTRA_TEXT, "${article.title}\n— ${article.authorName}\n\nMonthly Rahabar Magazine")
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, strings.shareArticle))
                                        }
                                    ) {
                                        Icon(Icons.Outlined.Share, contentDescription = strings.shareArticle, modifier = Modifier.size(16.dp))
                                    }
                                    OutlinedButton(onClick = { showReportDialog = true }) {
                                        Icon(Icons.Outlined.Flag, contentDescription = strings.reportArticle, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // COMMENTS & REPLIES
                    item {
                        Text(
                            text = "${strings.commentsTitle} (${comments.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = bodyTextColor)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            label = {
                                Text(
                                    if (replyTargetId != null) "${strings.reply}..." else strings.writeCommentHint
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onPostComment(replyTargetId, commentInput)
                                    commentInput = ""
                                    replyTargetId = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicNavy, contentColor = WarmCream)
                            ) {
                                Text(strings.postComment)
                            }
                            if (replyTargetId != null) {
                                TextButton(onClick = { replyTargetId = null }) {
                                    Text(if (language == AppLanguage.BN) "বাতিল" else "Cancel")
                                }
                            }
                        }
                    }

                    itemsIndexed(comments, key = { _, c -> c.id }) { _, c ->
                        val isReply = c.parentCommentId != null
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = if (isReply) 22.dp else 0.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${c.authorName} (@${c.authorUsername})",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = IslamicGold)
                                            .crispFor(c.authorName, article.language)
                                    )
                                    Row {
                                        TextButton(onClick = { replyTargetId = c.id }) {
                                            Text(strings.reply, style = MaterialTheme.typography.labelSmall)
                                        }
                                        IconButton(onClick = { onReportComment(c, "Reported comment") }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Outlined.Flag, contentDescription = "Report comment", modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                Text(
                                    c.content,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = bodyTextColor)
                                        .crispFor(c.content, article.language)
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(32.dp)) }
                }
            }

            if (fullScreen) {
                Surface(
                    onClick = { fullScreen = false },
                    shape = CircleShape,
                    color = IslamicNavyDeep.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, IslamicGold),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Outlined.FullscreenExit,
                        contentDescription = "Exit Fullscreen",
                        tint = IslamicGold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(onDismissRequest = { showSettingsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(strings.readingSettings, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${strings.fontSize}: ${fontSizeSp.roundToInt()}sp")
                Slider(
                    value = fontSizeSp,
                    onValueChange = { onUpdateReaderSettings(it, lineSpacing, readerThemeMode) },
                    valueRange = 14f..26f
                )
                Text("${strings.lineSpacing}: ${String.format("%.2f", lineSpacing)}")
                Slider(
                    value = lineSpacing,
                    onValueChange = { onUpdateReaderSettings(fontSizeSp, it, readerThemeMode) },
                    valueRange = 1.3f..2.1f
                )
                Text(strings.readerTheme)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReaderThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = readerThemeMode == mode,
                            onClick = { onUpdateReaderSettings(fontSizeSp, lineSpacing, mode) },
                            label = { Text(mode.name) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text(strings.addPersonalNote) },
            text = {
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text(if (language == AppLanguage.BN) "আপনার ব্যক্তিগত নোট" else "Your private reflection") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAddPersonalNote(noteInput)
                    noteInput = ""
                    showNoteDialog = false
                }) {
                    Text(if (language == AppLanguage.BN) "সংরক্ষণ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text(if (language == AppLanguage.BN) "বাতিল" else "Cancel")
                }
            }
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text(strings.reportArticle) },
            text = {
                OutlinedTextField(
                    value = reportReason,
                    onValueChange = { reportReason = it },
                    label = { Text(if (language == AppLanguage.BN) "রিপোর্টের কারণ লিখুন" else "Reason for reporting") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onReportArticle(reportReason)
                    reportReason = ""
                    showReportDialog = false
                }) {
                    Text(if (language == AppLanguage.BN) "জমা দিন" else "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text(if (language == AppLanguage.BN) "বাতিল" else "Cancel")
                }
            }
        )
    }
}
