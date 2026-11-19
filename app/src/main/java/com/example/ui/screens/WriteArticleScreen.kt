package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.ArticleEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.theme.GoldCalligraphyDivider
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.WarmCream
import com.example.ui.theme.smartFontFamily

@Composable
fun WriteArticleScreen(
    language: AppLanguage,
    currentUser: UserEntity?,
    categories: List<CategoryEntity>,
    editingArticle: ArticleEntity?,
    onRequireAuth: () -> Unit,
    onSaveOrSubmit: (
        existingId: String?,
        title: String,
        authorName: String,
        body: String,
        subtitle: String,
        publicationDescription: String,
        categorySlug: String,
        articleLanguage: String,
        tags: String,
        coverUri: String?,
        coverThemeIndex: Int,
        submitForReview: Boolean
    ) -> Unit
) {
    val strings = AppStrings.get(language)

    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, IslamicGold)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "প্রবন্ধ লিখতে লগইন করুন" else "Sign In to Publish Articles",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (language == AppLanguage.BN) {
                            "লেখা জমা দেওয়া, খসড়া সংরক্ষণ এবং আপনার ইউনিক RHM আইডি পাওয়ার জন্য অ্যাকাউন্টে প্রবেশ করুন।"
                        } else {
                            "Sign in or register to submit articles, save drafts, and receive your permanent RHM author ID."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onRequireAuth,
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicNavy, contentColor = WarmCream)
                    ) {
                        Text(strings.signInButton)
                    }
                }
            }
        }
        return
    }

    var title by remember(editingArticle?.id) { mutableStateOf(editingArticle?.title ?: "") }
    var authorName by remember(editingArticle?.id, currentUser.uid) {
        mutableStateOf(editingArticle?.authorName ?: currentUser.fullName)
    }
    var body by remember(editingArticle?.id) { mutableStateOf(editingArticle?.body ?: "") }
    var subtitle by remember(editingArticle?.id) { mutableStateOf(editingArticle?.subtitle ?: "") }
    var pubDesc by remember(editingArticle?.id) { mutableStateOf(editingArticle?.publicationDescription ?: "") }
    var selectedCategorySlug by remember(editingArticle?.id) {
        mutableStateOf(editingArticle?.categorySlug ?: categories.firstOrNull()?.slug ?: "islamic_articles")
    }
    var articleLanguage by remember(editingArticle?.id) {
        mutableStateOf(editingArticle?.language ?: language.code)
    }
    var tags by remember(editingArticle?.id) { mutableStateOf(editingArticle?.tags ?: "") }
    var coverUri by remember(editingArticle?.id) { mutableStateOf(editingArticle?.coverUri) }
    var coverThemeIndex by remember(editingArticle?.id) { mutableIntStateOf(editingArticle?.coverThemeIndex ?: 0) }
    var isPreview by remember { mutableStateOf(false) }

    LaunchedEffect(categories) {
        if (selectedCategorySlug.isBlank() && categories.isNotEmpty()) {
            selectedCategorySlug = categories.first().slug
        }
    }

    // Google Play Policy compliant zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coverUri = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = strings.writeArticleHeader,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedButton(
                onClick = { isPreview = !isPreview },
                border = BorderStroke(1.dp, IslamicGold)
            ) {
                Icon(Icons.Outlined.Preview, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (isPreview) strings.editMode else strings.previewMode)
            }
        }

        if (isPreview) {
            // Live Unified Reading Header Preview
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, IslamicGold)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = title.ifBlank { strings.fieldTitleHint },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = smartFontFamily(title, articleLanguage),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${strings.authorPrefix} ${authorName.ifBlank { currentUser.fullName }}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (subtitle.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(10.dp))
                    GoldCalligraphyDivider()
                    Spacer(Modifier.height(12.dp))
                    RichArticleBodyContent(
                        body = body.ifBlank { strings.fieldBodyHint },
                        languageCode = articleLanguage,
                        fontSizeSp = 16f,
                        lineSpacingMultiplier = 1.6f
                    )
                }
            }
        } else {
            // MANDATORY FIELD 1: Article Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(strings.fieldTitleLabel) },
                placeholder = { Text(strings.fieldTitleHint) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("write_title_input")
            )

            // MANDATORY FIELD 2: Author Name
            OutlinedTextField(
                value = authorName,
                onValueChange = { authorName = it },
                label = { Text(strings.fieldAuthorLabel) },
                placeholder = { Text(strings.fieldAuthorHint) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("write_author_input")
            )

            // Rich Text Formatting Toolbar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IslamicNavyDeep,
                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        IconButton(onClick = { body += "\n# শিরোনাম / Heading\n" }) {
                            Icon(Icons.Outlined.Title, contentDescription = "Heading", tint = WarmCream)
                        }
                    }
                    item {
                        IconButton(onClick = { body += "**গুরুত্বপূর্ণ কথা**" }) {
                            Icon(Icons.Outlined.FormatBold, contentDescription = "Bold", tint = WarmCream)
                        }
                    }
                    item {
                        IconButton(onClick = { body += "*বাঁকা অক্ষর*" }) {
                            Icon(Icons.Outlined.FormatItalic, contentDescription = "Italic", tint = WarmCream)
                        }
                    }
                    item {
                        IconButton(onClick = { body += "\n> উদ্ধৃতি বা গুরুত্বপূর্ণ বাণী\n" }) {
                            Icon(Icons.Outlined.FormatQuote, contentDescription = "Quote", tint = WarmCream)
                        }
                    }
                    item {
                        IconButton(onClick = { body += "\n- তালিকা আইটেম\n" }) {
                            Icon(Icons.Outlined.FormatListBulleted, contentDescription = "Bullet List", tint = WarmCream)
                        }
                    }
                    item {
                        IconButton(onClick = { body += "[তথ্যসূত্র](https://example.com)" }) {
                            Icon(Icons.Outlined.Link, contentDescription = "Hyperlink", tint = WarmCream)
                        }
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = {
                                body += "\n[ARABIC]\nبِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\n[/ARABIC]\n"
                            },
                            label = { Text(strings.insertArabicBlock, color = IslamicGold) }
                        )
                    }
                }
            }

            // MANDATORY FIELD 3: Main Article Rich Text Editor
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text(strings.fieldBodyLabel) },
                placeholder = { Text(strings.fieldBodyHint) },
                minLines = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("write_body_input")
            )

            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text(strings.subtitleOptional) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pubDesc,
                onValueChange = { pubDesc = it },
                label = { Text(strings.publicationDescOptional) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Article Language Selector
            Text(strings.selectLanguage, style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = articleLanguage == "bn",
                    onClick = { articleLanguage = "bn" },
                    label = { Text("বাংলা (AdorshoLipi)") }
                )
                FilterChip(
                    selected = articleLanguage == "en",
                    onClick = { articleLanguage = "en" },
                    label = { Text("English") }
                )
            }

            // Category Picker
            Text(strings.selectCategory, style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.slug }) { cat ->
                    FilterChip(
                        selected = selectedCategorySlug == cat.slug,
                        onClick = { selectedCategorySlug = cat.slug },
                        label = { Text(if (language == AppLanguage.BN) cat.nameBn else cat.nameEn) }
                    )
                }
            }

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text(strings.tagsLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Cover Image Picker (Zero-permission PickVisualMedia) + Islamic Cover Theme
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(Icons.Outlined.Image, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(strings.selectCoverImage)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (0..3).forEach { idx ->
                        FilterChip(
                            selected = coverThemeIndex == idx,
                            onClick = { coverThemeIndex = idx },
                            label = { Text("#${idx + 1}") }
                        )
                    }
                }
            }
            if (!coverUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(coverUri),
                    contentDescription = "Selected Cover",
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.High,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    onSaveOrSubmit(
                        editingArticle?.id,
                        title, authorName, body, subtitle, pubDesc,
                        selectedCategorySlug, articleLanguage, tags, coverUri, coverThemeIndex,
                        false
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("write_save_draft_button")
            ) {
                Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(strings.saveDraft)
            }
            Button(
                onClick = {
                    onSaveOrSubmit(
                        editingArticle?.id,
                        title, authorName, body, subtitle, pubDesc,
                        selectedCategorySlug, articleLanguage, tags, coverUri, coverThemeIndex,
                        true
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = IslamicEmeraldDeep, contentColor = WarmCream),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("write_submit_approval_button")
            ) {
                Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(strings.submitForApproval)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
