package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.theme.AdorsholipiFontFamily
import com.example.ui.theme.ArabicQuranFontFamily
import com.example.ui.theme.DeveloperCreditCard
import com.example.ui.theme.DisplayEnglishFontFamily
import com.example.ui.theme.GoldCalligraphyDivider
import com.example.ui.theme.IslamicEmeraldDeep
import com.example.ui.theme.IslamicGeometricWatermark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicNavy
import com.example.ui.theme.IslamicNavyDeep
import com.example.ui.theme.RahabarLogoImage
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.WarmCream
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    language: AppLanguage,
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1300)
        onFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(IslamicEmeraldDeep, IslamicNavyDeep, IslamicNavy)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        IslamicGeometricWatermark(
            modifier = Modifier.fillMaxSize(),
            tint = IslamicGold.copy(alpha = 0.14f),
            stepDp = 64.dp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = ArabicQuranFontFamily,
                    color = IslamicGoldLight
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            RahabarLogoImage(
                modifier = Modifier
                    .size(172.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.5.dp, IslamicGold, RoundedCornerShape(28.dp))
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "MONTHLY RAHABAR MAGAZINE",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = DisplayEnglishFontFamily,
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (language == AppLanguage.BN) {
                    "ইসলামিক সাহিত্য, জ্ঞান ও সংস্কৃতির ডিজিটাল মঞ্চ"
                } else {
                    "Islamic Literary & Knowledge Publishing Platform"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = if (language == AppLanguage.BN) AdorsholipiFontFamily else MaterialTheme.typography.bodyMedium.fontFamily,
                    color = WarmCream
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            GoldCalligraphyDivider(modifier = Modifier.width(220.dp))
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Developed & Owned by Ahnaf Salman\nahnafsalman555@gmail.com",
                style = MaterialTheme.typography.labelMedium.copy(color = IslamicGoldLight),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AuthScreen(
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String) -> Unit,
    onCheckUsername: suspend (String) -> Boolean,
    onResetPassword: (String, String, String) -> Unit,
    onSuperAdminSignIn: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val strings = AppStrings.get(language)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Register, 2 = Recover
    var showPassword by remember { mutableStateOf(false) }

    // Login state
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    // Register state
    var regFullName by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var usernameCheckStatus by remember { mutableStateOf<Boolean?>(null) }

    // Recovery state
    var recEmail by remember { mutableStateOf("") }
    var recUsername by remember { mutableStateOf("") }
    var recNewPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        IslamicGeometricWatermark(
            modifier = Modifier.fillMaxSize(),
            tint = IslamicGold.copy(alpha = 0.08f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.bismillah,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = ArabicQuranFontFamily,
                        color = IslamicGold,
                        letterSpacing = 0.sp
                    )
                )
                Surface(
                    onClick = onToggleLanguage,
                    shape = CircleShape,
                    color = IslamicNavy,
                    border = BorderStroke(1.dp, IslamicGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Language, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.BN) "বাংলা / EN" else "EN / বাংলা",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = AdorsholipiFontFamily,
                                color = WarmCream,
                                letterSpacing = 0.sp
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            RahabarLogoImage(
                modifier = Modifier
                    .size(102.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.5.dp, IslamicGold, RoundedCornerShape(22.dp))
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = strings.appTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text = strings.salamGreeting,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text(strings.signInButton) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text(if (language == AppLanguage.BN) "নিবন্ধন" else "Register") }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text(if (language == AppLanguage.BN) "পুনরুদ্ধার" else "Recover") }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    when (selectedTab) {
                        0 -> {
                            Text(strings.signInTitle, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = loginIdentifier,
                                onValueChange = { loginIdentifier = it },
                                label = { Text("${strings.emailLabel} / ${strings.usernameLabel}") },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input")
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text(strings.passwordLabel) },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )
                            Spacer(Modifier.height(14.dp))
                            Button(
                                onClick = { onLogin(loginIdentifier, loginPassword) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicNavy, contentColor = WarmCream)
                            ) {
                                Text(strings.signInButton, fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = { selectedTab = 2 },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(strings.forgotPassword)
                            }
                        }

                        1 -> {
                            Text(strings.registerTitle, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = regFullName,
                                onValueChange = { regFullName = it },
                                label = { Text(strings.fullNameLabel) },
                                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_fullname_input")
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = regUsername,
                                onValueChange = {
                                    regUsername = it
                                    usernameCheckStatus = null
                                },
                                label = { Text(strings.usernameLabel) },
                                leadingIcon = { Icon(Icons.Rounded.AlternateEmail, contentDescription = null) },
                                trailingIcon = {
                                    TextButton(onClick = {
                                        scope.launch { usernameCheckStatus = onCheckUsername(regUsername) }
                                    }) {
                                        Text(strings.checkUsernameAvailability)
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_username_input")
                            )
                            usernameCheckStatus?.let { available ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        if (available) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = if (available) StatusApproved else StatusRejected,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = if (available) strings.usernameAvailable else strings.usernameTaken,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (available) StatusApproved else StatusRejected
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it },
                                label = { Text(strings.emailLabel) },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_email_input")
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it },
                                label = { Text(strings.passwordLabel) },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_input")
                            )
                            Spacer(Modifier.height(14.dp))
                            Button(
                                onClick = { onRegister(regFullName, regUsername, regEmail, regPassword) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("register_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicNavy, contentColor = WarmCream)
                            ) {
                                Text(strings.createAccountButton, fontWeight = FontWeight.Bold)
                            }
                        }

                        2 -> {
                            Text(strings.recoverPasswordTitle, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = recEmail,
                                onValueChange = { recEmail = it },
                                label = { Text(strings.emailLabel) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = recUsername,
                                onValueChange = { recUsername = it },
                                label = { Text(strings.usernameLabel) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = recNewPassword,
                                onValueChange = { recNewPassword = it },
                                label = { Text(if (language == AppLanguage.BN) "নতুন পাসওয়ার্ড" else "New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(14.dp))
                            Button(
                                onClick = { onResetPassword(recEmail, recUsername, recNewPassword) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(strings.resetPasswordButton)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onSuperAdminSignIn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("super_admin_signin_button"),
                        border = BorderStroke(1.dp, IslamicGold)
                    ) {
                        Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = IslamicGold)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.quickDemoSuperAdmin, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = onContinueAsGuest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guest_mode_button")
                    ) {
                        Text(strings.continueAsGuest)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            DeveloperCreditCard(isBengali = language == AppLanguage.BN)
            Spacer(Modifier.height(24.dp))
        }
    }
}
