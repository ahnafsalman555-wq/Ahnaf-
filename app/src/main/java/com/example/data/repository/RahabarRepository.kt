package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.local.AdminPermission
import com.example.data.local.AnnouncementEntity
import com.example.data.local.AppPreferences
import com.example.data.local.ArticleEntity
import com.example.data.local.ArticleLikeEntity
import com.example.data.local.ArticleRevisionEntity
import com.example.data.local.ArticleStatus
import com.example.data.local.ArticleViewLogEntity
import com.example.data.local.AuditLogEntity
import com.example.data.local.BookmarkEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.CollectionEntity
import com.example.data.local.CommentEntity
import com.example.data.local.EditionEntity
import com.example.data.local.FollowEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PasswordHasher
import com.example.data.local.PersonalNoteEntity
import com.example.data.local.RahabarDatabase
import com.example.data.local.ReaderThemeMode
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.ReportEntity
import com.example.data.local.SavedArticleEntity
import com.example.data.local.SeedDataProvider
import com.example.data.local.SupportFeedbackEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserPreferencesDataStore
import com.example.data.local.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import kotlin.math.max

class RahabarRepository(
    private val db: RahabarDatabase,
    private val prefs: UserPreferencesDataStore
) {
    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val editionDao = db.editionDao()
    private val articleDao = db.articleDao()
    private val engagementDao = db.engagementDao()
    private val governanceDao = db.governanceDao()

    val preferencesFlow: Flow<AppPreferences> = prefs.preferencesFlow

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentUserFlow: Flow<UserEntity?> = prefs.preferencesFlow.flatMapLatest { appPrefs ->
        val uid = appPrefs.currentUserUid
        if (uid.isNullOrBlank()) flowOf(null) else userDao.observeUserByUid(uid)
    }

    fun observeCategories(): Flow<List<CategoryEntity>> = categoryDao.observeCategories()
    fun observePublishedEditions(): Flow<List<EditionEntity>> = editionDao.observePublishedEditions()
    fun observeAllEditions(): Flow<List<EditionEntity>> = editionDao.observeAllEditions()
    fun observeApprovedArticles(): Flow<List<ArticleEntity>> = articleDao.observeApprovedArticles()
    fun observeAllArticlesForAdmin(): Flow<List<ArticleEntity>> = articleDao.observeAllArticlesForAdmin()
    fun observePendingArticles(): Flow<List<ArticleEntity>> = articleDao.observePendingArticles()
    fun observeOfflineArticles(): Flow<List<ArticleEntity>> = articleDao.observeOfflineArticles()
    fun observeArticleById(articleId: String): Flow<ArticleEntity?> = articleDao.observeArticleById(articleId)
    fun observeArticlesByAuthor(uid: String): Flow<List<ArticleEntity>> = articleDao.observeArticlesByAuthor(uid)
    fun observeArticlesInEdition(editionId: String): Flow<List<ArticleEntity>> = articleDao.observeArticlesInEdition(editionId)
    fun observeRevisions(articleId: String): Flow<List<ArticleRevisionEntity>> = articleDao.observeRevisions(articleId)
    fun observeAllUsers(): Flow<List<UserEntity>> = userDao.observeAllUsers()
    fun observeUser(uid: String): Flow<UserEntity?> = userDao.observeUserByUid(uid)

    fun searchApprovedArticles(query: String, language: String, categorySlug: String): Flow<List<ArticleEntity>> =
        articleDao.searchApprovedArticles(query.trim(), language, categorySlug)

    fun observeIsLiked(articleId: String, uid: String?): Flow<Boolean> =
        if (uid.isNullOrBlank()) flowOf(false) else engagementDao.observeIsLiked(articleId, uid)

    fun observeIsFollowing(followerUid: String?, authorUid: String): Flow<Boolean> =
        if (followerUid.isNullOrBlank()) flowOf(false) else engagementDao.observeIsFollowing(followerUid, authorUid)

    fun observeFollowersCount(authorUid: String): Flow<Int> = engagementDao.observeFollowersCount(authorUid)
    fun observeFollowedAuthorIds(uid: String): Flow<List<String>> = engagementDao.observeFollowedAuthorIds(uid)

    fun observeIsSaved(uid: String?, articleId: String): Flow<Boolean> =
        if (uid.isNullOrBlank()) flowOf(false) else engagementDao.observeIsSaved(uid, articleId)

    fun observeSavedArticles(uid: String): Flow<List<ArticleEntity>> = engagementDao.observeSavedArticles(uid)
    fun observeBookmarks(uid: String): Flow<List<BookmarkEntity>> = engagementDao.observeBookmarks(uid)
    fun observeNotes(uid: String): Flow<List<PersonalNoteEntity>> = engagementDao.observeNotes(uid)
    fun observeCollections(uid: String): Flow<List<CollectionEntity>> = engagementDao.observeCollections(uid)
    fun observeReadingHistory(uid: String): Flow<List<ReadingProgressEntity>> = engagementDao.observeReadingHistory(uid)
    fun observeComments(articleId: String): Flow<List<CommentEntity>> = engagementDao.observeCommentsForArticle(articleId)
    fun observeTotalCommentsCount(): Flow<Int> = engagementDao.observeTotalCommentsCount()

    fun observeNotifications(uid: String, isAdmin: Boolean): Flow<List<NotificationEntity>> =
        governanceDao.observeNotificationsForUser(uid, isAdmin)

    fun observeUnreadNotificationsCount(uid: String, isAdmin: Boolean): Flow<Int> =
        governanceDao.observeUnreadCount(uid, isAdmin)

    fun observeReports(): Flow<List<ReportEntity>> = governanceDao.observeAllReports()
    fun observeAnnouncements(): Flow<List<AnnouncementEntity>> = governanceDao.observeAnnouncements()
    fun observeAuditLogs(): Flow<List<AuditLogEntity>> = governanceDao.observeAuditLogs()

    // --- Ensure seed data exists on first boot ---
    suspend fun ensureSeeded() {
        RahabarDatabase.seedInitialData(db)
    }

    // --- Preferences ---
    suspend fun setLanguage(languageCode: String, currentUid: String?) {
        prefs.setLanguage(languageCode)
        if (!currentUid.isNullOrBlank()) {
            userDao.updatePreferredLanguage(currentUid, languageCode)
        }
    }

    suspend fun setDarkMode(enabled: Boolean) = prefs.setDarkMode(enabled)
    suspend fun setLowDataMode(enabled: Boolean) = prefs.setLowDataMode(enabled)
    suspend fun setNotificationsEnabled(enabled: Boolean) = prefs.setNotificationsEnabled(enabled)
    suspend fun updateReaderSettings(fontSizeSp: Float, lineSpacing: Float, themeMode: ReaderThemeMode) =
        prefs.updateReaderSettings(fontSizeSp, lineSpacing, themeMode)

    // --- Authentication & Permanent User ID System ---
    suspend fun isUsernameAvailable(username: String): Boolean {
        val clean = username.trim().lowercase()
        if (clean.length < 3 || !clean.matches(Regex("^[a-z0-9_\\.]+$"))) return false
        return userDao.getUserByUsername(clean) == null
    }

    suspend fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String,
        preferredLanguage: String
    ): Result<UserEntity> {
        val cleanName = fullName.trim()
        val cleanUsername = username.trim().lowercase()
        val cleanEmail = email.trim().lowercase()

        if (cleanName.length < 2) return Result.failure(IllegalArgumentException("Full name is required"))
        if (!cleanUsername.matches(Regex("^[a-z0-9_\\.]{3,24}$"))) {
            return Result.failure(IllegalArgumentException("Username must be 3-24 characters (a-z, 0-9, _, .)"))
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Valid email address is required"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        return try {
            val created = db.withTransaction {
                if (userDao.getUserByUsername(cleanUsername) != null) {
                    throw IllegalStateException("This username is already assigned to another account.")
                }
                if (userDao.getUserByEmail(cleanEmail) != null) {
                    throw IllegalStateException("An account with this email already exists.")
                }
                val nextNum = userDao.getMaxUserNumber() + 1L
                val permanentId = "RHM-$nextNum"
                val salt = PasswordHasher.generateSalt()
                val hash = PasswordHasher.hashPassword(password, salt)
                val user = UserEntity(
                    uid = "uid-${UUID.randomUUID()}",
                    userId = permanentId,
                    userNumber = nextNum,
                    username = cleanUsername,
                    fullName = cleanName,
                    email = cleanEmail,
                    passwordHash = hash,
                    salt = salt,
                    preferredLanguage = preferredLanguage,
                    role = UserRole.USER.name,
                    adminPermissions = "",
                    isSuspended = false,
                    isDeveloperProtected = false
                )
                userDao.insertUser(user)
                governanceDao.insertNotification(
                    NotificationEntity(
                        id = "notif-welcome-${user.uid}",
                        recipientUserId = user.uid,
                        titleBn = "আহলান ওয়া সাহলান, ${user.fullName}!",
                        titleEn = "Ahlan wa Sahlan, ${user.fullName}!",
                        messageBn = "আপনার স্থায়ী আইডি ${user.userId} সফলভাবে তৈরি হয়েছে। আসসালামু আলাইকুম!",
                        messageEn = "Your permanent ID ${user.userId} has been assigned. Assalamu Alaikum!",
                        type = "SYSTEM"
                    )
                )
                user
            }
            prefs.setSession(created.uid, isGuest = false)
            prefs.setLanguage(preferredLanguage)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(emailOrUsername: String, password: String): Result<UserEntity> {
        val identifier = emailOrUsername.trim().lowercase()
        val user = userDao.getUserByEmail(identifier) ?: userDao.getUserByUsername(identifier)
            ?: return Result.failure(IllegalArgumentException("Account not found."))
        if (user.isSuspended) {
            return Result.failure(IllegalStateException("This account has been suspended by administration."))
        }
        if (!PasswordHasher.verify(password, user.salt, user.passwordHash)) {
            return Result.failure(IllegalArgumentException("Incorrect password."))
        }
        prefs.setSession(user.uid, isGuest = false)
        prefs.setLanguage(user.preferredLanguage)
        return Result.success(user)
    }

    suspend fun signInAsDeveloperSuperAdmin(): Result<UserEntity> {
        ensureSeeded()
        val admin = userDao.getUserByUid(SeedDataProvider.SUPER_ADMIN_UID)
            ?: return Result.failure(IllegalStateException("Super Admin profile not found"))
        prefs.setSession(admin.uid, isGuest = false)
        return Result.success(admin)
    }

    suspend fun resetPasswordByEmailAndUsername(email: String, username: String, newPassword: String): Result<Unit> {
        if (newPassword.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        val user = userDao.getUserByEmail(email.trim()) ?: return Result.failure(IllegalArgumentException("Email not found"))
        if (!user.username.equals(username.trim(), ignoreCase = true)) {
            return Result.failure(IllegalArgumentException("Username does not match this account"))
        }
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(newPassword, salt)
        userDao.updatePassword(user.uid, hash, salt)
        return Result.success(Unit)
    }

    suspend fun continueAsGuest() {
        prefs.setSession(null, isGuest = true)
    }

    suspend fun logout() {
        prefs.setSession(null, isGuest = false)
    }

    suspend fun updateProfile(uid: String, fullName: String, bio: String, avatarUri: String?): Result<Unit> {
        val current = userDao.getUserByUid(uid) ?: return Result.failure(IllegalStateException("User not found"))
        if (fullName.trim().length < 2) return Result.failure(IllegalArgumentException("Name cannot be empty"))
        userDao.updateUser(
            current.copy(
                fullName = fullName.trim(),
                bio = bio.trim(),
                avatarUri = avatarUri
            )
        )
        return Result.success(Unit)
    }

    // --- Article Submission & Revision History (3 Mandatory Fields Enforced) ---
    suspend fun submitOrSaveArticle(
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
        author: UserEntity
    ): Result<ArticleEntity> {
        val cleanTitle = title.trim()
        val cleanAuthor = authorName.trim()
        val cleanBody = body.trim()

        if (cleanTitle.isBlank()) return Result.failure(IllegalArgumentException("Field 1 (Article Title) is mandatory."))
        if (cleanAuthor.isBlank()) return Result.failure(IllegalArgumentException("Field 2 (Author Name) is mandatory."))
        if (cleanBody.isBlank()) return Result.failure(IllegalArgumentException("Field 3 (Main Article) is mandatory."))

        val wordCount = cleanBody.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val readingTime = max(1, (wordCount / 180) + 1)
        val now = System.currentTimeMillis()
        val targetStatus = if (submitForReview) ArticleStatus.PENDING else ArticleStatus.DRAFT

        val existing = existingArticleId?.let { articleDao.getArticleById(it) }
        val articleId = existing?.id ?: "art-${UUID.randomUUID()}"

        val entity = ArticleEntity(
            id = articleId,
            title = cleanTitle,
            subtitle = subtitle.trim(),
            authorUserId = author.uid,
            authorName = cleanAuthor,
            authorUsername = author.username,
            authorPermanentId = author.userId,
            body = cleanBody,
            publicationDescription = publicationDescription.trim(),
            language = language,
            categorySlug = categorySlug,
            tags = tags.trim(),
            editionId = existing?.editionId,
            coverUri = coverUri ?: existing?.coverUri,
            coverThemeIndex = coverThemeIndex,
            status = targetStatus.name,
            rejectionReason = null,
            isFeatured = existing?.isFeatured ?: false,
            isEditorsChoice = existing?.isEditorsChoice ?: false,
            viewsCount = existing?.viewsCount ?: 0L,
            likesCount = existing?.likesCount ?: 0L,
            readingTimeMinutes = readingTime,
            isDownloadedOffline = existing?.isDownloadedOffline ?: false,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            publishedAt = existing?.publishedAt
        )

        db.withTransaction {
            articleDao.upsertArticle(entity)
            articleDao.insertRevision(
                ArticleRevisionEntity(
                    id = "rev-${UUID.randomUUID()}",
                    articleId = articleId,
                    title = cleanTitle,
                    authorName = cleanAuthor,
                    body = cleanBody,
                    status = targetStatus.name,
                    note = if (submitForReview) "Submitted for editorial review" else "Saved draft",
                    revisedByUserId = author.uid,
                    createdAt = now
                )
            )
            if (submitForReview) {
                governanceDao.insertNotification(
                    NotificationEntity(
                        id = "notif-sub-${UUID.randomUUID()}",
                        recipientUserId = "ADMINS",
                        titleBn = "নতুন প্রবন্ধ জমা হয়েছে",
                        titleEn = "New Article Submission",
                        messageBn = "$cleanAuthor '$cleanTitle' প্রবন্ধটি অনুমোদনের জন্য জমা দিয়েছেন।",
                        messageEn = "$cleanAuthor submitted '$cleanTitle' for review.",
                        type = "SUBMISSION",
                        referenceId = articleId,
                        createdAt = now
                    )
                )
            }
        }
        return Result.success(entity)
    }

    // --- Likes, Views (Deduplicated), Follows ---
    suspend fun recordGenuineView(articleId: String, uid: String?) {
        val viewerKey = uid?.takeIf { it.isNotBlank() } ?: "guest_device"
        val inserted = engagementDao.insertViewLog(ArticleViewLogEntity(articleId, viewerKey))
        if (inserted != -1L) {
            articleDao.incrementViewsCount(articleId)
        }
    }

    suspend fun toggleLike(articleId: String, uid: String): Boolean {
        return db.withTransaction {
            val currentlyLiked = engagementDao.isLiked(articleId, uid)
            if (currentlyLiked) {
                val deleted = engagementDao.deleteLike(articleId, uid)
                if (deleted > 0) articleDao.adjustLikesCount(articleId, -1)
                false
            } else {
                val inserted = engagementDao.insertLike(ArticleLikeEntity(articleId, uid))
                if (inserted != -1L) articleDao.adjustLikesCount(articleId, 1)
                true
            }
        }
    }

    suspend fun toggleFollowAuthor(followerUid: String, authorUid: String, currentlyFollowing: Boolean) {
        if (followerUid == authorUid) return
        if (currentlyFollowing) {
            engagementDao.unfollowAuthor(followerUid, authorUid)
        } else {
            engagementDao.followAuthor(FollowEntity(followerUid, authorUid))
        }
    }

    // --- Personal Library & Reading Progress ---
    suspend fun toggleSaveArticle(uid: String, articleId: String, currentlySaved: Boolean, collectionName: String = "General") {
        if (currentlySaved) {
            engagementDao.removeSavedArticle(uid, articleId)
        } else {
            engagementDao.saveArticle(SavedArticleEntity(uid, articleId, collectionName))
        }
    }

    suspend fun toggleOfflineDownload(articleId: String, download: Boolean) {
        articleDao.setOfflineDownloaded(articleId, download)
    }

    suspend fun bookmarkParagraph(uid: String, article: ArticleEntity, paragraphIndex: Int, excerpt: String) {
        engagementDao.insertBookmark(
            BookmarkEntity(
                id = "bm-${UUID.randomUUID()}",
                userId = uid,
                articleId = article.id,
                articleTitle = article.title,
                paragraphIndex = paragraphIndex,
                excerpt = excerpt.take(220)
            )
        )
    }

    suspend fun deleteBookmark(bookmarkId: String) = engagementDao.deleteBookmark(bookmarkId)

    suspend fun addPersonalNote(uid: String, article: ArticleEntity, noteText: String) {
        if (noteText.isBlank()) return
        engagementDao.insertNote(
            PersonalNoteEntity(
                id = "note-${UUID.randomUUID()}",
                userId = uid,
                articleId = article.id,
                articleTitle = article.title,
                noteText = noteText.trim()
            )
        )
    }

    suspend fun deleteNote(noteId: String) = engagementDao.deleteNote(noteId)

    suspend fun createCollection(uid: String, name: String, description: String) {
        if (name.isBlank()) return
        engagementDao.insertCollection(CollectionEntity(uid, name.trim(), description.trim()))
    }

    suspend fun saveReadingProgress(uid: String, articleId: String, paragraphIndex: Int, progressPercent: Int) {
        engagementDao.upsertProgress(
            ReadingProgressEntity(
                userId = uid,
                articleId = articleId,
                paragraphIndex = paragraphIndex,
                progressPercent = progressPercent.coerceIn(0, 100),
                lastReadAt = System.currentTimeMillis(),
                isPendingSync = false
            )
        )
    }

    suspend fun getReadingProgress(uid: String, articleId: String): ReadingProgressEntity? =
        engagementDao.getProgress(uid, articleId)

    // --- Comments & Reports ---
    suspend fun postComment(articleId: String, parentCommentId: String?, user: UserEntity, content: String): Result<Unit> {
        val clean = content.trim()
        if (clean.isBlank()) return Result.failure(IllegalArgumentException("Comment cannot be empty"))
        val comment = CommentEntity(
            id = "cmt-${UUID.randomUUID()}",
            articleId = articleId,
            parentCommentId = parentCommentId,
            userId = user.uid,
            authorName = user.fullName,
            authorUsername = user.username,
            content = clean
        )
        engagementDao.insertComment(comment)
        articleDao.getArticleById(articleId)?.let { article ->
            if (article.authorUserId != user.uid) {
                governanceDao.insertNotification(
                    NotificationEntity(
                        id = "notif-cmt-${UUID.randomUUID()}",
                        recipientUserId = article.authorUserId,
                        titleBn = "আপনার প্রবন্ধে নতুন মন্তব্য",
                        titleEn = "New Comment on Your Article",
                        messageBn = "${user.fullName} '${article.title}' প্রবন্ধে মন্তব্য করেছেন।",
                        messageEn = "${user.fullName} commented on '${article.title}'.",
                        type = "COMMENT",
                        referenceId = articleId
                    )
                )
            }
        }
        return Result.success(Unit)
    }

    suspend fun submitReport(targetType: String, targetId: String, targetPreview: String, reporterUid: String, reason: String) {
        val report = ReportEntity(
            id = "rep-${UUID.randomUUID()}",
            targetType = targetType,
            targetId = targetId,
            targetPreview = targetPreview.take(120),
            reporterUserId = reporterUid,
            reason = reason.trim().ifBlank { "Community ethics review requested" }
        )
        governanceDao.insertReport(report)
        governanceDao.insertNotification(
            NotificationEntity(
                id = "notif-rep-${UUID.randomUUID()}",
                recipientUserId = "ADMINS",
                titleBn = "নতুন কন্টেন্ট রিপোর্ট জমা হয়েছে",
                titleEn = "New Content Report Filed",
                messageBn = "একজন পাঠক একটি $targetType পর্যালোচনার জন্য রিপোর্ট করেছেন।",
                messageEn = "A reader reported a $targetType for moderation review.",
                type = "REPORT",
                referenceId = targetId
            )
        )
    }

    // --- Notifications ---
    suspend fun markNotificationRead(id: String) = governanceDao.markAsRead(id)
    suspend fun markAllNotificationsRead(uid: String, isAdmin: Boolean) = governanceDao.markAllRead(uid, isAdmin)

    // --- Admin & Super Admin Governance (Protected Invariants) ---
    suspend fun approveArticle(articleId: String, admin: UserEntity, editionId: String? = null, featured: Boolean = false, editorsChoice: Boolean = false): Result<Unit> {
        if (!admin.hasPermission(AdminPermission.ARTICLE_APPROVAL)) {
            return Result.failure(SecurityException("Not authorized to approve articles"))
        }
        val article = articleDao.getArticleById(articleId) ?: return Result.failure(IllegalStateException("Article not found"))
        val now = System.currentTimeMillis()
        db.withTransaction {
            articleDao.upsertArticle(
                article.copy(
                    status = ArticleStatus.APPROVED.name,
                    rejectionReason = null,
                    editionId = editionId ?: article.editionId,
                    isFeatured = featured || article.isFeatured,
                    isEditorsChoice = editorsChoice || article.isEditorsChoice,
                    publishedAt = article.publishedAt ?: now,
                    updatedAt = now
                )
            )
            articleDao.insertRevision(
                ArticleRevisionEntity(
                    id = "rev-${UUID.randomUUID()}",
                    articleId = articleId,
                    title = article.title,
                    authorName = article.authorName,
                    body = article.body,
                    status = ArticleStatus.APPROVED.name,
                    note = "Approved by ${admin.fullName}",
                    revisedByUserId = admin.uid,
                    createdAt = now
                )
            )
            governanceDao.insertNotification(
                NotificationEntity(
                    id = "notif-app-${UUID.randomUUID()}",
                    recipientUserId = article.authorUserId,
                    titleBn = "আলহামদুলিল্লাহ! আপনার প্রবন্ধ অনুমোদিত হয়েছে",
                    titleEn = "Alhamdulillah! Your Article Was Approved",
                    messageBn = "'${article.title}' এখন মাসিক রাহাবার ম্যাগাজিনে প্রকাশিত হয়েছে।",
                    messageEn = "'${article.title}' is now published on Monthly Rahabar Magazine.",
                    type = "APPROVAL",
                    referenceId = articleId,
                    createdAt = now
                )
            )
            // Notify followers
            engagementDao.getFollowerIds(article.authorUserId).forEach { followerId ->
                governanceDao.insertNotification(
                    NotificationEntity(
                        id = "notif-fol-${UUID.randomUUID()}",
                        recipientUserId = followerId,
                        titleBn = "${article.authorName} নতুন লেখা প্রকাশ করেছেন",
                        titleEn = "${article.authorName} Published a New Article",
                        messageBn = "'${article.title}' পড়ুন।",
                        messageEn = "Read '${article.title}'.",
                        type = "FOLLOW_PUBLISH",
                        referenceId = articleId,
                        createdAt = now
                    )
                )
            }
            governanceDao.insertAuditLog(
                AuditLogEntity(
                    id = "log-${UUID.randomUUID()}",
                    actorUserId = admin.uid,
                    actorName = admin.fullName,
                    action = "APPROVE_ARTICLE",
                    targetType = "ARTICLE",
                    targetId = articleId,
                    details = "Approved '${article.title}'"
                )
            )
        }
        return Result.success(Unit)
    }

    suspend fun rejectArticle(articleId: String, reason: String, admin: UserEntity): Result<Unit> {
        if (!admin.hasPermission(AdminPermission.ARTICLE_APPROVAL)) {
            return Result.failure(SecurityException("Not authorized to reject articles"))
        }
        val cleanReason = reason.trim().ifBlank { "Editorial revision requested for compliance with Islamic literary standards." }
        val article = articleDao.getArticleById(articleId) ?: return Result.failure(IllegalStateException("Article not found"))
        val now = System.currentTimeMillis()
        db.withTransaction {
            articleDao.rejectArticle(articleId, cleanReason, now)
            articleDao.insertRevision(
                ArticleRevisionEntity(
                    id = "rev-${UUID.randomUUID()}",
                    articleId = articleId,
                    title = article.title,
                    authorName = article.authorName,
                    body = article.body,
                    status = ArticleStatus.REJECTED.name,
                    note = "Rejected: $cleanReason",
                    revisedByUserId = admin.uid,
                    createdAt = now
                )
            )
            governanceDao.insertNotification(
                NotificationEntity(
                    id = "notif-rej-${UUID.randomUUID()}",
                    recipientUserId = article.authorUserId,
                    titleBn = "প্রবন্ধ সংশোধনের অনুরোধ",
                    titleEn = "Article Revision Requested",
                    messageBn = "'${article.title}' প্রবন্ধটি সংশোধনের জন্য ফেরত পাঠানো হয়েছে: $cleanReason",
                    messageEn = "'${article.title}' needs revision: $cleanReason",
                    type = "REJECTION",
                    referenceId = articleId,
                    createdAt = now
                )
            )
            governanceDao.insertAuditLog(
                AuditLogEntity(
                    id = "log-${UUID.randomUUID()}",
                    actorUserId = admin.uid,
                    actorName = admin.fullName,
                    action = "REJECT_ARTICLE",
                    targetType = "ARTICLE",
                    targetId = articleId,
                    details = "Rejected '${article.title}': $cleanReason"
                )
            )
        }
        return Result.success(Unit)
    }

    suspend fun deleteArticleByAdmin(articleId: String, admin: UserEntity): Result<Unit> {
        if (!admin.hasPermission(AdminPermission.CONTENT_MODERATOR) && !admin.hasPermission(AdminPermission.ARTICLE_APPROVAL)) {
            return Result.failure(SecurityException("Not authorized to delete articles"))
        }
        val article = articleDao.getArticleById(articleId) ?: return Result.failure(IllegalStateException("Article not found"))
        articleDao.deleteArticle(articleId)
        governanceDao.insertAuditLog(
            AuditLogEntity(
                id = "log-${UUID.randomUUID()}",
                actorUserId = admin.uid,
                actorName = admin.fullName,
                action = "DELETE_ARTICLE",
                targetType = "ARTICLE",
                targetId = articleId,
                details = "Deleted article '${article.title}'"
            )
        )
        return Result.success(Unit)
    }

    suspend fun findUserForAdmin(query: String): List<UserEntity> =
        if (query.isBlank()) emptyList() else userDao.searchUsers(query.trim())

    suspend fun appointOrUpdateAdmin(targetUid: String, permissions: Set<AdminPermission>, superAdmin: UserEntity): Result<Unit> {
        if (superAdmin.parsedRole() != UserRole.SUPER_ADMIN && !superAdmin.isDeveloperProtected) {
            return Result.failure(SecurityException("Only Developer Super Admin Ahnaf Salman can manage administrator roles."))
        }
        val target = userDao.getUserByUid(targetUid) ?: return Result.failure(IllegalStateException("User not found"))
        if (target.isDeveloperProtected) {
            return Result.failure(SecurityException("Developer Super Admin privileges cannot be modified."))
        }
        val role = if (permissions.isEmpty()) UserRole.USER.name else UserRole.ADMIN.name
        val permStr = permissions.joinToString(",") { it.name }
        userDao.updateAdminRole(targetUid, role, permStr)
        governanceDao.insertNotification(
            NotificationEntity(
                id = "notif-role-${UUID.randomUUID()}",
                recipientUserId = targetUid,
                titleBn = if (permissions.isEmpty()) "অ্যাডমিন দায়িত্ব পরিবর্তন" else "অ্যাডমিন দায়িত্ব প্রদান",
                titleEn = if (permissions.isEmpty()) "Admin Role Updated" else "Appointed as Administrator",
                messageBn = if (permissions.isEmpty()) "আপনার অ্যাডমিন অনুমতি প্রত্যাহার করা হয়েছে।" else "সুপার অ্যাডমিন আহনাফ সালমান আপনাকে সম্পাদকীয় অ্যাডমিন দায়িত্ব প্রদান করেছেন।",
                messageEn = if (permissions.isEmpty()) "Your admin permissions were revoked." else "Super Admin Ahnaf Salman appointed you as an Administrator.",
                type = "ADMIN_ROLE"
            )
        )
        governanceDao.insertAuditLog(
            AuditLogEntity(
                id = "log-${UUID.randomUUID()}",
                actorUserId = superAdmin.uid,
                actorName = superAdmin.fullName,
                action = "UPDATE_ADMIN_ROLE",
                targetType = "USER",
                targetId = target.userId,
                details = "Set role=$role permissions=[$permStr] for ${target.username}"
            )
        )
        return Result.success(Unit)
    }

    suspend fun setUserSuspended(targetUid: String, suspend: Boolean, admin: UserEntity): Result<Unit> {
        if (!admin.hasPermission(AdminPermission.USER_MANAGEMENT)) {
            return Result.failure(SecurityException("Not authorized to manage users"))
        }
        val target = userDao.getUserByUid(targetUid) ?: return Result.failure(IllegalStateException("User not found"))
        if (target.isDeveloperProtected || target.parsedRole() == UserRole.SUPER_ADMIN) {
            return Result.failure(SecurityException("Protected Super Admin account cannot be suspended."))
        }
        userDao.setUserSuspended(targetUid, suspend)
        governanceDao.insertAuditLog(
            AuditLogEntity(
                id = "log-${UUID.randomUUID()}",
                actorUserId = admin.uid,
                actorName = admin.fullName,
                action = if (suspend) "SUSPEND_USER" else "REACTIVATE_USER",
                targetType = "USER",
                targetId = target.userId,
                details = "${if (suspend) "Suspended" else "Reactivated"} ${target.username}"
            )
        )
        return Result.success(Unit)
    }

    suspend fun deleteUserAccount(targetUid: String, superAdmin: UserEntity): Result<Unit> {
        if (superAdmin.parsedRole() != UserRole.SUPER_ADMIN && !superAdmin.isDeveloperProtected) {
            return Result.failure(SecurityException("Only Developer Super Admin can permanently delete accounts."))
        }
        val target = userDao.getUserByUid(targetUid) ?: return Result.failure(IllegalStateException("User not found"))
        if (target.isDeveloperProtected || target.parsedRole() == UserRole.SUPER_ADMIN) {
            return Result.failure(SecurityException("Protected Super Admin account cannot be deleted."))
        }
        db.withTransaction {
            articleDao.deleteArticlesByAuthor(targetUid)
            userDao.deleteUserProtected(targetUid)
            governanceDao.insertAuditLog(
                AuditLogEntity(
                    id = "log-${UUID.randomUUID()}",
                    actorUserId = superAdmin.uid,
                    actorName = superAdmin.fullName,
                    action = "DELETE_USER",
                    targetType = "USER",
                    targetId = target.userId,
                    details = "Permanently deleted user ${target.fullName} (@${target.username}, ${target.userId})"
                )
            )
        }
        return Result.success(Unit)
    }

    suspend fun createOrPublishEdition(edition: EditionEntity, admin: UserEntity): Result<Unit> {
        if (!admin.isAnyAdmin()) return Result.failure(SecurityException("Admin access required"))
        editionDao.upsertEdition(edition)
        if (edition.isPublished) {
            governanceDao.insertNotification(
                NotificationEntity(
                    id = "notif-ed-${UUID.randomUUID()}",
                    recipientUserId = "ALL",
                    titleBn = "নতুন সংখ্যা প্রকাশিত: ${edition.titleBn}",
                    titleEn = "New Edition Published: ${edition.titleEn}",
                    messageBn = edition.descriptionBn,
                    messageEn = edition.descriptionEn,
                    type = "EDITION",
                    referenceId = edition.id
                )
            )
        }
        governanceDao.insertAuditLog(
            AuditLogEntity(
                id = "log-${UUID.randomUUID()}",
                actorUserId = admin.uid,
                actorName = admin.fullName,
                action = "UPSERT_EDITION",
                targetType = "EDITION",
                targetId = edition.id,
                details = "Saved edition ${edition.titleEn} (published=${edition.isPublished})"
            )
        )
        return Result.success(Unit)
    }

    suspend fun upsertCategory(category: CategoryEntity, admin: UserEntity): Result<Unit> {
        if (!admin.isAnyAdmin()) return Result.failure(SecurityException("Admin access required"))
        categoryDao.upsertCategory(category)
        governanceDao.insertAuditLog(
            AuditLogEntity(
                id = "log-${UUID.randomUUID()}",
                actorUserId = admin.uid,
                actorName = admin.fullName,
                action = "UPSERT_CATEGORY",
                targetType = "CATEGORY",
                targetId = category.slug,
                details = "Saved category ${category.nameEn} / ${category.nameBn}"
            )
        )
        return Result.success(Unit)
    }

    suspend fun broadcastAnnouncement(titleBn: String, titleEn: String, bodyBn: String, bodyEn: String, admin: UserEntity): Result<Unit> {
        if (!admin.hasPermission(AdminPermission.NOTIFICATION_ADMIN)) {
            return Result.failure(SecurityException("Not authorized to send announcements"))
        }
        val ann = AnnouncementEntity(
            id = "ann-${UUID.randomUUID()}",
            titleBn = titleBn.trim(),
            titleEn = titleEn.trim(),
            bodyBn = bodyBn.trim(),
            bodyEn = bodyEn.trim(),
            createdByUserId = admin.uid
        )
        governanceDao.insertAnnouncement(ann)
        governanceDao.insertNotification(
            NotificationEntity(
                id = "notif-ann-${UUID.randomUUID()}",
                recipientUserId = "ALL",
                titleBn = titleBn.trim(),
                titleEn = titleEn.trim(),
                messageBn = bodyBn.trim(),
                messageEn = bodyEn.trim(),
                type = "ANNOUNCEMENT",
                referenceId = ann.id
            )
        )
        governanceDao.insertAuditLog(
            AuditLogEntity(
                id = "log-${UUID.randomUUID()}",
                actorUserId = admin.uid,
                actorName = admin.fullName,
                action = "BROADCAST_ANNOUNCEMENT",
                targetType = "ANNOUNCEMENT",
                targetId = ann.id,
                details = "Sent announcement: $titleEn"
            )
        )
        return Result.success(Unit)
    }

    suspend fun resolveReport(report: ReportEntity, dismiss: Boolean, hideTarget: Boolean, admin: UserEntity): Result<Unit> {
        if (!admin.hasPermission(AdminPermission.CONTENT_MODERATOR)) {
            return Result.failure(SecurityException("Not authorized to moderate reports"))
        }
        if (hideTarget && !dismiss) {
            if (report.targetType == "COMMENT") engagementDao.hideComment(report.targetId)
            else if (report.targetType == "ARTICLE") articleDao.rejectArticle(report.targetId, "Removed after moderation review.", System.currentTimeMillis())
        }
        governanceDao.updateReportStatus(report.id, if (dismiss) "DISMISSED" else "RESOLVED", admin.fullName)
        governanceDao.insertAuditLog(
            AuditLogEntity(
                id = "log-${UUID.randomUUID()}",
                actorUserId = admin.uid,
                actorName = admin.fullName,
                action = "RESOLVE_REPORT",
                targetType = report.targetType,
                targetId = report.targetId,
                details = "Status=${if (dismiss) "DISMISSED" else "RESOLVED"} hideTarget=$hideTarget"
            )
        )
        return Result.success(Unit)
    }

    suspend fun submitFeedback(uid: String, type: String, subject: String, message: String) {
        governanceDao.insertFeedback(
            SupportFeedbackEntity(
                id = "fb-${UUID.randomUUID()}",
                userId = uid,
                type = type,
                subject = subject.trim(),
                message = message.trim()
            )
        )
    }
}
