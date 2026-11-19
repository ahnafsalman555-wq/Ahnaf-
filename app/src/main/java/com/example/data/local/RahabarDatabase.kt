package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        EditionEntity::class,
        ArticleEntity::class,
        ArticleRevisionEntity::class,
        ArticleLikeEntity::class,
        ArticleViewLogEntity::class,
        FollowEntity::class,
        SavedArticleEntity::class,
        BookmarkEntity::class,
        PersonalNoteEntity::class,
        CollectionEntity::class,
        ReadingProgressEntity::class,
        CommentEntity::class,
        ReportEntity::class,
        NotificationEntity::class,
        AnnouncementEntity::class,
        AuditLogEntity::class,
        SupportFeedbackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RahabarDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun editionDao(): EditionDao
    abstract fun articleDao(): ArticleDao
    abstract fun engagementDao(): EngagementDao
    abstract fun governanceDao(): GovernanceDao

    companion object {
        @Volatile
        private var INSTANCE: RahabarDatabase? = null

        fun getInstance(context: Context): RahabarDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RahabarDatabase::class.java,
                    "rahabar_magazine.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                seedInitialData(database)
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        suspend fun seedInitialData(database: RahabarDatabase) {
            database.categoryDao().insertAllIgnore(SeedDataProvider.defaultCategories)
            database.userDao().insertUsersIgnore(SeedDataProvider.buildSeedUsers())
            database.editionDao().insertAllIgnore(SeedDataProvider.buildSeedEditions())
            database.articleDao().insertAllIgnore(SeedDataProvider.buildSeedArticles())
            database.governanceDao().insertNotificationsIgnore(SeedDataProvider.buildSeedNotifications())
        }
    }
}
