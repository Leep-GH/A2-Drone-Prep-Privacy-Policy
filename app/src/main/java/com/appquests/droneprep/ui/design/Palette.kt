package com.appquests.droneprep.ui.design

import androidx.compose.ui.graphics.Color

object Palette {
    // Base / Backgrounds
    val Bg = Color(0xFF0B0E13)        // Primary Background: near black, navy-tinted
    val Card = Color(0xFF161B22)      // Card / Container Background: slightly lighter dark gray

    // Typography
    val TextPrimary = Color(0xFFFFFFFF)    // Primary Text: white for main headings and body
    val TextSecondary = Color(0xFF9CA3AF)  // Secondary Text: light gray for subtext, labels
    val TextDisabled = Color(0xFF6B7280)   // Disabled Text: muted gray for inactive states

    // Primary Accent
    val AccentOrange = Color(0xFFFF8A34)      // CTA Buttons: bright orange for Start, key actions
    val AccentOrangeActive = Color(0xFFFF9F1C) // CTA Hover/Active: lighter vibrant orange highlight

    // Secondary Accent  
    val AccentBlue = Color(0xFF4F8CFF)        // Interactive Elements: electric blue for sliders, chips
    val AccentBlueActive = Color(0xFF3B75E6)  // Hover/Active: slightly darker blue for press states

    // Icons & Feedback
    val IconNeutral = Color(0xFFB0B6BC)    // Icons (Neutral)
    val Success = Color(0xFF22C55E)        // Success/Positive (optional): green
    val Error = Color(0xFFEF4444)          // Error/Negative (optional): red

    // NEW: Brand accents
    val BrandYellow = Color(0xFFFFD54A)
    val BrandYellowPressed = Color(0xFFFFC107)
}
