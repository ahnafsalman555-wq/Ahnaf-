package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DataSaverOn
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.AppPreferences
import com.example.data.local.ArticleEntity
import com.example.data.local.ArticleStatus
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.formatPublicationDate
import com.example.ui.theme.DeveloperCreditCard
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.WarmCream
import com.example.ui.theme.crispFor

@Composable
fun ProfileAndSettingsScreen(
    language: AppLanguage,
    preferences: AppPreferences,
    currentUser: UserEntity?,
    myArticles: List<ArticleEntity>,
    onToggleLanguage: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleLowDataMode: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onUpdateProfile: (String, String, String?) -> Unit,
    onSubmitSupportFeedback: (String, String, String) -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onRequireAuth: () -> Unit,
    onLogout: () -> Unit,
    onMessage: (String) -> Unit
) {
    val strings = AppStrings.get(language)
    val context = LocalContext.current
    var showEditProfile by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf<String?>(null) }

    val publishedArticles = myArticles.filter { it.status == ArticleStatus.APPROVED.name }
    val totalViews = publishedArticles.sumOf { it.viewsCount }
    val totalLikes = publishedArticles.sumOf { it.likesCount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. User Profile Identity Card (Permanent User ID & Number)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicNavyDeep),
                border = BorderStroke(1.dp, IslamicGold)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    if (currentUser == null) {
                        Text(
                            text = if (language == AppLanguage.BN) "অতিথি পাঠক" else "Guest Reader",
                            style = MaterialTheme.typography.titleLarge.copy(color = WarmCream, fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.BN) {
                                "স্থায়ী ইউজার আইডি পেতে, লিখতে ও ব্যক্তিগত লাইব্রেরি ব্যবহার করতে সাইন ইন করুন।"
                            } else {
                                "Sign in to receive your permanent RHM ID, write articles, and sync your personal library."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = WarmCream.copy(alpha = 0.94f))
                                .crispFor(
                                    if (language == AppLanguage.BN) {
                                        "স্থায়ী ইউজার আইডি পেতে, লিখতে ও ব্যক্তিগত লাইব্রেরি ব্যবহার করতে সাইন ইন করুন।"
                                    } else {
                                        "Sign in to receive your permanent RHM ID, write articles, and sync your personal library."
                                    },
                                    language.code
                                )
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onRequireAuth,
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = IslamicNavyDeep)
                        ) {
                            Text(strings.signInButton, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!currentUser.avatarUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = Uri.parse(currentUser.avatarUri),
                                    contentDescription = currentUser.fullName,
                                    contentScale = ContentScale.Crop,
                                    filterQuality = androidx.compose.ui.graphics.FilterQuality.High,
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Surface(shape = CircleShape, color = IslamicGold, modifier = Modifier.size(62.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            currentUser.fullName.take(1).uppercase(),
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                color = IslamicNavyDeep,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser.fullName,
                                    style = MaterialTheme.typography.titleLarge.copy(color = WarmCream, fontWeight = FontWeight.Bold)
                                        .crispFor(currentUser.fullName, language.code)
                                )
                                Text(
                                    text = "@${currentUser.username} • ${currentUser.role}",
                                    style = MaterialTheme.typography.labelMedium.copy(color = IslamicGoldLight)
                                )
                                Text(
                                    text = currentUser.email,
                                    style = MaterialTheme.typography.bodySmall.copy(color = WarmCream.copy(alpha = 0.92f))
                                )
                            }
                            IconButton(onClick = { showEditProfile = true }) {
                                Icon(Icons.Outlined.Edit, contentDescription = "Edit profile", tint = IslamicGold)
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        Surface(
                            color = IslamicGold.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(strings.userIdLabel, style = MaterialTheme.typography.labelSmall, color = IslamicGoldLight)
                                    Text(currentUser.userId, style = MaterialTheme.typography.titleMedium, color = IslamicGold, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(strings.userNumberLabel, style = MaterialTheme.typography.labelSmall, color = IslamicGoldLight)
                                    Text("${currentUser.userNumber}", style = MaterialTheme.typography.titleMedium, color = WarmCream, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        // Author Analytics Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatMini(publishedArticles.size.toString(), if (language == AppLanguage.BN) "প্রকাশিত" else "Published")
                            StatMini(totalViews.toString(), strings.viewsLabel)
                            StatMini(totalLikes.toString(), strings.likesLabel)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${if (language == AppLanguage.BN) "নিবন্ধনের তারিখ" else "Joined"}: ${formatPublicationDate(currentUser.createdAt, language)}",
                            style = MaterialTheme.typography.labelSmall.copy(color = WarmCream.copy(alpha = 0.90f))
                        )
                    }
                }
            }
        }

        // 2. Admin & Developer Super Admin Control Panel Button
        if (currentUser?.isAnyAdmin() == true) {
            item {
                Button(
                    onClick = onOpenAdminDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("open_admin_dashboard_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicEmeraldDeep, contentColor = IslamicGoldLight)
                ) {
                    Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (currentUser.isDeveloperProtected) strings.developerControlPanel else strings.adminDashboardTitle,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. App Preferences (Bilingual, Dark Mode, Low Data Mode, Notifications)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (language == AppLanguage.BN) "অ্যাপ ও ভাষা সেটিংস" else "App & Display Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Language, contentDescription = null, tint = IslamicGold)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(if (language == AppLanguage.BN) "অ্যাপের ভাষা" else "App Language", fontWeight = FontWeight.SemiBold)
                                Text(language.displayName, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        OutlinedButton(onClick = onToggleLanguage) {
                            val toggleLabel = if (language == AppLanguage.BN) "Switch to English" else "বাংলায় দেখুন"
                            Text(
                                toggleLabel,
                                style = MaterialTheme.typography.labelLarge.crispFor(toggleLabel, language.code)
                            )
                        }
                    }
                    SettingSwitchRow(
                        icon = Icons.Outlined.DarkMode,
                        title = strings.darkMode,
                        checked = preferences.darkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                    SettingSwitchRow(
                        icon = Icons.Outlined.DataSaverOn,
                        title = strings.lowDataMode,
                        subtitle = if (language == AppLanguage.BN) "ছবি ও ব্যাকগ্রাউন্ড ডাটা সাশ্রয় করুন" else "Load text first and minimize background data",
                        checked = preferences.lowDataMode,
                        onCheckedChange = onToggleLowDataMode
                    )
                    SettingSwitchRow(
                        icon = Icons.Outlined.NotificationsActive,
                        title = strings.notificationsTitle,
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = onToggleNotifications
                    )
                }
            }
        }

        // 4. Support & Donation Section (bKash: 01924442346)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, IslamicGold)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.VolunteerActivism, contentDescription = null, tint = IslamicGold)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.donationTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        strings.donationDescription,
                        style = MaterialTheme.typography.bodyMedium.crispFor(strings.donationDescription, language.code)
                    )
                    Surface(
                        color = IslamicGold.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("bKash Number", style = MaterialTheme.typography.labelSmall)
                                Text("01924442346", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = IslamicNavy)
                            }
                            Button(
                                onClick = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("bKash Number", "01924442346"))
                                    onMessage(if (language == AppLanguage.BN) "বিকাশ নম্বর 01924442346 কপি হয়েছে" else "bKash number 01924442346 copied")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicNavy, contentColor = WarmCream)
                            ) {
                                Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(strings.copyBkash)
                            }
                        }
                    }
                    Text(
                        text = if (language == AppLanguage.BN) {
                            "সহযোগিতা সম্পূর্ণ ঐচ্ছিক। লেখা পড়া বা জমা দেওয়ার জন্য কোনো অনুদান বাধ্যতামূলক নয়।"
                        } else {
                            "Contributions are completely voluntary and never required to read or submit articles."
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 5. Contact & Support Page (WhatsApp & Call: 01924442346)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = IslamicGold)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.supportTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text("WhatsApp: 01924442346\nCall: 01924442346", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/8801924442346"))
                                runCatching { context.startActivity(intent) }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicEmeraldDeep, contentColor = WarmCream)
                        ) {
                            Text(strings.whatsappSupport, maxLines = 1)
                        }
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01924442346"))
                                runCatching { context.startActivity(intent) }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(strings.callSupport, maxLines = 1)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showFeedbackDialog = "PROBLEM" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(strings.reportProblem, maxLines = 1)
                        }
                        OutlinedButton(
                            onClick = { showFeedbackDialog = "FEEDBACK" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Outlined.Feedback, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(strings.sendFeedback, maxLines = 1)
                        }
                    }
                }
            }
        }

        // 6. About Us & Developer Credit
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(strings.aboutUsTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (language == AppLanguage.BN) {
                            "মাসিক রাহাবার ম্যাগাজিন একটি প্রিমিয়াম দ্বিভাষিক ইসলামিক সাহিত্য, শিক্ষা ও জ্ঞানভিত্তিক ডিজিটাল প্রকাশনা প্ল্যাটফর্ম।"
                        } else {
                            "Monthly Rahabar Magazine is a bilingual Islamic literary, educational, and knowledge-focused digital publishing platform."
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Developed & Owned by Ahnaf Salman", fontWeight = FontWeight.Bold)
                    Text("Email: ahnafsalman555@gmail.com", style = MaterialTheme.typography.bodySmall, color = IslamicGold)
                }
            }
        }

        item {
            DeveloperCreditCard(isBengali = language == AppLanguage.BN)
        }

        if (currentUser != null) {
            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button"),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(strings.logout, color = MaterialTheme.colorScheme.error)
                }
            }
        }
        item { Spacer(Modifier.height(18.dp)) }
    }

    if (showEditProfile && currentUser != null) {
        EditProfileDialog(
            user = currentUser,
            language = language,
            onDismiss = { showEditProfile = false },
            onSave = { name, bio, avatar ->
                onUpdateProfile(name, bio, avatar)
                showEditProfile = false
            }
        )
    }

    showFeedbackDialog?.let { fbType ->
        var subject by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = null },
            title = { Text(if (fbType == "PROBLEM") strings.reportProblem else strings.sendFeedback) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text(if (language == AppLanguage.BN) "বিষয়" else "Subject") }
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text(if (language == AppLanguage.BN) "বিস্তারিত বার্তা" else "Message") },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSubmitSupportFeedback(fbType, subject, message)
                    showFeedbackDialog = null
                }) { Text(if (language == AppLanguage.BN) "পাঠান" else "Submit") }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = null }) {
                    Text(if (language == AppLanguage.BN) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatMini(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(color = IslamicGold, fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = WarmCream.copy(alpha = 0.92f)))
    }
}

@Composable
private fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = IslamicGold)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun EditProfileDialog(
    user: UserEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (String, String, String?) -> Unit
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var bio by remember { mutableStateOf(user.bio) }
    var avatarUri by remember { mutableStateOf(user.avatarUri) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) avatarUri = uri.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (language == AppLanguage.BN) "প্রোফাইল সম্পাদনা" else "Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${user.userId} (@${user.username}) • Immutable", style = MaterialTheme.typography.labelSmall, color = IslamicGold)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (language == AppLanguage.BN) "পূর্ণ নাম" else "Full Name") }
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    minLines = 2
                )
                OutlinedButton(onClick = {
                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text(if (language == AppLanguage.BN) "প্রোফাইল ছবি পরিবর্তন" else "Change Profile Photo")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(fullName, bio, avatarUri) }) {
                Text(if (language == AppLanguage.BN) "সংরক্ষণ" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.BN) "বাতিল" else "Cancel")
            }
        }
    )
}
