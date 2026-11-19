package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole {
    USER,
    ADMIN,
    SUPER_ADMIN
}

enum class AdminPermission {
    ARTICLE_APPROVAL,
    CONTENT_MODERATOR,
    NOTIFICATION_ADMIN,
    USER_MANAGEMENT,
    GENERAL_ADMIN
}

enum class ArticleStatus {
    DRAFT,
    PENDING,
    APPROVED,
    REJECTED
}

enum class EditionType {
    MONTHLY,
    WEEKLY,
    SPECIAL
}

@Entity(
    tableName = "profiles",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true),
        Index(value = ["userId"], unique = true),
        Index(value = ["userNumber"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey val uid: String,
    val userId: String,                 // Immutable e.g. RHM-100001
    val userNumber: Long,               // Immutable e.g. 100001
    val username: String,               // Unique lowercase handle
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val avatarUri: String? = null,
    val preferredLanguage: String = "bn",
    val role: String = UserRole.USER.name,
    val adminPermissions: String = "",  // Comma-separated AdminPermission names
    val isSuspended: Boolean = false,
    val isDeveloperProtected: Boolean = false,
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun parsedRole(): UserRole = UserRole.entries.firstOrNull { it.name == role } ?: UserRole.USER

    fun parsedPermissions(): Set<AdminPermission> = adminPermissions
        .split(",")
        .mapNotNull { token -> AdminPermission.entries.firstOrNull { it.name == token.trim() } }
        .toSet()

    fun hasPermission(permission: AdminPermission): Boolean {
        if (parsedRole() == UserRole.SUPER_ADMIN || isDeveloperProtected) return true
        if (parsedRole() != UserRole.ADMIN) return false
        val perms = parsedPermissions()
        return perms.contains(permission) || perms.contains(AdminPermission.GENERAL_ADMIN)
    }

    fun isAnyAdmin(): Boolean =
        parsedRole() == UserRole.SUPER_ADMIN || parsedRole() == UserRole.ADMIN || isDeveloperProtected
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val slug: String,
    val nameBn: String,
    val nameEn: String,
    val sortOrder: Int,
    val iconKey: String = "menu_book"
)

@Entity(tableName = "monthly_editions")
data class EditionEntity(
    @PrimaryKey val id: String,
    val titleBn: String,
    val titleEn: String,
    val editionType: String = EditionType.MONTHLY.name,
    val monthNumber: Int,
    val year: Int,
    val descriptionBn: String,
    val descriptionEn: String,
    val coverColorHex: String = "#14243A",
    val isPublished: Boolean = true,
    val publishedAt: Long = System.currentTimeMillis(),
    val scheduledFor: Long? = null
)

@Entity(
    tableName = "articles",
    indices = [
        Index(value = ["status"]),
        Index(value = ["categorySlug"]),
        Index(value = ["language"]),
        Index(value = ["authorUserId"]),
        Index(value = ["editionId"])
    ]
)
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String = "",
    val authorUserId: String,
    val authorName: String,
    val authorUsername: String,
    val authorPermanentId: String,
    val body: String,
    val publicationDescription: String = "",
    val language: String = "bn",
    val categorySlug: String,
    val tags: String = "",
    val editionId: String? = null,
    val coverUri: String? = null,
    val coverThemeIndex: Int = 0,
    val status: String = ArticleStatus.PENDING.name,
    val rejectionReason: String? = null,
    val isFeatured: Boolean = false,
    val isEditorsChoice: Boolean = false,
    val viewsCount: Long = 0,
    val likesCount: Long = 0,
    val readingTimeMinutes: Int = 3,
    val isDownloadedOffline: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val publishedAt: Long? = null
) {
    fun parsedStatus(): ArticleStatus =
        ArticleStatus.entries.firstOrNull { it.name == status } ?: ArticleStatus.PENDING
}

@Entity(tableName = "article_revisions")
data class ArticleRevisionEntity(
    @PrimaryKey val id: String,
    val articleId: String,
    val title: String,
    val authorName: String,
    val body: String,
    val status: String,
    val note: String,
    val revisedByUserId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "likes",
    primaryKeys = ["articleId", "userId"]
)
data class ArticleLikeEntity(
    val articleId: String,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "article_views",
    primaryKeys = ["articleId", "viewerKey"]
)
data class ArticleViewLogEntity(
    val articleId: String,
    val viewerKey: String,
    val viewedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "follows",
    primaryKeys = ["followerUserId", "authorUserId"]
)
data class FollowEntity(
    val followerUserId: String,
    val authorUserId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "saved_articles",
    primaryKeys = ["userId", "articleId"]
)
data class SavedArticleEntity(
    val userId: String,
    val articleId: String,
    val collectionName: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val articleId: String,
    val articleTitle: String,
    val paragraphIndex: Int,
    val excerpt: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_notes")
data class PersonalNoteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val articleId: String,
    val articleTitle: String,
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "collections",
    primaryKeys = ["userId", "name"]
)
data class CollectionEntity(
    val userId: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reading_progress",
    primaryKeys = ["userId", "articleId"]
)
data class ReadingProgressEntity(
    val userId: String,
    val articleId: String,
    val paragraphIndex: Int,
    val progressPercent: Int,
    val lastReadAt: Long = System.currentTimeMillis(),
    val isPendingSync: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val articleId: String,
    val parentCommentId: String? = null,
    val userId: String,
    val authorName: String,
    val authorUsername: String,
    val content: String,
    val isReported: Boolean = false,
    val isHidden: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val targetType: String, // ARTICLE or COMMENT
    val targetId: String,
    val targetPreview: String,
    val reporterUserId: String,
    val reason: String,
    val status: String = "OPEN", // OPEN, RESOLVED, DISMISSED
    val resolvedBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientUserId: String, // "ALL", "ADMINS", or specific uid
    val titleBn: String,
    val titleEn: String,
    val messageBn: String,
    val messageEn: String,
    val type: String,
    val referenceId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val titleBn: String,
    val titleEn: String,
    val bodyBn: String,
    val bodyEn: String,
    val createdByUserId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actorUserId: String,
    val actorName: String,
    val action: String,
    val targetType: String,
    val targetId: String,
    val details: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_feedback")
data class SupportFeedbackEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // PROBLEM or FEEDBACK
    val subject: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
)
