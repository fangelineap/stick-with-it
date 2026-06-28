package com.stickwithit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.stickwithit.R

val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val nunitoSans = GoogleFont("Nunito Sans")

val NunitoSansFontFamily = FontFamily(
    Font(googleFont = nunitoSans, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = nunitoSans, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = nunitoSans, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = nunitoSans, fontProvider = googleFontProvider, weight = FontWeight.Bold),
    Font(googleFont = nunitoSans, fontProvider = googleFontProvider, weight = FontWeight.ExtraBold),
)

private fun nunito(
    weight: FontWeight,
    size: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit,
    letterSpacing: androidx.compose.ui.unit.TextUnit = 0.sp,
) = TextStyle(
    fontFamily = NunitoSansFontFamily,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)

val Typography = Typography(
    displayLarge  = nunito(FontWeight.Normal,   57.sp, 64.sp, (-0.25).sp),
    displayMedium = nunito(FontWeight.Normal,   45.sp, 52.sp),
    displaySmall  = nunito(FontWeight.Normal,   36.sp, 44.sp),
    headlineLarge  = nunito(FontWeight.Bold,    32.sp, 40.sp),
    headlineMedium = nunito(FontWeight.Bold,    28.sp, 36.sp),
    headlineSmall  = nunito(FontWeight.Bold,    24.sp, 32.sp),
    titleLarge  = nunito(FontWeight.SemiBold,   22.sp, 28.sp),
    titleMedium = nunito(FontWeight.SemiBold,   16.sp, 24.sp, 0.15.sp),
    titleSmall  = nunito(FontWeight.Medium,     14.sp, 20.sp, 0.1.sp),
    bodyLarge   = nunito(FontWeight.Normal,     16.sp, 24.sp, 0.5.sp),
    bodyMedium  = nunito(FontWeight.Normal,     14.sp, 20.sp, 0.25.sp),
    bodySmall   = nunito(FontWeight.Normal,     12.sp, 16.sp, 0.4.sp),
    labelLarge  = nunito(FontWeight.Medium,     14.sp, 20.sp, 0.1.sp),
    labelMedium = nunito(FontWeight.Medium,     12.sp, 16.sp, 0.5.sp),
    labelSmall  = nunito(FontWeight.Medium,     11.sp, 16.sp, 0.5.sp),
)
