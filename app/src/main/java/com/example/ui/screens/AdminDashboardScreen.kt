package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AdminPermission
import com.example.data.local.ArticleEntity
import com.example.data.local.ArticleStatus
import com.example.data.local.AuditLogEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.EditionEntity
import com.example.data.local.EditionType
import com.example.data.local.ReportEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserRole
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.StatusPill
import com.example.ui.components.formatPublicationDate
import com.example.ui.theme.DeveloperCreditCard
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.WarmCream
import com.example.ui.theme.crispFor
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    language: AppLanguage,
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    allArticles: List<ArticleEntity>,
    pendingArticles: List<ArticleEntity>,
    categories: List<CategoryEntity>,
    editions: List<EditionEntity>,
    reports: List<ReportEntity>,
    auditLogs: List<AuditLogEntity>,
    totalCommentsCount: Int,
    onApproveArticle: (String, String?, Boolean, Boolean) -> Unit,
    onRejectArticle: (String, String) -> Unit,
    onDeleteArticle: (String) -> Unit,
    onSearchUsers: suspend (String) -> List<UserEntity>,
    onAppointAdmin: (String, Set<AdminPermission>) -> Unit,
    onSetUserSuspended: (String, Boolean) -> Unit,
    onDeleteUser: (String) -> Unit,
    onUpsertEdition: (EditionEntity) -> Unit,
    onUpsertCategory: (CategoryEntity) -> Unit,
    onBroadcastAnnouncement: (String, String, String, String) -> Unit,
    onResolveReport: (ReportEntity, Boolean, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val strings = AppStrings.get(language)
    val scope = rememberCoroutineScope()
    var activeTab by remember { mutableIntStateOf(0) }
    var reviewingArticle by remember { mutableStateOf<ArticleEntity?>(null) }
    var rejectingArticle by remember { mutableStateOf<ArticleEntity?>(null) }
    var confirmDeleteUser by remember { mutableStateOf<UserEntity?>(null) }
    var editingAdminUser by remember { mutableStateOf<UserEntity?>(null) }

    val approvedCount = allArticles.count { it.status == ArticleStatus.APPROVED.name }
    val rejectedCount = allArticles.count { it.status == ArticleStatus.REJECTED.name }
    val totalViews = allArticles.sumOf { it.viewsCount }
    val totalLikes = allArticles.sumOf { it.likesCount }

    val tabLabels = listOf(
        if (language == AppLanguage.BN) "পরিসংখ্যান" else "Overview",
        "${if (language == AppLanguage.BN) "অনুমোদন" else "Approvals"} (${pendingArticles.size})",
        if (language == AppLanguage.BN) "ব্যবহারকারী" else "Users",
        if (language == AppLanguage.BN) "অ্যাডমিন নিয়োগ" else "Admins",
        if (language == AppLanguage.BN) "সংখ্যা ও বিভাগ" else "Editions & Cats",
        if (language == AppLanguage.BN) "রিপোর্ট ও ঘোষণা" else "Moderation",
        if (language == AppLanguage.BN) "অডিট লগ" else "Audit Logs"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentUser?.isDeveloperProtected == true) strings.developerControlPanel else strings.adminDashboardTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Ahnaf Salman • ahnafsalman555@gmail.com",
                            style = MaterialTheme.typography.labelSmall.copy(color = IslamicGoldLight)
                        )
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabLabels.size) { index ->
                    FilterChip(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        label = { Text(tabLabels[index]) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (activeTab) {
                    0 -> {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AdminStatBox("Total Users", allUsers.size.toString(), Modifier.weight(1f))
                                    AdminStatBox("Active Users", allUsers.count { !it.isSuspended }.toString(), Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AdminStatBox("Total Articles", allArticles.size.toString(), Modifier.weight(1f))
                                    AdminStatBox("Pending", pendingArticles.size.toString(), Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AdminStatBox("Approved", approvedCount.toString(), Modifier.weight(1f))
                                    AdminStatBox("Rejected", rejectedCount.toString(), Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AdminStatBox("Total Views", totalViews.toString(), Modifier.weight(1f))
                                    AdminStatBox("Total Likes", totalLikes.toString(), Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AdminStatBox("Comments", totalCommentsCount.toString(), Modifier.weight(1f))
                                    AdminStatBox("Editions", editions.size.toString(), Modifier.weight(1f))
                                }
                            }
                        }
                        item { DeveloperCreditCard(isBengali = language == AppLanguage.BN) }
                    }

                    1 -> {
                        if (pendingArticles.isEmpty()) {
                            item {
                                Text(if (language == AppLanguage.BN) "অপেক্ষমাণ কোনো প্রবন্ধ নেই" else "No pending submissions")
                            }
                        } else {
                            items(pendingArticles, key = { it.id }) { article ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, IslamicGold)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        StatusPill(article.parsedStatus(), language)
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            article.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                                .crispFor(article.title, article.language)
                                        )
                                        Text(
                                            "${article.authorName} (@${article.authorUsername} • ${article.authorPermanentId})",
                                            style = MaterialTheme.typography.labelMedium.crispFor(article.authorName, article.language)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(onClick = { reviewingArticle = article }) {
                                                Text(if (language == AppLanguage.BN) "সম্পূর্ণ পড়ুন" else "Read Full")
                                            }
                                            Button(
                                                onClick = { onApproveArticle(article.id, null, false, false) },
                                                colors = ButtonDefaults.buttonColors(containerColor = StatusApproved, contentColor = WarmCream),
                                                modifier = Modifier.testTag("admin_approve_${article.id}")
                                            ) {
                                                Icon(Icons.Outlined.Check, contentDescription = null)
                                                Text(" Approve")
                                            }
                                            OutlinedButton(
                                                onClick = { rejectingArticle = article },
                                                modifier = Modifier.testTag("admin_reject_${article.id}")
                                            ) {
                                                Icon(Icons.Outlined.Close, contentDescription = null, tint = StatusRejected)
                                                Text(" Reject", color = StatusRejected)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(8.dp))
                            Text(if (language == AppLanguage.BN) "সকল প্রবন্ধ ব্যবস্থাপনা" else "All Articles Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        items(allArticles, key = { "all_${it.id}" }) { art ->
                            Card(shape = RoundedCornerShape(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            art.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                                .crispFor(art.title, art.language)
                                        )
                                        Text(
                                            "${art.authorName} • ${art.status}",
                                            style = MaterialTheme.typography.labelSmall.crispFor(art.authorName, art.language)
                                        )
                                    }
                                    IconButton(onClick = { onDeleteArticle(art.id) }) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "Delete article", tint = StatusRejected)
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        items(allUsers, key = { it.uid }) { user ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (user.isDeveloperProtected) IslamicGold else MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                user.fullName,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                                    .crispFor(user.fullName, language.code)
                                            )
                                            Text("${user.userId} (#${user.userNumber}) • @${user.username}", style = MaterialTheme.typography.labelMedium, color = IslamicGold)
                                            Text("${user.email} • Role: ${user.role}", style = MaterialTheme.typography.bodySmall)
                                        }
                                        if (user.isDeveloperProtected) {
                                            Surface(color = IslamicNavyDeep, shape = RoundedCornerShape(8.dp)) {
                                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = IslamicGold)
                                                    Text(" PROTECTED", style = MaterialTheme.typography.labelSmall, color = IslamicGold)
                                                }
                                            }
                                        }
                                    }
                                    if (!user.isDeveloperProtected) {
                                        Spacer(Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(onClick = { onSetUserSuspended(user.uid, !user.isSuspended) }) {
                                                Text(if (user.isSuspended) "Reactivate" else "Suspend")
                                            }
                                            if (currentUser?.isDeveloperProtected == true) {
                                                OutlinedButton(onClick = { confirmDeleteUser = user }) {
                                                    Text("Delete Account", color = StatusRejected)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        item {
                            var searchInput by remember { mutableStateOf("") }
                            var foundUsers by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
                            Card(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, IslamicGold)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Appoint / Manage Administrators (Super Admin Only)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    OutlinedTextField(
                                        value = searchInput,
                                        onValueChange = { searchInput = it },
                                        label = { Text("Search by User ID (RHM-...) or username") },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                scope.launch { foundUsers = onSearchUsers(searchInput) }
                                            }) {
                                                Icon(Icons.Outlined.Search, contentDescription = "Search")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    foundUsers.forEach { matched ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(matched.fullName, fontWeight = FontWeight.Bold)
                                                    Text("${matched.userId} • @${matched.username} • ${matched.role}", style = MaterialTheme.typography.labelSmall)
                                                }
                                                if (!matched.isDeveloperProtected) {
                                                    Button(onClick = { editingAdminUser = matched }) {
                                                        Text(if (matched.parsedRole() == UserRole.ADMIN) "Permissions" else "Add Admin")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        items(allUsers.filter { it.isAnyAdmin() }, key = { "adm_${it.uid}" }) { adminUser ->
                            Card(shape = RoundedCornerShape(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${adminUser.fullName} (${adminUser.role})", fontWeight = FontWeight.Bold)
                                        Text(adminUser.adminPermissions.ifBlank { "All Super Admin Privileges" }, style = MaterialTheme.typography.labelSmall)
                                    }
                                    if (!adminUser.isDeveloperProtected && currentUser?.isDeveloperProtected == true) {
                                        TextButton(onClick = { editingAdminUser = adminUser }) {
                                            Text("Edit")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        item {
                            var edTitleBn by remember { mutableStateOf("") }
                            var edTitleEn by remember { mutableStateOf("") }
                            var edDescBn by remember { mutableStateOf("") }
                            Card(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, IslamicGold)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Create & Publish Magazine Edition", fontWeight = FontWeight.Bold)
                                    OutlinedTextField(value = edTitleBn, onValueChange = { edTitleBn = it }, label = { Text("Title (বাংলা)") }, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = edTitleEn, onValueChange = { edTitleEn = it }, label = { Text("Title (English)") }, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = edDescBn, onValueChange = { edDescBn = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                                    Button(
                                        onClick = {
                                            if (edTitleBn.isNotBlank() || edTitleEn.isNotBlank()) {
                                                onUpsertEdition(
                                                    EditionEntity(
                                                        id = "edition-${UUID.randomUUID()}",
                                                        titleBn = edTitleBn.ifBlank { edTitleEn },
                                                        titleEn = edTitleEn.ifBlank { edTitleBn },
                                                        editionType = EditionType.MONTHLY.name,
                                                        monthNumber = 12,
                                                        year = 2026,
                                                        descriptionBn = edDescBn,
                                                        descriptionEn = edDescBn,
                                                        isPublished = true
                                                    )
                                                )
                                                edTitleBn = ""; edTitleEn = ""; edDescBn = ""
                                            }
                                        }
                                    ) { Text("Publish Edition & Notify Readers") }
                                }
                            }
                        }
                        item {
                            var catBn by remember { mutableStateOf("") }
                            var catEn by remember { mutableStateOf("") }
                            Card(shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Add Category", fontWeight = FontWeight.Bold)
                                    OutlinedTextField(value = catBn, onValueChange = { catBn = it }, label = { Text("Category Name (বাংলা)") }, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = catEn, onValueChange = { catEn = it }, label = { Text("Category Name (English)") }, modifier = Modifier.fillMaxWidth())
                                    Button(onClick = {
                                        if (catEn.isNotBlank() && catBn.isNotBlank()) {
                                            onUpsertCategory(
                                                CategoryEntity(
                                                    slug = catEn.lowercase().replace(" ", "_"),
                                                    nameBn = catBn,
                                                    nameEn = catEn,
                                                    sortOrder = categories.size + 1
                                                )
                                            )
                                            catBn = ""; catEn = ""
                                        }
                                    }) { Text("Add Category") }
                                }
                            }
                        }
                    }

                    5 -> {
                        item {
                            var annTitleBn by remember { mutableStateOf("") }
                            var annTitleEn by remember { mutableStateOf("") }
                            var annBody by remember { mutableStateOf("") }
                            Card(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, IslamicGold)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Broadcast Announcement", fontWeight = FontWeight.Bold)
                                    OutlinedTextField(value = annTitleBn, onValueChange = { annTitleBn = it }, label = { Text("Title (বাংলা)") }, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = annTitleEn, onValueChange = { annTitleEn = it }, label = { Text("Title (English)") }, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = annBody, onValueChange = { annBody = it }, label = { Text("Announcement Body") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                                    Button(onClick = {
                                        if (annBody.isNotBlank()) {
                                            onBroadcastAnnouncement(annTitleBn.ifBlank { annTitleEn }, annTitleEn.ifBlank { annTitleBn }, annBody, annBody)
                                            annTitleBn = ""; annTitleEn = ""; annBody = ""
                                        }
                                    }) { Text("Send Announcement") }
                                }
                            }
                        }
                        items(reports, key = { it.id }) { rep ->
                            Card(shape = RoundedCornerShape(14.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("${rep.targetType} • ${rep.status}", fontWeight = FontWeight.Bold)
                                    Text(rep.targetPreview, style = MaterialTheme.typography.bodySmall)
                                    Text("Reason: ${rep.reason}", style = MaterialTheme.typography.labelSmall)
                                    if (rep.status == "OPEN") {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(onClick = { onResolveReport(rep, false, true) }) { Text("Remove Target") }
                                            OutlinedButton(onClick = { onResolveReport(rep, true, false) }) { Text("Dismiss") }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    6 -> {
                        items(auditLogs, key = { it.id }) { log ->
                            Card(shape = RoundedCornerShape(12.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("${log.action} • ${log.targetType}", style = MaterialTheme.typography.labelLarge, color = IslamicGold, fontWeight = FontWeight.Bold)
                                    Text(log.details, style = MaterialTheme.typography.bodySmall)
                                    Text("By ${log.actorName} • ${formatPublicationDate(log.createdAt, language)}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    reviewingArticle?.let { art ->
        AlertDialog(
            onDismissRequest = { reviewingArticle = null },
            title = {
                Text(
                    art.title,
                    style = MaterialTheme.typography.titleLarge.crispFor(art.title, art.language)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "${strings.authorPrefix} ${art.authorName}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = IslamicGold)
                            .crispFor(art.authorName, art.language)
                    )
                    Text(
                        art.body,
                        style = MaterialTheme.typography.bodySmall.crispFor(art.body, art.language)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onApproveArticle(art.id, null, false, false)
                    reviewingArticle = null
                }) { Text("Approve") }
            },
            dismissButton = {
                TextButton(onClick = { reviewingArticle = null }) { Text("Close") }
            }
        )
    }

    rejectingArticle?.let { art ->
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { rejectingArticle = null },
            title = { Text("Reject / Request Revision") },
            text = {
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Explanation for Author") },
                    minLines = 3
                )
            },
            confirmButton = {
                Button(onClick = {
                    onRejectArticle(art.id, reason)
                    rejectingArticle = null
                }) { Text("Confirm Reject") }
            },
            dismissButton = {
                TextButton(onClick = { rejectingArticle = null }) { Text("Cancel") }
            }
        )
    }

    confirmDeleteUser?.let { target ->
        AlertDialog(
            onDismissRequest = { confirmDeleteUser = null },
            title = { Text("Confirm Safe Account Deletion") },
            text = { Text("Delete ${target.fullName} (${target.userId}) and their unpublished content? This action is audit-logged.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(target.uid)
                        confirmDeleteUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRejected)
                ) { Text("Delete Permanently") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteUser = null }) { Text("Cancel") }
            }
        )
    }

    editingAdminUser?.let { target ->
        var selectedPerms by remember(target.uid) { mutableStateOf(target.parsedPermissions()) }
        AlertDialog(
            onDismissRequest = { editingAdminUser = null },
            title = { Text("Permissions for ${target.fullName}") },
            text = {
                Column {
                    AdminPermission.entries.forEach { perm ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedPerms.contains(perm),
                                onCheckedChange = { checked ->
                                    selectedPerms = if (checked) selectedPerms + perm else selectedPerms - perm
                                }
                            )
                            Text(perm.name, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onAppointAdmin(target.uid, selectedPerms)
                    editingAdminUser = null
                }) { Text("Save Role") }
            },
            dismissButton = {
                TextButton(onClick = { editingAdminUser = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminStatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = IslamicGold, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
