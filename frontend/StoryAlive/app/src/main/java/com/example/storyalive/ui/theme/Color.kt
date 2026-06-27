package com.example.storyalive.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFFFF0D1)
val PurpleGrey80 = Color(0xFFD8C5B6)
val Pink80 = Color(0xFFE8D8C2)

val Purple40 = Color(0xFF5E372B)
val PurpleGrey40 = Color(0xFF917A73)
val Pink40 = Color(0xFF7A5B52)


// Light theme colors
val LightBackground = Color(0xFFFFF0D1)
val LightCard = Color(0xFFFFF8EA)
val LightText = Color(0xFF5E372B)
val LightHeading = Color(0xFF4A2B21)
val LightMuted = Color(0xFF917A73)
val LightAccent = Color(0xFF5E372B)


// Dark theme colors
val DarkBackground = Color(0xFF5E372B)
val DarkCard = Color(0xFF6B4336)
val DarkText = Color(0xFFFFF0D1)
val DarkHeading = Color(0xFFFFFFFF)
val DarkMuted = Color(0xFFD4C1BB)
val DarkAccent = Color(0xFF917A73)

// -------- DATA CLASS FOR EASY ACCESS --------
data class ThemeColors(
    val background: Color,
    val card: Color,
    val text: Color,
    val heading: Color,
    val muted: Color,
    val accent: Color,
    val buttonText: Color
)

// -------- HELPER FUNCTION --------
@Composable
@ReadOnlyComposable
fun themeColors(isLightTheme: Boolean) = ThemeColors(
    background = if (isLightTheme) LightBackground else DarkBackground,
    card = if (isLightTheme) LightCard else DarkCard,
    text = if (isLightTheme) LightText else DarkText,
    heading = if (isLightTheme) LightHeading else DarkHeading,
    muted = if (isLightTheme) LightMuted else DarkMuted,
    accent = if (isLightTheme) LightAccent else DarkAccent,
    buttonText = if (isLightTheme) Color.White else Color(0xFFFFF0D1)
)
//package com.example.storyalive.ui.theme
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.ReadOnlyComposable
//import androidx.compose.ui.graphics.Color
//
//val Purple80 = Color(0xFFD0BCFF)
//val PurpleGrey80 = Color(0xFFCCC2DC)
//val Pink80 = Color(0xFFEFB8C8)
//
//val Purple40 = Color(0xFF6650a4)
//val PurpleGrey40 = Color(0xFF625b71)
//val Pink40 = Color(0xFF7D5260)
//
//
//// Light theme colors
//val LightBackground = Color(0xFFECF2FF)
//val LightCard = Color.White
//val LightText = Color(0xFF374151)
//val LightHeading = Color(0xFF111827)
//val LightMuted = Color(0xFF6B7280)
//val LightAccent = Color(0xFF3E54AC)
//
//
//// Dark theme colors
//val DarkBackground = Color(0xFF213C51)
//val DarkCard = Color(0xFF2A4A5E)
//val DarkText = Color(0xFF000000)
//val DarkHeading = Color.White
//val DarkMuted = Color(0xFF9CA3AF)
//val DarkAccent = Color(0xFFDDAED3)
//
//// -------- DATA CLASS FOR EASY ACCESS --------
//data class ThemeColors(
//    val background: Color,
//    val card: Color,
//    val text: Color,
//    val heading: Color,
//    val muted: Color,
//    val accent: Color,
//    val buttonText: Color
//)
//
//// -------- HELPER FUNCTION --------
//@Composable
//@ReadOnlyComposable
//fun themeColors(isLightTheme: Boolean) = ThemeColors(
//    background = if (isLightTheme) LightBackground else DarkBackground,
//    card = if (isLightTheme) LightCard else DarkCard,
//    text = if (isLightTheme) LightText else DarkText,
//    heading = if (isLightTheme) LightHeading else DarkHeading,
//    muted = if (isLightTheme) LightMuted else DarkMuted,
//    accent = if (isLightTheme) LightAccent else DarkAccent,
//    buttonText = if (isLightTheme) Color.White else Color(0xFF1F2937)
//)
