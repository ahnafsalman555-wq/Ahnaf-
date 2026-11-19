package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.ArticleEntity
import com.example.data.local.ReadingProgressEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.RahabarTopHeader
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ArticleReadingScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.AuthorProfileScreen
import com.example.ui.screens.EditionDetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileAndSettingsScreen
import com.example.ui.screens.QuranRecitationScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WriteArticleScreen
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.RahabarTheme
import com.example.ui.theme.WarmCream
import com.example.ui.viewmodel.MainViewModel

private object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val WRITE = "write"
    const val LIBRARY = "library"
    const val PROFILE = "profile"
    const val ARTICLE = "article/{articleId}"
    const val QURAN = "quran"
    const val EDITION = "edition/{editionId}"
    const val AUTHOR = "author/{authorUid}"
    const val NOTIFICATIONS = "notifications"
    const val ADMIN = "admin"

    fun article(id: String) = "article/$id"
    fun edition(id: String) = "edition/$id"
    fun author(uid: String) = "author/$uid"
}

private data class BottomNavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as RahabarApplication

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(app.repository, app.quranAudioPlayer)
            )
            val preferences by viewModel.preferences.collectAsStateWithLifecycle()
            val language by viewModel.appLanguage.collectAsStateWithLifecycle()

            RahabarTheme(
                darkTheme = preferences.darkMode,
                isBengali = language == AppLanguage.BN
            ) {
                RahabarAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RahabarAppRoot(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val publishedEditions by viewModel.publishedEditions.collectAsStateWithLifecycle()
    val allEditions by viewModel.allEditions.collectAsStateWithLifecycle()
    val approvedArticles by viewModel.approvedArticles.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val unreadNotifications by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val savedArticles by viewModel.savedArticles.collectAsStateWithLifecycle()
    val offlineArticles by viewModel.offlineArticles.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val personalNotes by viewModel.personalNotes.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val readingHistory by viewModel.readingHistory.collectAsStateWithLifecycle()
    val myArticles by viewModel.myArticles.collectAsStateWithLifecycle()
    val followedAuthorIds by viewModel.followedAuthorIds.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterLanguage by viewModel.filterLanguage.collectAsStateWithLifecycle()
    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val recitationState by viewModel.recitationState.collectAsStateWithLifecycle()
    val allArticlesForAdmin by viewModel.allArticlesForAdmin.collectAsStateWithLifecycle()
    val pendingArticles by viewModel.pendingArticles.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val totalCommentsCount by viewModel.totalCommentsCount.collectAsStateWithLifecycle()

    var editingArticle by remember { mutableStateOf<ArticleEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val strings = AppStrings.get(language)
    val mainTabs = listOf(
        BottomNavTab(Routes.HOME, strings.navHome, Icons.Rounded.Home, Icons.Outlined.Home, "nav_home"),
        BottomNavTab(Routes.EXPLORE, strings.navExplore, Icons.Rounded.Explore, Icons.Outlined.Explore, "nav_explore"),
        BottomNavTab(Routes.WRITE, strings.navWrite, Icons.Rounded.Create, Icons.Outlined.Create, "nav_write"),
        BottomNavTab(Routes.LIBRARY, strings.navLibrary, Icons.Rounded.AutoStories, Icons.Outlined.AutoStories, "nav_library"),
        BottomNavTab(Routes.PROFILE, strings.navProfile, Icons.Rounded.Person, Icons.Outlined.Person, "nav_profile")
    )
    val isMainTabRoute = mainTabs.any { it.route == currentRoute }

    val navigateToTab: (String) -> Unit = { route ->
        if (route == Routes.WRITE && currentRoute != Routes.WRITE) {
            editingArticle = null
        }
        navController.navigate(route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (isMainTabRoute) {
                    Box(modifier = Modifier.statusBarsPadding()) {
                        RahabarTopHeader(
                            language = language,
                            unreadNotifications = unreadNotifications,
                            onSearchClick = { navigateToTab(Routes.EXPLORE) },
                            onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                            onLanguageToggle = { viewModel.toggleAppLanguage() }
                        )
                    }
                }
            },
            bottomBar = {
                if (isMainTabRoute && !useRail) {
                    NavigationBar(
                        containerColor = IslamicNavyDeep,
                        contentColor = WarmCream,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        mainTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label, maxLines = 1) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IslamicNavyDeep,
                                    selectedTextColor = IslamicGold,
                                    indicatorColor = IslamicGold,
                                    unselectedIconColor = WarmCream.copy(alpha = 0.88f),
                                    unselectedTextColor = WarmCream.copy(alpha = 0.88f)
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isMainTabRoute && useRail) {
                    NavigationRail(
                        containerColor = IslamicNavyDeep,
                        contentColor = WarmCream
                    ) {
                        mainTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = IslamicNavyDeep,
                                    selectedTextColor = IslamicGold,
                                    indicatorColor = IslamicGold,
                                    unselectedIconColor = WarmCream.copy(alpha = 0.88f),
                                    unselectedTextColor = WarmCream.copy(alpha = 0.88f)
                                )
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.SPLASH,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(Routes.SPLASH) {
                            SplashScreen(
                                language = language,
                                onFinished = {
                                    val dest = if (currentUser != null || preferences.isGuestMode) Routes.HOME else Routes.AUTH
                                    navController.navigate(dest) {
                                        popUpTo(Routes.SPLASH) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Routes.AUTH) {
                            AuthScreen(
                                language = language,
                                onToggleLanguage = { viewModel.toggleAppLanguage() },
                                onLogin = { id, pass ->
                                    viewModel.login(id, pass) { ok ->
                                        if (ok) {
                                            navController.navigate(Routes.HOME) {
                                                popUpTo(Routes.AUTH) { inclusive = true }
                                            }
                                        }
                                    }
                                },
                                onRegister = { name, username, email, pass ->
                                    viewModel.register(name, username, email, pass) { ok ->
                                        if (ok) {
                                            navController.navigate(Routes.HOME) {
                                                popUpTo(Routes.AUTH) { inclusive = true }
                                            }
                                        }
                                    }
                                },
                                onCheckUsername = { viewModel.checkUsernameAvailable(it) },
                                onResetPassword = { email, user, pass ->
                                    viewModel.resetPassword(email, user, pass)
                                },
                                onSuperAdminSignIn = {
                                    viewModel.signInAsDeveloperSuperAdmin()
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.AUTH) { inclusive = true }
                                    }
                                },
                                onContinueAsGuest = {
                                    viewModel.continueAsGuest()
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.AUTH) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Routes.HOME) {
                            HomeScreen(
                                language = language,
                                currentUser = currentUser,
                                approvedArticles = approvedArticles,
                                categories = categories,
                                editions = publishedEditions,
                                readingHistory = readingHistory,
                                authors = allUsers,
                                lowDataMode = preferences.lowDataMode,
                                onOpenArticle = { navController.navigate(Routes.article(it)) },
                                onOpenQuranStudio = { navController.navigate(Routes.QURAN) },
                                onOpenEdition = { navController.navigate(Routes.edition(it)) },
                                onOpenCategory = { slug ->
                                    viewModel.updateCategoryFilter(slug)
                                    navigateToTab(Routes.EXPLORE)
                                },
                                onOpenAuthor = { navController.navigate(Routes.author(it)) }
                            )
                        }

                        composable(Routes.EXPLORE) {
                            ExploreScreen(
                                language = language,
                                searchQuery = searchQuery,
                                selectedLanguageFilter = filterLanguage,
                                selectedCategoryFilter = filterCategory,
                                sortOption = sortOption,
                                searchResults = searchResults,
                                categories = categories,
                                editions = publishedEditions,
                                lowDataMode = preferences.lowDataMode,
                                onSearchChange = { viewModel.updateSearchQuery(it) },
                                onLanguageFilterChange = { viewModel.updateLanguageFilter(it) },
                                onCategoryFilterChange = { viewModel.updateCategoryFilter(it) },
                                onSortChange = { viewModel.updateSortOption(it) },
                                onOpenArticle = { navController.navigate(Routes.article(it)) },
                                onOpenEdition = { navController.navigate(Routes.edition(it)) },
                                onOpenQuranStudio = { navController.navigate(Routes.QURAN) }
                            )
                        }

                        composable(Routes.WRITE) {
                            WriteArticleScreen(
                                language = language,
                                currentUser = currentUser,
                                categories = categories,
                                editingArticle = editingArticle,
                                onRequireAuth = { navController.navigate(Routes.AUTH) },
                                onSaveOrSubmit = { existingId, title, authorName, body, subtitle, pubDesc, cat, lang, tags, coverUri, themeIdx, submit ->
                                    viewModel.submitOrSaveArticle(
                                        existingId, title, authorName, body, subtitle, pubDesc,
                                        cat, lang, tags, coverUri, themeIdx, submit
                                    ) {
                                        editingArticle = null
                                        navigateToTab(Routes.LIBRARY)
                                    }
                                }
                            )
                        }

                        composable(Routes.LIBRARY) {
                            val followedAuthors = allUsers.filter { followedAuthorIds.contains(it.uid) }
                            LibraryScreen(
                                language = language,
                                currentUser = currentUser,
                                savedArticles = savedArticles,
                                offlineArticles = offlineArticles,
                                bookmarks = bookmarks,
                                notes = personalNotes,
                                collections = collections,
                                readingHistory = readingHistory,
                                allApprovedArticles = approvedArticles,
                                myArticles = myArticles,
                                followedAuthors = followedAuthors,
                                categories = categories,
                                lowDataMode = preferences.lowDataMode,
                                onOpenArticle = { navController.navigate(Routes.article(it)) },
                                onEditArticle = { art ->
                                    editingArticle = art
                                    navigateToTab(Routes.WRITE)
                                },
                                onOpenAuthor = { navController.navigate(Routes.author(it)) },
                                onDeleteBookmark = { viewModel.deleteBookmark(it) },
                                onDeleteNote = { viewModel.deleteNote(it) },
                                onCreateCollection = { name, desc -> viewModel.createCollection(name, desc) },
                                onRequireAuth = { navController.navigate(Routes.AUTH) }
                            )
                        }

                        composable(Routes.PROFILE) {
                            ProfileAndSettingsScreen(
                                language = language,
                                preferences = preferences,
                                currentUser = currentUser,
                                myArticles = myArticles,
                                onToggleLanguage = { viewModel.toggleAppLanguage() },
                                onToggleDarkMode = { viewModel.setDarkMode(it) },
                                onToggleLowDataMode = { viewModel.setLowDataMode(it) },
                                onToggleNotifications = { viewModel.setNotificationsEnabled(it) },
                                onUpdateProfile = { name, bio, avatar -> viewModel.updateProfile(name, bio, avatar) },
                                onSubmitSupportFeedback = { type, subj, msg -> viewModel.submitSupportFeedback(type, subj, msg) },
                                onOpenAdminDashboard = { navController.navigate(Routes.ADMIN) },
                                onRequireAuth = { navController.navigate(Routes.AUTH) },
                                onLogout = {
                                    viewModel.logout()
                                    navController.navigate(Routes.AUTH) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onMessage = { viewModel.emitMessage(it) }
                            )
                        }

                        composable(
                            route = Routes.ARTICLE,
                            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
                        ) { entry ->
                            val articleId = entry.arguments?.getString("articleId") ?: ""
                            val article by viewModel.observeArticle(articleId).collectAsStateWithLifecycle(null)
                            val isLiked by viewModel.observeIsLiked(articleId).collectAsStateWithLifecycle(false)
                            val isSaved by viewModel.observeIsSaved(articleId).collectAsStateWithLifecycle(false)
                            val isFollowing by viewModel.observeIsFollowing(article?.authorUserId ?: "").collectAsStateWithLifecycle(false)
                            val comments by viewModel.observeComments(articleId).collectAsStateWithLifecycle(emptyList())
                            val initialProgress by produceState<ReadingProgressEntity?>(initialValue = null, articleId) {
                                value = viewModel.getReadingProgress(articleId)
                            }
                            val cat = categories.firstOrNull { it.slug == article?.categorySlug }

                            ArticleReadingScreen(
                                article = article,
                                category = cat,
                                language = language,
                                currentUser = currentUser,
                                isLiked = isLiked,
                                isSaved = isSaved,
                                isFollowingAuthor = isFollowing,
                                comments = comments,
                                fontSizeSp = preferences.readerFontSizeSp,
                                lineSpacing = preferences.readerLineSpacing,
                                readerThemeMode = preferences.readerThemeMode,
                                lowDataMode = preferences.lowDataMode,
                                initialProgress = initialProgress,
                                onRecordView = { viewModel.recordView(articleId) },
                                onSaveProgress = { pIdx, pct -> viewModel.saveReadingProgress(articleId, pIdx, pct) },
                                onToggleLike = { viewModel.toggleLike(articleId) },
                                onToggleSave = { viewModel.toggleSaveArticle(articleId, isSaved) },
                                onToggleOffline = { viewModel.toggleOfflineDownload(articleId, it) },
                                onToggleFollow = {
                                    article?.let { viewModel.toggleFollowAuthor(it.authorUserId, isFollowing) }
                                },
                                onBookmarkParagraph = { pIdx, excerpt ->
                                    article?.let { viewModel.bookmarkParagraph(it, pIdx, excerpt) }
                                },
                                onAddPersonalNote = { note ->
                                    article?.let { viewModel.addPersonalNote(it, note) }
                                },
                                onPostComment = { parentId, text ->
                                    viewModel.postComment(articleId, parentId, text)
                                },
                                onReportArticle = { reason ->
                                    article?.let { viewModel.submitReport("ARTICLE", it.id, it.title, reason) }
                                },
                                onReportComment = { cmt, reason ->
                                    viewModel.submitReport("COMMENT", cmt.id, cmt.content, reason)
                                },
                                onUpdateReaderSettings = { size, spacing, mode ->
                                    viewModel.updateReaderSettings(size, spacing, mode)
                                },
                                onOpenAuthor = { navController.navigate(Routes.author(it)) },
                                onMessage = { viewModel.emitMessage(it) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.QURAN) {
                            QuranRecitationScreen(
                                language = language,
                                playbackState = recitationState,
                                onSelectSurah = { viewModel.quranAudioPlayer.selectSurah(it) },
                                onPlayAyah = { s, a -> viewModel.quranAudioPlayer.playAyah(s, a) },
                                onTogglePlayPause = { viewModel.quranAudioPlayer.togglePlayPause() },
                                onStop = { viewModel.quranAudioPlayer.stop() },
                                onToggleRepeat = { viewModel.quranAudioPlayer.toggleRepeatAyah() },
                                onCycleSpeed = { viewModel.quranAudioPlayer.cycleSpeed() },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Routes.EDITION,
                            arguments = listOf(navArgument("editionId") { type = NavType.StringType })
                        ) { entry ->
                            val edId = entry.arguments?.getString("editionId") ?: ""
                            val edition = allEditions.firstOrNull { it.id == edId }
                            val articlesInEdition by viewModel.observeArticlesInEdition(edId).collectAsStateWithLifecycle(emptyList())
                            EditionDetailScreen(
                                edition = edition,
                                articles = articlesInEdition,
                                categories = categories,
                                language = language,
                                lowDataMode = preferences.lowDataMode,
                                onOpenArticle = { navController.navigate(Routes.article(it)) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Routes.AUTHOR,
                            arguments = listOf(navArgument("authorUid") { type = NavType.StringType })
                        ) { entry ->
                            val authorUid = entry.arguments?.getString("authorUid") ?: ""
                            val author by viewModel.observeUser(authorUid).collectAsStateWithLifecycle(null)
                            val authorArticles by viewModel.observeArticlesByAuthor(authorUid).collectAsStateWithLifecycle(emptyList())
                            val followersCount by viewModel.observeFollowersCount(authorUid).collectAsStateWithLifecycle(0)
                            val isFollowing by viewModel.observeIsFollowing(authorUid).collectAsStateWithLifecycle(false)
                            AuthorProfileScreen(
                                author = author,
                                authorArticles = authorArticles,
                                categories = categories,
                                followersCount = followersCount,
                                isFollowing = isFollowing,
                                language = language,
                                lowDataMode = preferences.lowDataMode,
                                onToggleFollow = { viewModel.toggleFollowAuthor(authorUid, isFollowing) },
                                onOpenArticle = { navController.navigate(Routes.article(it)) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.NOTIFICATIONS) {
                            NotificationsScreen(
                                language = language,
                                notifications = notifications,
                                onMarkRead = { viewModel.markNotificationRead(it) },
                                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.ADMIN) {
                            AdminDashboardScreen(
                                language = language,
                                currentUser = currentUser,
                                allUsers = allUsers,
                                allArticles = allArticlesForAdmin,
                                pendingArticles = pendingArticles,
                                categories = categories,
                                editions = allEditions,
                                reports = reports,
                                auditLogs = auditLogs,
                                totalCommentsCount = totalCommentsCount,
                                onApproveArticle = { id, edId, feat, edChoice ->
                                    viewModel.approveArticle(id, edId, feat, edChoice)
                                },
                                onRejectArticle = { id, reason -> viewModel.rejectArticle(id, reason) },
                                onDeleteArticle = { viewModel.deleteArticleByAdmin(it) },
                                onSearchUsers = { viewModel.searchUsersForAdmin(it) },
                                onAppointAdmin = { uid, perms -> viewModel.appointOrUpdateAdmin(uid, perms) },
                                onSetUserSuspended = { uid, susp -> viewModel.setUserSuspended(uid, susp) },
                                onDeleteUser = { viewModel.deleteUserAccount(it) },
                                onUpsertEdition = { viewModel.upsertEdition(it) },
                                onUpsertCategory = { viewModel.upsertCategory(it) },
                                onBroadcastAnnouncement = { tBn, tEn, bBn, bEn ->
                                    viewModel.broadcastAnnouncement(tBn, tEn, bBn, bEn)
                                },
                                onResolveReport = { rep, dismiss, hide ->
                                    viewModel.resolveReport(rep, dismiss, hide)
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
