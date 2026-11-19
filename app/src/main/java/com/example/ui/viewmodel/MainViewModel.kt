package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.QuranAudioPlayer
import com.example.audio.RecitationPlaybackState
import com.example.data.local.AdminPermission
import com.example.data.local.AnnouncementEntity
import com.example.data.local.AppPreferences
import com.example.data.local.ArticleEntity
import com.example.data.local.AuditLogEntity
import com.example.data.local.BookmarkEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.CollectionEntity
import com.example.data.local.CommentEntity
import com.example.data.local.EditionEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PersonalNoteEntity
import com.example.data.local.ReaderThemeMode
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.ReportEntity
import com.example.data.local.UserEntity
import com.example.data.repository.RahabarRepository
import com.example.i18n.AppLanguage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption {
    LATEST,
    MOST_READ,
    MOST_LIKED
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: RahabarRepository,
    val quranAudioPlayer: QuranAudioPlayer
) : ViewModel() {

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val events = _events.asSharedFlow()

    val preferences: StateFlow<AppPreferences> = repository.preferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppPreferences()
    )

    val appLanguage: StateFlow<AppLanguage> = preferences.map { AppLanguage.fromCode(it.languageCode) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.BN)

    val currentUser: StateFlow<UserEntity?> = repository.currentUserFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val categories: StateFlow<List<CategoryEntity>> = repository.observeCategories().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val publishedEditions: StateFlow<List<EditionEntity>> = repository.observePublishedEditions().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allEditions: StateFlow<List<EditionEntity>> = repository.observeAllEditions().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val approvedArticles: StateFlow<List<ArticleEntity>> = repository.observeApprovedArticles().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val offlineArticles: StateFlow<List<ArticleEntity>> = repository.observeOfflineArticles().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allUsers: StateFlow<List<UserEntity>> = repository.observeAllUsers().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allArticlesForAdmin: StateFlow<List<ArticleEntity>> = repository.observeAllArticlesForAdmin().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val pendingArticles: StateFlow<List<ArticleEntity>> = repository.observePendingArticles().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val reports: StateFlow<List<ReportEntity>> = repository.observeReports().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.observeAnnouncements().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.observeAuditLogs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val totalCommentsCount: StateFlow<Int> = repository.observeTotalCommentsCount().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val notifications: StateFlow<List<NotificationEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) repository.observeNotifications("GUEST", false)
        else repository.observeNotifications(user.uid, user.isAnyAdmin())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = currentUser.flatMapLatest { user ->
        if (user == null) repository.observeUnreadNotificationsCount("GUEST", false)
        else repository.observeUnreadNotificationsCount(user.uid, user.isAnyAdmin())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // User Personal Library Flows
    val savedArticles: StateFlow<List<ArticleEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeSavedArticles(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<BookmarkEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeBookmarks(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalNotes: StateFlow<List<PersonalNoteEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeNotes(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collections: StateFlow<List<CollectionEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeCollections(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingHistory: StateFlow<List<ReadingProgressEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeReadingHistory(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myArticles: StateFlow<List<ArticleEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeArticlesByAuthor(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followedAuthorIds: StateFlow<List<String>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeFollowedAuthorIds(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterLanguage = MutableStateFlow("ALL")
    val filterLanguage: StateFlow<String> = _filterLanguage.asStateFlow()

    private val _filterCategory = MutableStateFlow("ALL")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.LATEST)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    val searchResults: StateFlow<List<ArticleEntity>> = combine(
        _searchQuery,
        _filterLanguage,
        _filterCategory
    ) { q, lang, cat -> Triple(q, lang, cat) }.flatMapLatest { (q, lang, cat) ->
        repository.searchApprovedArticles(q, lang, cat)
    }.combine(_sortOption) { list, sort ->
        when (sort) {
            SortOption.LATEST -> list
            SortOption.MOST_READ -> list.sortedByDescending { it.viewsCount }
            SortOption.MOST_LIKED -> list.sortedByDescending { it.likesCount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recitationState: StateFlow<RecitationPlaybackState> = quranAudioPlayer.state

    init {
        viewModelScope.launch { repository.ensureSeeded() }
    }

    fun emitMessage(message: String) {
        _events.tryEmit(message)
    }

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateLanguageFilter(lang: String) { _filterLanguage.value = lang }
    fun updateCategoryFilter(slug: String) { _filterCategory.value = slug }
    fun updateSortOption(option: SortOption) { _sortOption.value = option }

    // --- Language & Settings ---
    fun toggleAppLanguage() {
        val next = if (appLanguage.value == AppLanguage.BN) "en" else "bn"
        viewModelScope.launch {
            repository.setLanguage(next, currentUser.value?.uid)
        }
    }

    fun setAppLanguage(code: String) {
        viewModelScope.launch { repository.setLanguage(code, currentUser.value?.uid) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun setLowDataMode(enabled: Boolean) {
        viewModelScope.launch { repository.setLowDataMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setNotificationsEnabled(enabled) }
    }

    fun updateReaderSettings(fontSizeSp: Float, lineSpacing: Float, themeMode: ReaderThemeMode) {
        viewModelScope.launch { repository.updateReaderSettings(fontSizeSp, lineSpacing, themeMode) }
    }

    // --- Auth ---
    suspend fun checkUsernameAvailable(username: String): Boolean = repository.isUsernameAvailable(username)

    fun login(identifier: String, password: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.login(identifier, password)
            res.onSuccess {
                emitMessage(if (appLanguage.value == AppLanguage.BN) "আসসালামু আলাইকুম, ${it.fullName}!" else "Assalamu Alaikum, ${it.fullName}!")
                onDone(true)
            }.onFailure {
                emitMessage(it.message ?: "Login failed")
                onDone(false)
            }
        }
    }

    fun register(fullName: String, username: String, email: String, password: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.registerUser(fullName, username, email, password, appLanguage.value.code)
            res.onSuccess {
                emitMessage(if (appLanguage.value == AppLanguage.BN) "নিবন্ধন সফল হয়েছে! আইডি: ${it.userId}" else "Account created! ID: ${it.userId}")
                onDone(true)
            }.onFailure {
                emitMessage(it.message ?: "Registration failed")
                onDone(false)
            }
        }
    }

    fun resetPassword(email: String, username: String, newPassword: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.resetPasswordByEmailAndUsername(email, username, newPassword)
            res.onSuccess {
                emitMessage(if (appLanguage.value == AppLanguage.BN) "পাসওয়ার্ড সফলভাবে পরিবর্তন হয়েছে" else "Password updated successfully")
                onDone(true)
            }.onFailure {
                emitMessage(it.message ?: "Password recovery failed")
                onDone(false)
            }
        }
    }

    fun signInAsDeveloperSuperAdmin() {
        viewModelScope.launch {
            repository.signInAsDeveloperSuperAdmin().onSuccess {
                emitMessage("Signed in as Super Admin: ${it.fullName} (${it.userId})")
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch { repository.continueAsGuest() }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }

    fun updateProfile(fullName: String, bio: String, avatarUri: String?) {
        val uid = currentUser.value?.uid ?: return
        viewModelScope.launch {
            repository.updateProfile(uid, fullName, bio, avatarUri)
                .onSuccess { emitMessage(if (appLanguage.value == AppLanguage.BN) "প্রোফাইল হালনাগাদ হয়েছে" else "Profile updated") }
                .onFailure { emitMessage(it.message ?: "Failed") }
        }
    }

    // --- Article Observations ---
    fun observeArticle(id: String): Flow<ArticleEntity?> = repository.observeArticleById(id)
    fun observeUser(uid: String): Flow<UserEntity?> = repository.observeUser(uid)
    fun observeArticlesByAuthor(uid: String): Flow<List<ArticleEntity>> = repository.observeArticlesByAuthor(uid)
    fun observeArticlesInEdition(editionId: String): Flow<List<ArticleEntity>> = repository.observeArticlesInEdition(editionId)
    fun observeIsLiked(articleId: String): Flow<Boolean> = currentUser.flatMapLatest { repository.observeIsLiked(articleId, it?.uid) }
    fun observeIsSaved(articleId: String): Flow<Boolean> = currentUser.flatMapLatest { repository.observeIsSaved(it?.uid, articleId) }
    fun observeIsFollowing(authorUid: String): Flow<Boolean> = currentUser.flatMapLatest { repository.observeIsFollowing(it?.uid, authorUid) }
    fun observeFollowersCount(authorUid: String): Flow<Int> = repository.observeFollowersCount(authorUid)
    fun observeComments(articleId: String): Flow<List<CommentEntity>> = repository.observeComments(articleId)

    // --- Article Editor & Reading Actions ---
    fun submitOrSaveArticle(
        existingArticleId: String?,
        title: String,
        authorName: String,
        body: String,
        subtitle: String,
        publicationDescription: String,
        categorySlug: String,
        language: String,
        tags: String,
        coverUri: String?,
        coverThemeIndex: Int,
        submitForReview: Boolean,
        onSuccess: (ArticleEntity) -> Unit
    ) {
        val author = currentUser.value ?: run {
            emitMessage(if (appLanguage.value == AppLanguage.BN) "অনুগ্রহ করে প্রথমে লগইন করুন" else "Please sign in first")
            return
        }
        viewModelScope.launch {
            repository.submitOrSaveArticle(
                existingArticleId, title, authorName, body, subtitle, publicationDescription,
                categorySlug, language, tags, coverUri, coverThemeIndex, submitForReview, author
            ).onSuccess {
                emitMessage(
                    if (submitForReview) {
                        if (appLanguage.value == AppLanguage.BN) "প্রবন্ধটি অনুমোদনের জন্য জমা হয়েছে" else "Article submitted for approval"
                    } else {
                        if (appLanguage.value == AppLanguage.BN) "খসড়া সংরক্ষিত হয়েছে" else "Draft saved"
                    }
                )
                onSuccess(it)
            }.onFailure {
                emitMessage(it.message ?: "Validation failed")
            }
        }
    }

    fun recordView(articleId: String) {
        viewModelScope.launch { repository.recordGenuineView(articleId, currentUser.value?.uid) }
    }

    fun toggleLike(articleId: String) {
        val uid = currentUser.value?.uid ?: run {
            emitMessage(if (appLanguage.value == AppLanguage.BN) "পছন্দ করতে লগইন করুন" else "Sign in to like articles")
            return
        }
        viewModelScope.launch { repository.toggleLike(articleId, uid) }
    }

    fun toggleFollowAuthor(authorUid: String, currentlyFollowing: Boolean) {
        val uid = currentUser.value?.uid ?: run {
            emitMessage(if (appLanguage.value == AppLanguage.BN) "অনুসরণ করতে লগইন করুন" else "Sign in to follow authors")
            return
        }
        viewModelScope.launch { repository.toggleFollowAuthor(uid, authorUid, currentlyFollowing) }
    }

    fun toggleSaveArticle(articleId: String, currentlySaved: Boolean, collectionName: String = "General") {
        val uid = currentUser.value?.uid ?: run {
            emitMessage(if (appLanguage.value == AppLanguage.BN) "সংরক্ষণ করতে লগইন করুন" else "Sign in to save articles")
            return
        }
        viewModelScope.launch {
            repository.toggleSaveArticle(uid, articleId, currentlySaved, collectionName)
            emitMessage(if (!currentlySaved) "Saved to library" else "Removed from saved")
        }
    }

    fun toggleOfflineDownload(articleId: String, downloaded: Boolean) {
        viewModelScope.launch {
            repository.toggleOfflineDownload(articleId, downloaded)
            emitMessage(if (downloaded) "Saved for offline reading" else "Removed offline copy")
        }
    }

    fun bookmarkParagraph(article: ArticleEntity, paragraphIndex: Int, excerpt: String) {
        val uid = currentUser.value?.uid ?: return
        viewModelScope.launch {
            repository.bookmarkParagraph(uid, article, paragraphIndex, excerpt)
            emitMessage(if (appLanguage.value == AppLanguage.BN) "অনুচ্ছেদ বুকমার্ক করা হয়েছে" else "Paragraph bookmarked")
        }
    }

    fun deleteBookmark(id: String) {
        viewModelScope.launch { repository.deleteBookmark(id) }
    }

    fun addPersonalNote(article: ArticleEntity, noteText: String) {
        val uid = currentUser.value?.uid ?: return
        viewModelScope.launch {
            repository.addPersonalNote(uid, article, noteText)
            emitMessage(if (appLanguage.value == AppLanguage.BN) "ব্যক্তিগত নোট সংরক্ষিত হয়েছে" else "Personal note saved")
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch { repository.deleteNote(id) }
    }

    fun createCollection(name: String, description: String) {
        val uid = currentUser.value?.uid ?: return
        viewModelScope.launch { repository.createCollection(uid, name, description) }
    }

    fun saveReadingProgress(articleId: String, paragraphIndex: Int, progressPercent: Int) {
        val uid = currentUser.value?.uid ?: return
        viewModelScope.launch { repository.saveReadingProgress(uid, articleId, paragraphIndex, progressPercent) }
    }

    suspend fun getReadingProgress(articleId: String): ReadingProgressEntity? {
        val uid = currentUser.value?.uid ?: return null
        return repository.getReadingProgress(uid, articleId)
    }

    fun postComment(articleId: String, parentCommentId: String?, content: String, onDone: () -> Unit = {}) {
        val user = currentUser.value ?: run {
            emitMessage(if (appLanguage.value == AppLanguage.BN) "মন্তব্য করতে লগইন করুন" else "Sign in to comment")
            return
        }
        viewModelScope.launch {
            repository.postComment(articleId, parentCommentId, user, content)
                .onSuccess { onDone() }
                .onFailure { emitMessage(it.message ?: "Comment failed") }
        }
    }

    fun submitReport(targetType: String, targetId: String, targetPreview: String, reason: String) {
        val uid = currentUser.value?.uid ?: "guest"
        viewModelScope.launch {
            repository.submitReport(targetType, targetId, targetPreview, uid, reason)
            emitMessage(if (appLanguage.value == AppLanguage.BN) "রিপোর্টটি পর্যালোচনার জন্য জমা হয়েছে" else "Report submitted for moderation")
        }
    }

    // --- Notifications ---
    fun markNotificationRead(id: String) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun markAllNotificationsRead() {
        val user = currentUser.value
        viewModelScope.launch {
            repository.markAllNotificationsRead(user?.uid ?: "GUEST", user?.isAnyAdmin() == true)
        }
    }

    // --- Admin & Developer Control Panel ---
    fun approveArticle(articleId: String, editionId: String? = null, featured: Boolean = false, editorsChoice: Boolean = false) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.approveArticle(articleId, admin, editionId, featured, editorsChoice)
                .onSuccess { emitMessage("Article approved and published") }
                .onFailure { emitMessage(it.message ?: "Action denied") }
        }
    }

    fun rejectArticle(articleId: String, reason: String) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.rejectArticle(articleId, reason, admin)
                .onSuccess { emitMessage("Article returned to author with feedback") }
                .onFailure { emitMessage(it.message ?: "Action denied") }
        }
    }

    fun deleteArticleByAdmin(articleId: String) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteArticleByAdmin(articleId, admin)
                .onSuccess { emitMessage("Article deleted") }
                .onFailure { emitMessage(it.message ?: "Action denied") }
        }
    }

    suspend fun searchUsersForAdmin(query: String): List<UserEntity> = repository.findUserForAdmin(query)

    fun appointOrUpdateAdmin(targetUid: String, permissions: Set<AdminPermission>) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.appointOrUpdateAdmin(targetUid, permissions, admin)
                .onSuccess { emitMessage("Admin permissions updated") }
                .onFailure { emitMessage(it.message ?: "Action denied") }
        }
    }

    fun setUserSuspended(targetUid: String, suspend: Boolean) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.setUserSuspended(targetUid, suspend, admin)
                .onSuccess { emitMessage(if (suspend) "User suspended" else "User reactivated") }
                .onFailure { emitMessage(it.message ?: "Action denied") }
        }
    }

    fun deleteUserAccount(targetUid: String) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteUserAccount(targetUid, admin)
                .onSuccess { emitMessage("User account deleted safely") }
                .onFailure { emitMessage(it.message ?: "Action denied") }
        }
    }

    fun upsertEdition(edition: EditionEntity) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.createOrPublishEdition(edition, admin)
                .onSuccess { emitMessage("Edition saved") }
                .onFailure { emitMessage(it.message ?: "Failed") }
        }
    }

    fun upsertCategory(category: CategoryEntity) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.upsertCategory(category, admin)
                .onSuccess { emitMessage("Category updated") }
                .onFailure { emitMessage(it.message ?: "Failed") }
        }
    }

    fun broadcastAnnouncement(titleBn: String, titleEn: String, bodyBn: String, bodyEn: String) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.broadcastAnnouncement(titleBn, titleEn, bodyBn, bodyEn, admin)
                .onSuccess { emitMessage("Announcement sent to all users") }
                .onFailure { emitMessage(it.message ?: "Failed") }
        }
    }

    fun resolveReport(report: ReportEntity, dismiss: Boolean, hideTarget: Boolean) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            repository.resolveReport(report, dismiss, hideTarget, admin)
                .onSuccess { emitMessage("Report updated") }
                .onFailure { emitMessage(it.message ?: "Failed") }
        }
    }

    fun submitSupportFeedback(type: String, subject: String, message: String) {
        val uid = currentUser.value?.uid ?: "guest"
        viewModelScope.launch {
            repository.submitFeedback(uid, type, subject, message)
            emitMessage(if (appLanguage.value == AppLanguage.BN) "আপনার বার্তা সাদরে গৃহীত হয়েছে" else "Feedback received. JazakAllahu Khairan.")
        }
    }

    class Factory(
        private val repository: RahabarRepository,
        private val audioPlayer: QuranAudioPlayer
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository, audioPlayer) as T
        }
    }
}
