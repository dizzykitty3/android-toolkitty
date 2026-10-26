package me.dizzykitty3.androidtoolkitty.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultTypography = Typography()

val AppTypography = defaultTypography.copy(
    titleLarge = defaultTypography.titleLarge.copy(fontWeight = FontWeight.Medium),
    titleMedium = defaultTypography.titleMedium.copy(fontSize = 18.sp, lineHeight = 24.sp),
    titleSmall = defaultTypography.titleSmall.copy(fontSize = 16.sp, lineHeight = 24.sp),
)
