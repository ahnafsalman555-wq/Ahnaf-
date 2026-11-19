package com.example.data.local

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordHasher {
    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest("$salt:$password".toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, salt: String, expectedHash: String): Boolean =
        hashPassword(password, salt) == expectedHash
}

object SeedDataProvider {
    const val SUPER_ADMIN_UID = "uid-ahnaf-salman-super-admin"
    const val SUPER_ADMIN_PERMANENT_ID = "RHM-100001"
    const val SUPER_ADMIN_NUMBER = 100001L
    const val SUPER_ADMIN_EMAIL = "ahnafsalman555@gmail.com"
    const val SUPER_ADMIN_NAME = "Ahnaf Salman"

    val defaultCategories: List<CategoryEntity> = listOf(
        CategoryEntity("islamic_articles", "ইসলামিক প্রবন্ধ", "Islamic Articles", 1, "mosque"),
        CategoryEntity("literature", "সাহিত্য", "Literature", 2, "auto_stories"),
        CategoryEntity("stories", "গল্প", "Stories", 3, "menu_book"),
        CategoryEntity("short_stories", "ছোটগল্প", "Short Stories", 4, "import_contacts"),
        CategoryEntity("poetry", "কবিতা", "Poetry", 5, "edit_note"),
        CategoryEntity("essays", "প্রবন্ধ", "Essays", 6, "article"),
        CategoryEntity("history", "ইতিহাস", "History", 7, "history_edu"),
        CategoryEntity("education", "শিক্ষা", "Education", 8, "school"),
        CategoryEntity("science", "বিজ্ঞান", "Science", 9, "science"),
        CategoryEntity("technology", "প্রযুক্তি", "Technology", 10, "memory"),
        CategoryEntity("research", "গবেষণা", "Research", 11, "biotech"),
        CategoryEntity("travel", "ভ্রমণ", "Travel", 12, "explore"),
        CategoryEntity("lifestyle", "জীবনধারা", "Lifestyle", 13, "spa"),
        CategoryEntity("culture", "সংস্কৃতি", "Culture", 14, "palette"),
        CategoryEntity("social_awareness", "সামাজিক সচেতনতা", "Social Awareness", 15, "groups"),
        CategoryEntity("opinion", "মতামত", "Opinion", 16, "forum"),
        CategoryEntity("personal_experiences", "ব্যক্তিগত অভিজ্ঞতা", "Personal Experiences", 17, "person_pin"),
        CategoryEntity("funny", "রম্য ও হাস্যরস", "Funny", 18, "sentiment_satisfied")
    )

    fun buildSeedUsers(): List<UserEntity> {
        val now = System.currentTimeMillis()
        val adminSalt = "rahabar_super_salt_2026"
        val authorSalt = "rahabar_author_salt_2026"
        val allPerms = AdminPermission.entries.joinToString(",") { it.name }
        return listOf(
            UserEntity(
                uid = SUPER_ADMIN_UID,
                userId = SUPER_ADMIN_PERMANENT_ID,
                userNumber = SUPER_ADMIN_NUMBER,
                username = "ahnafsalman",
                fullName = SUPER_ADMIN_NAME,
                email = SUPER_ADMIN_EMAIL,
                passwordHash = PasswordHasher.hashPassword("Rahabar@2026", adminSalt),
                salt = adminSalt,
                preferredLanguage = "bn",
                role = UserRole.SUPER_ADMIN.name,
                adminPermissions = allPerms,
                isSuspended = false,
                isDeveloperProtected = true,
                bio = "Developer, Founder & Permanent Super Admin of Monthly Rahabar Magazine.",
                createdAt = now - 86400_000L * 90
            ),
            UserEntity(
                uid = "uid-mahmud-hassan",
                userId = "RHM-100002",
                userNumber = 100002L,
                username = "mahmud_hassan",
                fullName = "মাওলানা মাহমুদ হাসান",
                email = "mahmud.hassan@rahabar.org",
                passwordHash = PasswordHasher.hashPassword("Author@1234", authorSalt),
                salt = authorSalt,
                preferredLanguage = "bn",
                role = UserRole.ADMIN.name,
                adminPermissions = AdminPermission.ARTICLE_APPROVAL.name,
                isSuspended = false,
                isDeveloperProtected = false,
                bio = "সাহিত্য সম্পাদক ও গবেষক — মাসিক রাহাবার ম্যাগাজিন।",
                createdAt = now - 86400_000L * 60
            ),
            UserEntity(
                uid = "uid-fatima-zahra",
                userId = "RHM-100003",
                userNumber = 100003L,
                username = "fatima_zahra",
                fullName = "ড. ফাতিমা তাসনিম",
                email = "fatima.tasnim@rahabar.org",
                passwordHash = PasswordHasher.hashPassword("Reader@1234", authorSalt),
                salt = authorSalt,
                preferredLanguage = "bn",
                role = UserRole.USER.name,
                adminPermissions = "",
                isSuspended = false,
                isDeveloperProtected = false,
                bio = "ইসলামিক ইতিহাস, বিজ্ঞান ও শিশু-কিশোর সাহিত্য বিষয়ক লেখিকা।",
                createdAt = now - 86400_000L * 30
            )
        )
    }

