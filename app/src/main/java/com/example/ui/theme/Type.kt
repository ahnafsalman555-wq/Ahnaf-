package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.example.R

// Bundled Adorsholipi TrueType Font for crisp, authentic Bengali rendering across the entire app
val AdorsholipiFontFamily = FontFamily(
    Font(R.font.adorsholipi, FontWeight.Normal),
    Font(R.font.adorsholipi, FontWeight.Medium),
    Font(R.font.adorsholipi, FontWeight.SemiBold),
    Font(R.font.adorsholipi, FontWeight.Bold)
)

// Arabic Quranic & Hadith Font (Amiri RTL)
val ArabicQuranFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Bold)
)

// Premium English Literary Typography
val DisplayEnglishFontFamily = FontFamily(
    Font(R.font.cormorant_garamond, FontWeight.Normal),
    Font(R.font.cormorant_garamond, FontWeight.SemiBold),
    Font(R.font.cormorant_garamond, FontWeight.Bold)
)

val BodyEnglishFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

fun fontFamilyForLanguage(languageCode: String): FontFamily = when (languageCode.lowercase()) {
    "ar", "arabic" -> ArabicQuranFontFamily
    "en", "english" -> BodyEnglishFontFamily
    else -> AdorsholipiFontFamily
}

fun containsBengali(text: String): Boolean =
    text.any { it in '\u0980'..'\u09FF' }

fun containsArabic(text: String): Boolean =
    text.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' || it in '\uFB50'..'\uFDFF' || it in '\uFE70'..'\uFEFF' }

fun smartFontFamily(text: String, fallbackLanguage: String = "bn"): FontFamily = when {
    containsArabic(text) && !containsBengali(text) -> ArabicQuranFontFamily
    containsBengali(text) -> AdorsholipiFontFamily
    else -> fontFamilyForLanguage(fallbackLanguage)
}

fun TextStyle.crispFor(text: String, fallbackLanguage: String = "bn"): TextStyle {
    val family = smartFontFamily(text, fallbackLanguage)
    return copy(
        fontFamily = family,
        letterSpacing = if (family == AdorsholipiFontFamily || family == ArabicQuranFontFamily) 0.sp else letterSpacing
    )
}

fun arabicQuranTextStyle(fontSizeSp: Float = 24f, lineHeightSp: Float = 44f): TextStyle = TextStyle(
    fontFamily = ArabicQuranFontFamily,
    fontSize = fontSizeSp.sp,
    lineHeight = lineHeightSp.sp,
    letterSpacing = 0.sp,
    textDirection = TextDirection.Rtl
)

fun buildRahabarTypography(isBengali: Boolean): Typography {
    val primaryFamily = if (isBengali) AdorsholipiFontFamily else BodyEnglishFontFamily
    val displayFamily = if (isBengali) AdorsholipiFontFamily else DisplayEnglishFontFamily
    val bodyLetterSpacing = 0.sp

    return Typography(
        displayLarge = TextStyle(
            fontFamily = displayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 44.sp,
            letterSpacing = bodyLetterSpacing
        ),
        headlineLarge = TextStyle(
            fontFamily = displayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 38.sp,
            letterSpacing = bodyLetterSpacing
        ),
        headlineMedium = TextStyle(
            fontFamily = displayFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 33.sp,
            letterSpacing = bodyLetterSpacing
        ),
        headlineSmall = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 21.sp,
            lineHeight = 30.sp,
            letterSpacing = bodyLetterSpacing
        ),
        titleLarge = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            lineHeight = 27.sp,
            letterSpacing = bodyLetterSpacing
        ),
        titleMedium = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.5.sp,
            lineHeight = 24.sp,
            letterSpacing = bodyLetterSpacing
        ),
        titleSmall = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.5.sp,
            lineHeight = 21.sp,
            letterSpacing = bodyLetterSpacing
        ),
        bodyLarge = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            letterSpacing = bodyLetterSpacing
        ),
        bodyMedium = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.5.sp,
            lineHeight = 23.sp,
            letterSpacing = bodyLetterSpacing
        ),
        bodySmall = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.5.sp,
            lineHeight = 19.sp,
            letterSpacing = bodyLetterSpacing
        ),
        labelLarge = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = bodyLetterSpacing
        ),
        labelMedium = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.5.sp,
            lineHeight = 18.sp,
            letterSpacing = bodyLetterSpacing
        ),
        labelSmall = TextStyle(
            fontFamily = primaryFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            letterSpacing = bodyLetterSpacing
        )
    )
}
