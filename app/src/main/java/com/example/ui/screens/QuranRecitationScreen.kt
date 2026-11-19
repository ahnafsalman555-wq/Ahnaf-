package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.audio.RecitationPlaybackState
import com.example.data.local.QuranData
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.theme.GoldCalligraphyDivider
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGeometricWatermark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.WarmCream
import com.example.ui.theme.arabicQuranTextStyle
import com.example.ui.theme.crispFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranRecitationScreen(
    language: AppLanguage,
    playbackState: RecitationPlaybackState,
    onSelectSurah: (Int) -> Unit,
    onPlayAyah: (Int, Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    onToggleRepeat: () -> Unit,
    onCycleSpeed: () -> Unit,
    onBack: () -> Unit
) {
    val strings = AppStrings.get(language)
    val selectedSurah = QuranData.surahs.firstOrNull { it.surahNumber == playbackState.selectedSurahNumber }
        ?: QuranData.surahs.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.quranStudioTitle, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (language == AppLanguage.BN) "ক্বারী: শায়খ মিশারী রশিদ আল-আফাসী" else "Reciter: Sheikh Mishary Rashid Alafasy",
                            style = MaterialTheme.typography.labelSmall.copy(color = IslamicGoldLight)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicEmeraldDeep,
                    titleContentColor = WarmCream,
                    navigationIconContentColor = WarmCream
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Surah Picker
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QuranData.surahs, key = { it.surahNumber }) { surah ->
                    val selected = surah.surahNumber == selectedSurah.surahNumber
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectSurah(surah.surahNumber) },
                        label = {
                            val name = if (language == AppLanguage.BN) surah.nameBengali else surah.nameEnglish
                            Text(name, style = MaterialTheme.typography.labelLarge.crispFor(name, language.code))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicEmeraldDeep,
                            selectedLabelColor = IslamicGoldLight
                        )
                    )
                }
            }

            // Surah Calligraphy Banner & Player Controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicNavyDeep),
                border = BorderStroke(1.dp, IslamicGold)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IslamicGeometricWatermark(
                        modifier = Modifier.matchParentSize(),
                        tint = IslamicGold.copy(alpha = 0.12f),
                        stepDp = 48.dp
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedSurah.nameArabic,
                            style = arabicQuranTextStyle(28f, 44f).copy(color = IslamicGold),
                            textAlign = TextAlign.Center
                        )
                        val surahInfo = if (language == AppLanguage.BN) {
                            "${selectedSurah.nameBengali} • ${selectedSurah.revelationTypeBn}"
                        } else {
                            "${selectedSurah.nameEnglish} • ${selectedSurah.revelationTypeEn}"
                        }
                        Text(
                            text = surahInfo,
                            style = MaterialTheme.typography.labelMedium.copy(color = WarmCream)
                                .crispFor(surahInfo, language.code)
                        )
                        Spacer(Modifier.height(10.dp))
                        GoldCalligraphyDivider()
                        Spacer(Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(onClick = onToggleRepeat) {
                                Icon(
                                    Icons.Rounded.Repeat,
                                    contentDescription = "Repeat Ayah",
                                    tint = if (playbackState.repeatAyah) IslamicGold else WarmCream.copy(alpha = 0.85f)
                                )
                            }
                            Surface(
                                onClick = onTogglePlayPause,
                                shape = CircleShape,
                                color = IslamicGold,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (playbackState.isBuffering) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = IslamicNavyDeep,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            if (playbackState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = "Play/Pause Recitation",
                                            tint = IslamicNavyDeep,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }
                            IconButton(onClick = onStop) {
                                Icon(Icons.Rounded.Stop, contentDescription = "Stop", tint = WarmCream)
                            }
                            Surface(
                                onClick = onCycleSpeed,
                                shape = RoundedCornerShape(14.dp),
                                color = IslamicGold.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.Speed, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("${playbackState.playbackSpeed}x", style = MaterialTheme.typography.labelSmall.copy(color = IslamicGold))
                                }
                            }
                        }
                        playbackState.errorMessage?.let { err ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = err,
                                style = MaterialTheme.typography.labelSmall.copy(color = IslamicGoldLight),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Ayahs List with Khatte Quran / Amiri RTL rendering
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(selectedSurah.ayahs, key = { _, a -> a.globalAyahNumber }) { index, ayah ->
                    val isCurrent = playbackState.selectedSurahNumber == selectedSurah.surahNumber &&
                        playbackState.currentAyahIndex == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlayAyah(selectedSurah.surahNumber, index) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) IslamicGold.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            color = if (isCurrent) IslamicGold else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = IslamicNavyDeep
                                ) {
                                    Text(
                                        text = "${ayah.surahNumber}:${ayah.ayahNumberInSurah}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = IslamicGold),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onPlayAyah(selectedSurah.surahNumber, index) }
                                ) {
                                    Icon(
                                        if (isCurrent && playbackState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                        contentDescription = "Play Ayah",
                                        tint = IslamicGold
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = ayah.arabicText,
                                style = arabicQuranTextStyle(25f, 44f).copy(color = MaterialTheme.colorScheme.onSurface),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(10.dp))
                            val translation = if (language == AppLanguage.BN) ayah.bengaliTranslation else ayah.englishTranslation
                            Text(
                                text = translation,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ).crispFor(translation, language.code)
                            )
                        }
                    }
                }
            }
        }
    }
}
