package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM profiles ORDER BY userNumber ASC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM profiles WHERE uid = :uid LIMIT 1")
    fun observeUserByUid(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM profiles WHERE uid = :uid LIMIT 1")
    suspend fun getUserByUid(uid: String): UserEntity?

    @Query("SELECT * FROM profiles WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM profiles WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM profiles WHERE UPPER(userId) = UPPER(:userId) LIMIT 1")
    suspend fun getUserByPermanentId(userId: String): UserEntity?

    @Query(
        """
        SELECT * FROM profiles
        WHERE LOWER(userId) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(username) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(fullName) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(email) LIKE '%' || LOWER(:query) || '%'
        ORDER BY userNumber ASC
        """
    )
    suspend fun searchUsers(query: String): List<UserEntity>

    @Query("SELECT COALESCE(MAX(userNumber), 100000) FROM profiles")
    suspend fun getMaxUserNumber(): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsersIgnore(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE profiles SET preferredLanguage = :language WHERE uid = :uid")
    suspend fun updatePreferredLanguage(uid: String, language: String)

    @Query("UPDATE profiles SET isSuspended = :suspended WHERE uid = :uid AND isDeveloperProtected = 0")
    suspend fun setUserSuspended(uid: String, suspended: Boolean): Int

    @Query("UPDATE profiles SET role = :role, adminPermissions = :permissions WHERE uid = :uid AND isDeveloperProtected = 0")
    suspend fun updateAdminRole(uid: String, role: String, permissions: String): Int

    @Query("UPDATE profiles SET passwordHash = :hash, salt = :salt WHERE uid = :uid")
    suspend fun updatePassword(uid: String, hash: String, salt: String)

    @Query("DELETE FROM profiles WHERE uid = :uid AND isDeveloperProtected = 0")
    suspend fun deleteUserProtected(uid: String): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE slug = :slug")
    suspend fun deleteCategory(slug: String)
}

@Dao
interface EditionDao {
    @Query("SELECT * FROM monthly_editions ORDER BY year DESC, monthNumber DESC, publishedAt DESC")
    fun observeAllEditions(): Flow<List<EditionEntity>>

    @Query("SELECT * FROM monthly_editions WHERE isPublished = 1 ORDER BY year DESC, monthNumber DESC, publishedAt DESC")
    fun observePublishedEditions(): Flow<List<EditionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEdition(edition: EditionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(editions: List<EditionEntity>)

    @Query("UPDATE monthly_editions SET isPublished = :published WHERE id = :editionId")
    suspend fun setPublished(editionId: String, published: Boolean)
}

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles WHERE status = 'APPROVED' ORDER BY publishedAt DESC, createdAt DESC")
    fun observeApprovedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles ORDER BY updatedAt DESC")
    fun observeAllArticlesForAdmin(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE status = 'PENDING' ORDER BY updatedAt ASC")
    fun observePendingArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE authorUserId = :uid ORDER BY updatedAt DESC")
    fun observeArticlesByAuthor(uid: String): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :articleId LIMIT 1")
    fun observeArticleById(articleId: String): Flow<ArticleEntity?>

    @Query("SELECT * FROM articles WHERE id = :articleId LIMIT 1")
    suspend fun getArticleById(articleId: String): ArticleEntity?

    @Query("SELECT * FROM articles WHERE isDownloadedOffline = 1 AND status = 'APPROVED' ORDER BY updatedAt DESC")
    fun observeOfflineArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE editionId = :editionId AND status = 'APPROVED' ORDER BY publishedAt DESC")
    fun observeArticlesInEdition(editionId: String): Flow<List<ArticleEntity>>

    @Query(
        """
        SELECT * FROM articles
        WHERE status = 'APPROVED'
          AND (:language = 'ALL' OR language = :language)
          AND (:categorySlug = 'ALL' OR categorySlug = :categorySlug)
          AND (
              :query = ''
              OR title LIKE '%' || :query || '%'
              OR subtitle LIKE '%' || :query || '%'
              OR authorName LIKE '%' || :query || '%'
              OR LOWER(authorUsername) LIKE '%' || LOWER(:query) || '%'
              OR UPPER(authorPermanentId) LIKE '%' || UPPER(:query) || '%'
              OR tags LIKE '%' || :query || '%'
              OR body LIKE '%' || :query || '%'
              OR categorySlug LIKE '%' || LOWER(:query) || '%'
          )
        ORDER BY
          CASE WHEN LOWER(title) = LOWER(:query) THEN 0
               WHEN title LIKE '%' || :query || '%' THEN 1
               ELSE 2 END,
          publishedAt DESC
        """
    )
    fun searchApprovedArticles(
        query: String,
        language: String,
        categorySlug: String
    ): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticle(article: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(articles: List<ArticleEntity>)

    @Query("UPDATE articles SET viewsCount = viewsCount + 1 WHERE id = :articleId")
    suspend fun incrementViewsCount(articleId: String)

    @Query("UPDATE articles SET likesCount = MAX(0, likesCount + :delta) WHERE id = :articleId")
    suspend fun adjustLikesCount(articleId: String, delta: Long)

    @Query("UPDATE articles SET isDownloadedOffline = :downloaded WHERE id = :articleId")
    suspend fun setOfflineDownloaded(articleId: String, downloaded: Boolean)

    @Query("UPDATE articles SET status = 'APPROVED', rejectionReason = NULL, publishedAt = :publishedAt, updatedAt = :publishedAt WHERE id = :articleId")
    suspend fun approveArticle(articleId: String, publishedAt: Long)

    @Query("UPDATE articles SET status = 'REJECTED', rejectionReason = :reason, updatedAt = :updatedAt WHERE id = :articleId")
    suspend fun rejectArticle(articleId: String, reason: String, updatedAt: Long)

    @Query("UPDATE articles SET editionId = :editionId WHERE id = :articleId")
    suspend fun assignToEdition(articleId: String, editionId: String?)

    @Query("DELETE FROM articles WHERE id = :articleId")
    suspend fun deleteArticle(articleId: String)

    @Query("DELETE FROM articles WHERE authorUserId = :uid")
    suspend fun deleteArticlesByAuthor(uid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(revision: ArticleRevisionEntity)

    @Query("SELECT * FROM article_revisions WHERE articleId = :articleId ORDER BY createdAt DESC")
    fun observeRevisions(articleId: String): Flow<List<ArticleRevisionEntity>>
}

@Dao
interface EngagementDao {
    // Likes
    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE articleId = :articleId AND userId = :userId)")
    fun observeIsLiked(articleId: String, userId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE articleId = :articleId AND userId = :userId)")
    suspend fun isLiked(articleId: String, userId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLike(like: ArticleLikeEntity): Long

    @Query("DELETE FROM likes WHERE articleId = :articleId AND userId = :userId")
    suspend fun deleteLike(articleId: String, userId: String): Int

    // Views (Deduplicated)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertViewLog(viewLog: ArticleViewLogEntity): Long

    // Follows
    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerUserId = :followerId AND authorUserId = :authorId)")
    fun observeIsFollowing(followerId: String, authorId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM follows WHERE authorUserId = :authorId")
    fun observeFollowersCount(authorId: String): Flow<Int>

    @Query("SELECT followerUserId FROM follows WHERE authorUserId = :authorId")
    suspend fun getFollowerIds(authorId: String): List<String>

    @Query("SELECT authorUserId FROM follows WHERE followerUserId = :followerId")
    fun observeFollowedAuthorIds(followerId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun followAuthor(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerUserId = :followerId AND authorUserId = :authorId")
    suspend fun unfollowAuthor(followerId: String, authorId: String)

    // Saved Articles
    @Query("SELECT EXISTS(SELECT 1 FROM saved_articles WHERE userId = :userId AND articleId = :articleId)")
    fun observeIsSaved(userId: String, articleId: String): Flow<Boolean>

    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN saved_articles s ON a.id = s.articleId
        WHERE s.userId = :userId
        ORDER BY s.createdAt DESC
        """
    )
    fun observeSavedArticles(userId: String): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(saved: SavedArticleEntity)

    @Query("DELETE FROM saved_articles WHERE userId = :userId AND articleId = :articleId")
    suspend fun removeSavedArticle(userId: String, articleId: String)

    // Paragraph Bookmarks
    @Query("SELECT * FROM bookmarks WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeBookmarks(userId: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun deleteBookmark(bookmarkId: String)

    // Personal Notes
    @Query("SELECT * FROM personal_notes WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeNotes(userId: String): Flow<List<PersonalNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: PersonalNoteEntity)

    @Query("DELETE FROM personal_notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)

    // Collections
    @Query("SELECT * FROM collections WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeCollections(userId: String): Flow<List<CollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity)

    // Reading Progress & History
    @Query("SELECT * FROM reading_progress WHERE userId = :userId AND articleId = :articleId LIMIT 1")
    suspend fun getProgress(userId: String, articleId: String): ReadingProgressEntity?

    @Query("SELECT * FROM reading_progress WHERE userId = :userId ORDER BY lastReadAt DESC")
    fun observeReadingHistory(userId: String): Flow<List<ReadingProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ReadingProgressEntity)

    // Comments
    @Query("SELECT * FROM comments WHERE articleId = :articleId AND isHidden = 0 ORDER BY createdAt ASC")
    fun observeCommentsForArticle(articleId: String): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments")
    fun observeTotalCommentsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("UPDATE comments SET isHidden = 1 WHERE id = :commentId")
    suspend fun hideComment(commentId: String)
}

@Dao
interface GovernanceDao {
    // Notifications
    @Query(
        """
        SELECT * FROM notifications
        WHERE recipientUserId = :uid OR recipientUserId = 'ALL' OR (:isAdmin = 1 AND recipientUserId = 'ADMINS')
        ORDER BY createdAt DESC
        """
    )
    fun observeNotificationsForUser(uid: String, isAdmin: Boolean): Flow<List<NotificationEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM notifications
        WHERE isRead = 0 AND (recipientUserId = :uid OR recipientUserId = 'ALL' OR (:isAdmin = 1 AND recipientUserId = 'ADMINS'))
        """
    )
    fun observeUnreadCount(uid: String, isAdmin: Boolean): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotificationsIgnore(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query(
        """
        UPDATE notifications SET isRead = 1
        WHERE recipientUserId = :uid OR recipientUserId = 'ALL' OR (:isAdmin = 1 AND recipientUserId = 'ADMINS')
        """
    )
    suspend fun markAllRead(uid: String, isAdmin: Boolean)

    // Reports
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun observeAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET status = :status, resolvedBy = :resolvedBy WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: String, status: String, resolvedBy: String)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY createdAt DESC")
    fun observeAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY createdAt DESC LIMIT 150")
    fun observeAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Support Feedback
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: SupportFeedbackEntity)
}
