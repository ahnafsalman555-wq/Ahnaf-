package com.example.data.local

data class QuranAyah(
    val surahNumber: Int,
    val ayahNumberInSurah: Int,
    val globalAyahNumber: Int,
    val arabicText: String,
    val bengaliTranslation: String,
    val englishTranslation: String,
    val audioUrl: String
)

data class QuranSurah(
    val surahNumber: Int,
    val nameArabic: String,
    val nameBengali: String,
    val nameEnglish: String,
    val revelationTypeBn: String,
    val revelationTypeEn: String,
    val ayahs: List<QuranAyah>
)

object QuranData {
    // Verified Quranic text (Surah Al-Fatihah, Ayat al-Kursi, Al-Inshirah, Al-Asr, Al-Ikhlas, Al-Falaq, An-Nas)
    // Audio streams from Islamic Network CDN (Reciter: Sheikh Mishary Rashid Alafasy)
    private fun alafasyUrl(globalAyahNumber: Int): String =
        "https://cdn.islamic.network/quran/audio/128/ar.alafasy/$globalAyahNumber.mp3"

    val surahs: List<QuranSurah> = listOf(
        QuranSurah(
            surahNumber = 1,
            nameArabic = "سُورَةُ الْفَاتِحَةِ",
            nameBengali = "সূরা আল-ফাতিহা (১)",
            nameEnglish = "Surah Al-Fatihah (1)",
            revelationTypeBn = "মক্কী • ৭ আয়াত",
            revelationTypeEn = "Makki • 7 Ayahs",
            ayahs = listOf(
                QuranAyah(1, 1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে শুরু করছি।", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", alafasyUrl(1)),
                QuranAyah(1, 2, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "সকল প্রশংসা জগতসমূহের প্রতিপালক আল্লাহর জন্য।", "All praise is due to Allah, Lord of the worlds.", alafasyUrl(2)),
                QuranAyah(1, 3, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "যিনি পরম করুণাময়, অতিশয় দয়ালু।", "The Entirely Merciful, the Especially Merciful,", alafasyUrl(3)),
                QuranAyah(1, 4, 4, "مَالِكِ يَوْمِ الدِّينِ", "বিচার দিবসের মালিক।", "Sovereign of the Day of Recompense.", alafasyUrl(4)),
                QuranAyah(1, 5, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "আমরা একমাত্র আপনারই ইবাদত করি এবং কেবল আপনারই সাহায্য প্রার্থনা করি।", "It is You we worship and You we ask for help.", alafasyUrl(5)),
                QuranAyah(1, 6, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "আমাদেরকে সরল-সঠিক পথ প্রদর্শন করুন।", "Guide us to the straight path —", alafasyUrl(6)),
                QuranAyah(1, 7, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "তাদের পথ, যাদের প্রতি আপনি অনুগ্রহ করেছেন; তাদের পথ নয়, যাদের প্রতি গযব নাযিল হয়েছে এবং যারা পথভ্রষ্ট হয়েছে।", "The path of those upon whom You have bestowed favor, not of those who have evoked anger or of those who are astray.", alafasyUrl(7))
            )
        ),
        QuranSurah(
            surahNumber = 2,
            nameArabic = "آيَةُ الْكُرْسِيِّ (البقرة ٢٥٥)",
            nameBengali = "আয়াতুল কুরসী (আল-বাকারা ২:২৫৫)",
            nameEnglish = "Ayat al-Kursi (Al-Baqarah 2:255)",
            revelationTypeBn = "মাদানী • মহান আয়াত",
            revelationTypeEn = "Madani • Greatest Verse",
            ayahs = listOf(
                QuranAyah(
                    2, 255, 262,
                    "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                    "আল্লাহ, তিনি ব্যতীত কোনো উপাস্য নেই। তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তাঁকে তন্দ্রা ও নিদ্রা স্পর্শ করে না। আকাশমণ্ডলী ও পৃথিবীতে যা কিছু আছে সবই তাঁর। তাঁর অনুমতি ব্যতীত তাঁর কাছে সুপারিশ করার কে আছে? তাদের সম্মুখে ও পশ্চাতে যা কিছু আছে সবই তিনি জানেন। তাঁর জ্ঞানের কিছুই তারা আয়ত্ত করতে পারে না, তবে তিনি যা ইচ্ছা করেন তা ব্যতীত। তাঁর কুরসী আকাশমণ্ডলী ও পৃথিবীকে পরিবেষ্টন করে আছে। আর এ উভয়ের রক্ষণাবেক্ষণ তাঁকে ক্লান্ত করে না। তিনি সর্বোচ্চ ও সুমহান।",
                    "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is presently before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
                    alafasyUrl(262)
                )
            )
        ),
        QuranSurah(
            surahNumber = 94,
            nameArabic = "سُورَةُ الشَّرْحِ",
            nameBengali = "সূরা আল-ইনশিরাহ (৯৪)",
            nameEnglish = "Surah Ash-Sharh (94)",
            revelationTypeBn = "মক্কী • ৮ আয়াত",
            revelationTypeEn = "Makki • 8 Ayahs",
            ayahs = listOf(
                QuranAyah(94, 1, 6091, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "আমি কি আপনার বক্ষকে প্রশস্ত করে দিইনি?", "Did We not expand for you your breast?", alafasyUrl(6091)),
                QuranAyah(94, 2, 6092, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "এবং আপনার থেকে আপনার ভার অপসারণ করেছি,", "And We removed from you your burden", alafasyUrl(6092)),
                QuranAyah(94, 3, 6093, "الَّذِي أَنقَضَ ظَهْرَكَ", "যা আপনার মেরুদণ্ড ভেঙে দিচ্ছিল।", "Which had weighed upon your back", alafasyUrl(6093)),
                QuranAyah(94, 4, 6094, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "আর আমি আপনার খ্যাতিকে উচ্চ মর্যাদা দান করেছি।", "And raised high for you your repute.", alafasyUrl(6094)),
                QuranAyah(94, 5, 6095, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "নিশ্চয়ই কষ্টের সাথেই রয়েছে স্বস্তি।", "For indeed, with hardship will be ease.", alafasyUrl(6095)),
                QuranAyah(94, 6, 6096, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "নিশ্চয়ই কষ্টের সাথেই রয়েছে স্বস্তি।", "Indeed, with hardship will be ease.", alafasyUrl(6096)),
                QuranAyah(94, 7, 6097, "فَإِذَا فَرَغْتَ فَانصَبْ", "অতএব যখনই আপনি অবসর পান, তখনই ইবাদতে সচেষ্ট হোন।", "So when you have finished your duties, then stand up for worship.", alafasyUrl(6097)),
                QuranAyah(94, 8, 6098, "وَإِلَىٰ رَبِّكَ فَارْغَب", "এবং আপনার প্রতিপালকের প্রতিই মনোনিবেশ করুন।", "And to your Lord direct your longing.", alafasyUrl(6098))
            )
        ),
        QuranSurah(
            surahNumber = 103,
            nameArabic = "سُورَةُ الْعَصْرِ",
            nameBengali = "সূরা আল-আসর (১০৩)",
            nameEnglish = "Surah Al-Asr (103)",
            revelationTypeBn = "মক্কী • ৩ আয়াত",
            revelationTypeEn = "Makki • 3 Ayahs",
            ayahs = listOf(
                QuranAyah(103, 1, 6177, "وَالْعَصْرِ", "কালের শপথ!", "By time,", alafasyUrl(6177)),
                QuranAyah(103, 2, 6178, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "নিশ্চয়ই মানুষ ক্ষতির মধ্যে নিপতিত,", "Indeed, mankind is in loss,", alafasyUrl(6178)),
                QuranAyah(103, 3, 6179, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "তবে তারা ব্যতীত, যারা ঈমান এনেছে, সৎকর্ম করেছে এবং পরস্পরকে সত্যের ও ধৈর্যের উপদেশ দিয়েছে।", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.", alafasyUrl(6179))
            )
        ),
        QuranSurah(
            surahNumber = 112,
            nameArabic = "سُورَةُ الْإِخْلَاصِ",
            nameBengali = "সূরা আল-ইখলাস (১১২)",
            nameEnglish = "Surah Al-Ikhlas (112)",
            revelationTypeBn = "মক্কী • ৪ আয়াত",
            revelationTypeEn = "Makki • 4 Ayahs",
            ayahs = listOf(
                QuranAyah(112, 1, 6222, "قُلْ هُوَ اللَّهُ أَحَدٌ", "বলুন, তিনি আল্লাহ, একক অদ্বিতীয়।", "Say, 'He is Allah, who is One,'", alafasyUrl(6222)),
                QuranAyah(112, 2, 6223, "اللَّهُ الصَّمَدُ", "আল্লাহ অমুখাপেক্ষী (সকলের আশ্রয়স্থল)।", "Allah, the Eternal Refuge.", alafasyUrl(6223)),
                QuranAyah(112, 3, 6224, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "তিনি কাউকে জন্ম দেননি এবং তাঁকেও জন্ম দেওয়া হয়নি।", "He neither begets nor is born,", alafasyUrl(6224)),
                QuranAyah(112, 4, 6225, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "এবং তাঁর সমতুল্য কেউই নেই।", "Nor is there to Him any equivalent.", alafasyUrl(6225))
            )
        )
    )
}
