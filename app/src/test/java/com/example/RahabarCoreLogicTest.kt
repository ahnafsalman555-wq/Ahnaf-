package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AdminPermission
import com.example.data.local.ArticleStatus
import com.example.data.local.RahabarDatabase
import com.example.data.local.SeedDataProvider
import com.example.data.local.UserPreferencesDataStore
import com.example.data.repository.RahabarRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RahabarCoreLogicTest {
    private lateinit var db: RahabarDatabase
    private lateinit var repository: RahabarRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RahabarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = UserPreferencesDataStore(context)
        repository = RahabarRepository(db, prefs)
        runBlocking { repository.ensureSeeded() }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `permanent user id and duplicate username rejection work`() = runBlocking {
        val reg1 = repository.registerUser("Tariqul Islam", "tariqul_bd", "tariqul@example.com", "secret123", "bn")
        assertTrue(reg1.isSuccess)
        val user1 = reg1.getOrThrow()
        assertEquals("RHM-100004", user1.userId)
        assertEquals(100004L, user1.userNumber)

        val available = repository.isUsernameAvailable("TARIQUL_BD")
        assertFalse(available)

        val regDup = repository.registerUser("Another Person", "tariqul_bd", "other@example.com", "secret123", "bn")
        assertTrue(regDup.isFailure)
    }

    @Test
    fun `three mandatory article fields and approval workflow are enforced`() = runBlocking {
        val author = repository.registerUser("Nadia Karim", "nadia_k", "nadia@example.com", "pass1234", "bn").getOrThrow()

        // Missing title fails
        val missingTitle = repository.submitOrSaveArticle(
            null, "", author.fullName, "Body text", "", "", "islamic_articles", "bn", "", null, 0, true, author
        )
        assertTrue(missingTitle.isFailure)

        // Valid 3 fields enters PENDING status
        val submitted = repository.submitOrSaveArticle(
            null, "জীবনের প্রকৃত সফলতা", author.fullName, "মূল লেখা এখানে।", "", "", "islamic_articles", "bn", "সফলতা", null, 0, true, author
        ).getOrThrow()
        assertEquals(ArticleStatus.PENDING.name, submitted.status)

        // Super Admin approves
        val superAdmin = repository.signInAsDeveloperSuperAdmin().getOrThrow()
        val approveRes = repository.approveArticle(submitted.id, superAdmin)
        assertTrue(approveRes.isSuccess)

        val approvedList = repository.observeApprovedArticles().first()
        assertTrue(approvedList.any { it.id == submitted.id })
    }

    @Test
    fun `super admin ahnaf salman cannot be suspended or deleted`() = runBlocking {
        val superAdmin = repository.signInAsDeveloperSuperAdmin().getOrThrow()
        assertEquals(SeedDataProvider.SUPER_ADMIN_PERMANENT_ID, superAdmin.userId)
        assertEquals(SeedDataProvider.SUPER_ADMIN_EMAIL, superAdmin.email)

        val other = repository.registerUser("Mod User", "mod_user", "mod@example.com", "pass1234", "en").getOrThrow()
        repository.appointOrUpdateAdmin(other.uid, setOf(AdminPermission.USER_MANAGEMENT), superAdmin)
        val updatedMod = db.userDao().getUserByUid(other.uid)!!

        val suspendAttempt = repository.setUserSuspended(superAdmin.uid, true, updatedMod)
        assertTrue(suspendAttempt.isFailure)

        val deleteAttempt = repository.deleteUserAccount(superAdmin.uid, superAdmin)
        assertTrue(deleteAttempt.isFailure)
    }

    @Test
    fun `likes are single per user and views are deduplicated`() = runBlocking {
        val user = repository.registerUser("Reader One", "reader_one", "r1@example.com", "pass1234", "bn").getOrThrow()
        val articleId = "art-success-in-life"
        val before = db.articleDao().getArticleById(articleId)!!

        repository.recordGenuineView(articleId, user.uid)
        repository.recordGenuineView(articleId, user.uid)
        val afterView = db.articleDao().getArticleById(articleId)!!
        assertEquals(before.viewsCount + 1, afterView.viewsCount)

        val liked = repository.toggleLike(articleId, user.uid)
        assertTrue(liked)
        assertEquals(before.likesCount + 1, db.articleDao().getArticleById(articleId)!!.likesCount)

        val unliked = repository.toggleLike(articleId, user.uid)
        assertFalse(unliked)
        assertEquals(before.likesCount, db.articleDao().getArticleById(articleId)!!.likesCount)
    }
}