    fun buildSeedEditions(): List<EditionEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            EditionEntity(
                id = "edition-dec-2026",
                titleBn = "ডিসেম্বর সংখ্যা ২০২৬",
                titleEn = "December Edition 2026",
                editionType = EditionType.MONTHLY.name,
                monthNumber = 12,
                year = 2026,
                descriptionBn = "শীতকালীন সাহিত্য, আত্মশুদ্ধি ও জ্ঞানভিত্তিক প্রবন্ধের বিশেষ মাসিক সংখ্যা।",
                descriptionEn = "Monthly issue dedicated to Islamic reflection, heritage, and modern scholarship.",
                coverColorHex = "#14243A",
                isPublished = true,
                publishedAt = now - 86400_000L * 2
            ),
            EditionEntity(
                id = "edition-nov-2026",
                titleBn = "নভেম্বর সংখ্যা ২০২৬",
                titleEn = "November Edition 2026",
                editionType = EditionType.MONTHLY.name,
                monthNumber = 11,
                year = 2026,
                descriptionBn = "ইতিহাস, বিজ্ঞান ও মুসলিম মনীষীদের অবদান নিয়ে সমৃদ্ধ প্রকাশনা।",
                descriptionEn = "Exploring history, scientific heritage, and timeless literary essays.",
                coverColorHex = "#0F4C2A",
                isPublished = true,
                publishedAt = now - 86400_000L * 25
            ),
            EditionEntity(
                id = "edition-weekly-2026-46",
                titleBn = "সাপ্তাহিক রাহাবার • ৩য় সপ্তাহ ২০২৬",
                titleEn = "Weekly Rahabar • Week 3, 2026",
                editionType = EditionType.WEEKLY.name,
                monthNumber = 11,
                year = 2026,
                descriptionBn = "সমকালীন ভাবনা, ছোটগল্প ও তরুণ লেখকদের নির্বাচিত রচনা।",
                descriptionEn = "Weekly curated reflections, short stories, and contemporary essays.",
                coverColorHex = "#1F3554",
                isPublished = true,
                publishedAt = now - 86400_000L * 5
            )
        )
    }

    fun buildSeedArticles(): List<ArticleEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ArticleEntity(
                id = "art-success-in-life",
                title = "জীবনের প্রকৃত সফলতা ও আত্মশুদ্ধির পথ",
                subtitle = "কুরআন ও সুন্নাহর আলোকে প্রশান্ত হৃদয়ের সন্ধান",
                authorUserId = SUPER_ADMIN_UID,
                authorName = "আহনাফ সালমান",
                authorUsername = "ahnafsalman",
                authorPermanentId = SUPER_ADMIN_PERMANENT_ID,
                body = """
                    # প্রকৃত সফলতার মানদণ্ড
                    মানুষ জন্মগতভাবেই সফলতা ও প্রশান্তির অন্বেষী। কিন্তু আধুনিক বস্তুবাদী পৃথিবীতে সফলতার সংজ্ঞা প্রায়শই কেবল সম্পদ, পদমর্যাদা কিংবা ক্ষণস্থায়ী খ্যাতির মধ্যে সীমাবদ্ধ হয়ে পড়ে। পবিত্র কুরআন আমাদের সামনে চিরন্তন ও ভারসাম্যপূর্ণ সফলতার এক অনুপম আদর্শ তুলে ধরে।

                    [ARABIC]
                    قَدْ أَفْلَحَ مَن زَكَّاهَا ۝ وَقَدْ خَابَ مَن دَسَّاهَا
                    [/ARABIC]

                    সূরা আশ-শামসের (৯১:৯-১০) এই মহিমান্বিত আয়াতে মহান আল্লাহ ঘোষণা করেছেন: “নিশ্চয়ই সে সফলকাম হয়েছে, যে নিজেকে (আত্মাকে) পরিশুদ্ধ করেছে। আর সে ব্যর্থ হয়েছে, যে নিজেকে কলুষিত করেছে।”

                    ## অন্তরের প্রশান্তি ও তাওয়াক্কুল
                    যখন একজন মুমিন তার প্রতিটি কর্মে আল্লাহর সন্তুষ্টিকে প্রাধান্য দেয়, তখন দুনিয়ার ব্যস্ততাও ইবাদতে রূপান্তরিত হয়। জীবনের প্রতিটি পদক্ষেপে সততা, ন্যায়পরায়ণতা এবং মানুষের প্রতি কল্যাণকামিতা ধারণ করাই প্রকৃত মুমিনের পরিচয়।

                    > “হৃদয় কেবল আল্লাহর স্মরণেই প্রশান্তি লাভ করে।” — সূরা আর-রা'দ (১৩:২৮)

                    ## দৈনন্দিন জীবনে করণীয়
                    - ফজরের পর অন্তত দশ মিনিট অর্থসহ পবিত্র কুরআন তিলাওয়াত করা।
                    - সময়ের অপচয় রোধ করে জ্ঞান অর্জন ও সৎকর্মে নিজেকে নিয়োজিত রাখা।
                    - পিতা-মাতা, প্রতিবেশী ও সমাজের অসহায় মানুষের প্রতি সদাচরণ করা।
                    - প্রতিদিনের শেষে আত্মসমালোচনা (মুহাসাবা) করা।
                """.trimIndent(),
                publicationDescription = "আত্মশুদ্ধি ও কুরআনিক জীবনদর্শন বিষয়ক প্রবন্ধ।",
                language = "bn",
                categorySlug = "islamic_articles",
                tags = "সফলতা,আত্মশুদ্ধি,কুরআন,তাযকিয়া",
                editionId = "edition-dec-2026",
                coverThemeIndex = 0,
                status = ArticleStatus.APPROVED.name,
                isFeatured = true,
                isEditorsChoice = true,
                viewsCount = 1480,
                likesCount = 342,
                readingTimeMinutes = 4,
                createdAt = now - 86400_000L * 4,
                updatedAt = now - 86400_000L * 3,
                publishedAt = now - 86400_000L * 3
            ),
            ArticleEntity(
                id = "art-house-of-wisdom",
                title = "বাইতুল হিকমাহ: মুসলিম বিজ্ঞান ও জ্ঞানচর্চার স্বর্ণযুগ",
                subtitle = "বাগদাদের সেই ঐতিহাসিক জ্ঞানকেন্দ্রের অনুপ্রেরণাদায়ক ইতিহাস",
                authorUserId = "uid-fatima-zahra",
                authorName = "ড. ফাতিমা তাসনিম",
                authorUsername = "fatima_zahra",
                authorPermanentId = "RHM-100003",
                body = """
                    # জ্ঞান অন্বেষণের চিরন্তন ঐতিহ্য
                    ইসলামের প্রথম ওহী শুরু হয়েছিল 'ইকরা' বা 'পড়ো' নির্দেশের মাধ্যমে। সেই ঐশী আহ্বানে সাড়া দিয়ে মুসলিম মনীষীরা অষ্টম থেকে ত্রয়োদশ শতাব্দী পর্যন্ত বিজ্ঞান, চিকিৎসা, জ্যোতির্বিজ্ঞান, গণিত ও সাহিত্যে অভূতপূর্ব উৎকর্ষ সাধন করেছিলেন।

                    [ARABIC]
                    وَقُل رَّبِّ زِدْنِي عِلْمًا
                    [/ARABIC]

                    (সূরা ত্বাহা ২০:১১৪ — “আর বলুন, হে আমার প্রতিপালক! আমার জ্ঞান বৃদ্ধি করে দিন।”)

                    ## বাগদাদের বাইতুল হিকমাহ
                    খলিফা হারুন অর-রশীদ এবং পরবর্তীতে আল-মামুনের পৃষ্ঠপোষকতায় বাগদাদে গড়ে ওঠে বিশ্ববিখ্যাত 'বাইতুল হিকমাহ' বা জ্ঞানের ঘর। এখানে আল-খাওয়ারিজমী বীজগণিতের ভিত্তি স্থাপন করেন, ইবনে সিনা চিকিৎসাবিজ্ঞানে 'আল-কানুন ফিত-তিব্ব' রচনা করেন এবং আল-হাইসাম আধুনিক আলোকবিজ্ঞানের পরীক্ষামূলক পদ্ধতির সূচনা করেন।

                    ## আধুনিক প্রজন্মের জন্য শিক্ষা
                    আজকের মুসলিম তরুণদের জন্য বিজ্ঞান ও প্রযুক্তিকে নৈতিকতা ও আধ্যাত্মিকতার সাথে সমন্বয় করে নতুন আবিষ্কারে এগিয়ে আসা সময়ের দাবি।
                """.trimIndent(),
                publicationDescription = "মুসলিম সভ্যতার বৈজ্ঞানিক ও ঐতিহাসিক উত্তরাধিকার।",
                language = "bn",
                categorySlug = "history",
                tags = "ইতিহাস,বিজ্ঞান,বাইতুল হিকমাহ,শিক্ষা",
                editionId = "edition-dec-2026",
                coverThemeIndex = 1,
                status = ArticleStatus.APPROVED.name,
                isFeatured = true,
                isEditorsChoice = true,
                viewsCount = 1120,
                likesCount = 265,
                readingTimeMinutes = 5,
                createdAt = now - 86400_000L * 6,
                updatedAt = now - 86400_000L * 5,
                publishedAt = now - 86400_000L * 5
            ),
            ArticleEntity(
                id = "art-ethics-of-words",
                title = "The Ethics of the Pen in the Digital Age",
                subtitle = "Reviving adab, sincerity, and verification in modern Muslim writing",
                authorUserId = SUPER_ADMIN_UID,
                authorName = "Ahnaf Salman",
                authorUsername = "ahnafsalman",
                authorPermanentId = SUPER_ADMIN_PERMANENT_ID,
                body = """
                    # Responsibility Behind Every Published Word
                    In an era where anyone can broadcast thoughts to thousands within seconds, the Islamic tradition reminds us that the pen is a sacred trust (amanah). Every word we write leaves an imprint on hearts and society.

                    [ARABIC]
                    ن ۚ وَالْقَلَمِ وَمَا يَسْطُرُونَ
                    [/ARABIC]

                    (Surah Al-Qalam 68:1 — "Nun. By the pen and what they inscribe.")

                    ## Principles of Ethical Publishing
                    - **Verify Before Sharing (Tabayyun):** Never publish unverified claims, rumors, or unauthenticated religious citations.
                    - **Speak with Wisdom (Hikmah):** Address disagreements with dignity, fairness, and compassion.
                    - **Beneficial Knowledge (Ilm Nafi):** Aim to elevate readers intellectually, morally, and spiritually.

                    > "Whoever believes in Allah and the Last Day should speak what is good or remain silent." — Authentic Prophetic Principle (Sahih al-Bukhari & Muslim)
                """.trimIndent(),
                publicationDescription = "Editorial manifesto on responsible Islamic journalism and writing.",
                language = "en",
                categorySlug = "essays",
                tags = "ethics,writing,adab,journalism",
                editionId = "edition-dec-2026",
                coverThemeIndex = 2,
                status = ArticleStatus.APPROVED.name,
                isFeatured = true,
                isEditorsChoice = false,
                viewsCount = 890,
                likesCount = 198,
                readingTimeMinutes = 3,
                createdAt = now - 86400_000L * 7,
                updatedAt = now - 86400_000L * 6,
                publishedAt = now - 86400_000L * 6
            ),
            ArticleEntity(
                id = "art-winter-dawn-story",
                title = "কুয়াশা ভেদ করে ভোরের আলো",
                subtitle = "গ্রামবাংলার এক মসজিদ-কেন্দ্রিক পাঠাগারের ছোটগল্প",
                authorUserId = "uid-mahmud-hassan",
                authorName = "মাওলানা মাহমুদ হাসান",
                authorUsername = "mahmud_hassan",
                authorPermanentId = "RHM-100002",
                body = """
                    # ভোরের আজান ও পাঠাগারের বাতি
                    নভেম্বরের শেষ দিকের কুয়াশাচ্ছন্ন ভোর। ছোট্ট নদীর তীর ঘেঁষে দাঁড়িয়ে থাকা পুরোনো ইটের মসজিদ থেকে ভেসে আসছে সুমধুর আজানের ধ্বনি। মসজিদের পাশের ছোট্ট কক্ষটিতে তখনো টিমটিম করে জ্বলছে একটি টেবিল ল্যাম্প।

                    ## আলোর ফেরিওয়ালা
                    বয়স্ক ইমাম সাহেব প্রতিদিন ফজরের পর গ্রামের কিশোরদের নিয়ে বসেন। তাদের হাতে তুলে দেন ইতিহাস, সাহিত্য আর সীরাতের বই। একটি ছোট্ট পাঠাগার কীভাবে পুরো গ্রামের তরুণদের জীবনধারা বদলে দিতে পারে, তার জীবন্ত সাক্ষী এই শান্ত জনপদ।
                """.trimIndent(),
                publicationDescription = "মানবিক মূল্যবোধ ও পাঠাভ্যাস নিয়ে হৃদয়স্পর্শী ছোটগল্প।",
                language = "bn",
                categorySlug = "short_stories",
                tags = "ছোটগল্প,সাহিত্য,পাঠাগার,গ্রামবাংলা",
                editionId = "edition-weekly-2026-46",
                coverThemeIndex = 3,
                status = ArticleStatus.APPROVED.name,
                isFeatured = false,
                isEditorsChoice = true,
                viewsCount = 740,
                likesCount = 215,
                readingTimeMinutes = 3,
                createdAt = now - 86400_000L * 8,
                updatedAt = now - 86400_000L * 7,
                publishedAt = now - 86400_000L * 7
            ),
            ArticleEntity(
                id = "art-quranic-patience",
                title = "Reflections on Surah Al-Asr: Time, Faith, and Community",
                subtitle = "Why three short verses hold the blueprint for a meaningful life",
                authorUserId = "uid-fatima-zahra",
                authorName = "Dr. Fatima Tasnim",
                authorUsername = "fatima_zahra",
                authorPermanentId = "RHM-100003",
                body = """
                    # The Precious Capital of Time
                    Imam al-Shafi'i famously remarked that if people pondered deeply upon Surah Al-Asr alone, it would suffice for their guidance. Time is our most fragile resource; unlike wealth, once spent, it never returns.

                    [ARABIC]
                    وَالْعَصْرِ ۝ إِنَّ الْإِنسَانَ لَفِي خُسْرٍ
                    [/ARABIC]

                    ## Four Pillars Against Loss
                    - **Faith (Iman):** Grounding our worldview in truth and accountability.
                    - **Righteous Action (Amal Salih):** Translating conviction into service, excellence, and kindness.
                    - **Mutual Counsel in Truth:** Building communities that uplift one another honestly.
                    - **Mutual Counsel in Patience:** Standing firm with grace during hardship and long journeys.
                """.trimIndent(),
                publicationDescription = "Literary tafsir reflection on Surah Al-Asr.",
                language = "en",
                categorySlug = "islamic_articles",
                tags = "quran,tafsir,patience,time",
                editionId = "edition-nov-2026",
                coverThemeIndex = 0,
                status = ArticleStatus.APPROVED.name,
                isFeatured = false,
                isEditorsChoice = false,
                viewsCount = 610,
                likesCount = 154,
                readingTimeMinutes = 3,
                createdAt = now - 86400_000L * 10,
                updatedAt = now - 86400_000L * 9,
                publishedAt = now - 86400_000L * 9
            ),
            ArticleEntity(
                id = "art-pending-sample",
                title = "ডিজিটাল যুগে তরুণ সমাজ ও নৈতিক শিক্ষার গুরুত্ব",
                subtitle = "প্রযুক্তির কল্যাণকর ব্যবহার এবং পারিবারিক বন্ধন",
                authorUserId = "uid-fatima-zahra",
                authorName = "ড. ফাতিমা তাসনিম",
                authorUsername = "fatima_zahra",
                authorPermanentId = "RHM-100003",
                body = """
                    # প্রযুক্তি আমাদের সেবক, প্রভু নয়
                    স্মার্টফোন ও ইন্টারনেট আমাদের জ্ঞানার্জনের পথকে সহজ করেছে। কিন্তু নিয়ন্ত্রণহীন ব্যবহার আমাদের মনোযোগ ও পারিবারিক সময় কেড়ে নিচ্ছে। সচেতন ডিজিটাল জীবনযাপন এখন সময়ের অপরিহার্য দাবি।
                """.trimIndent(),
                publicationDescription = "সম্পাদকীয় অনুমোদনের অপেক্ষায় থাকা একটি প্রবন্ধ।",
                language = "bn",
                categorySlug = "social_awareness",
                tags = "তরুণ,প্রযুক্তি,নৈতিকতা",
                editionId = null,
                coverThemeIndex = 1,
                status = ArticleStatus.PENDING.name,
                isFeatured = false,
                isEditorsChoice = false,
                viewsCount = 0,
                likesCount = 0,
                readingTimeMinutes = 2,
                createdAt = now - 3600_000L * 5,
                updatedAt = now - 3600_000L * 5,
                publishedAt = null
            )
        )
    }

    fun buildSeedNotifications(): List<NotificationEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            NotificationEntity(
                id = "notif-welcome-all",
                recipientUserId = "ALL",
                titleBn = "বিসমিল্লাহ! মাসিক রাহাবার ম্যাগাজিনে স্বাগতম",
                titleEn = "Bismillah! Welcome to Monthly Rahabar Magazine",
                messageBn = "ডিসেম্বর সংখ্যা ২০২৬ এখন প্রকাশিত হয়েছে। নতুন প্রবন্ধ ও কুরআন তিলাওয়াত উপভোগ করুন।",
                messageEn = "December Edition 2026 is now published. Explore curated essays and Quran recitation.",
                type = "EDITION",
                referenceId = "edition-dec-2026",
                isRead = false,
                createdAt = now - 7200_000L
            ),
            NotificationEntity(
                id = "notif-admin-pending",
                recipientUserId = "ADMINS",
                titleBn = "নতুন প্রবন্ধ অনুমোদনের অপেক্ষায়",
                titleEn = "New Article Awaiting Editorial Review",
                messageBn = "ড. ফাতিমা তাসনিম 'ডিজিটাল যুগে তরুণ সমাজ ও নৈতিক শিক্ষার গুরুত্ব' শিরোনামে একটি লেখা জমা দিয়েছেন।",
                messageEn = "Dr. Fatima Tasnim submitted an article for editorial approval.",
                type = "SUBMISSION",
                referenceId = "art-pending-sample",
                isRead = false,
                createdAt = now - 3600_000L * 5
            )
        )
    }
}
