package com.stickwithit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KanaColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = White,
    primaryContainer = SkyBlue,
    onPrimaryContainer = NavyBlue,
    secondary = AzureBlue,
    onSecondary = White,
    secondaryContainer = SkyBlue,
    onSecondaryContainer = NavyBlue,
    tertiary = WarmSand,
    onTertiary = NavyBlue,
    background = Cream,
    onBackground = NavyBlue,
    surface = Cream,
    onSurface = NavyBlue,
    surfaceVariant = White,
    onSurfaceVariant = AzureBlue,
    error = ErrorRed,
    onError = White,
    outline = AzureBlue,
    outlineVariant = SkyBlue,
)

@Composable
fun StickWithItTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KanaColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun kanaTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NavyBlue,
    unfocusedBorderColor = SkyBlue,
    focusedTextColor = NavyBlue,
    unfocusedTextColor = NavyBlue,
    cursorColor = NavyBlue,
    focusedContainerColor = White,
    unfocusedContainerColor = White,
)