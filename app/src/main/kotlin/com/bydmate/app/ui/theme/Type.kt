package com.bydmate.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.bydmate.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val BYDMateTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp
    )
)

/** Arabic UI font (Tajawal, SIL OFL), used only when the app language is Arabic. */
val TajawalFontFamily = FontFamily(
    Font(R.font.tajawal_regular, FontWeight.Normal),
    Font(R.font.tajawal_medium, FontWeight.Medium),
    Font(R.font.tajawal_medium, FontWeight.SemiBold),
    Font(R.font.tajawal_bold, FontWeight.Bold),
)

/** [BYDMateTypography] with every style switched to [TajawalFontFamily]. */
val BYDMateArabicTypography: Typography = BYDMateTypography.run {
    fun TextStyle.ar() = copy(fontFamily = TajawalFontFamily)
    copy(
        displayLarge = displayLarge.ar(), displayMedium = displayMedium.ar(), displaySmall = displaySmall.ar(),
        headlineLarge = headlineLarge.ar(), headlineMedium = headlineMedium.ar(), headlineSmall = headlineSmall.ar(),
        titleLarge = titleLarge.ar(), titleMedium = titleMedium.ar(), titleSmall = titleSmall.ar(),
        bodyLarge = bodyLarge.ar(), bodyMedium = bodyMedium.ar(), bodySmall = bodySmall.ar(),
        labelLarge = labelLarge.ar(), labelMedium = labelMedium.ar(), labelSmall = labelSmall.ar(),
    )
}
