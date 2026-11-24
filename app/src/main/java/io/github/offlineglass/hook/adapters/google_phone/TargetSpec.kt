package io.github.offlineglass.hook.adapters.google_phone

import io.github.offlineglass.targets.AdapterSource
import io.github.offlineglass.targets.TargetSpec

internal val targetSpec: TargetSpec = TargetSpec(
    key = "google_phone",
    packageName = "com.google.android.dialer",
    displayName = "Phone",
    source = AdapterSource.DEDICATED,
    activityHints = setOf(
        "com.android.dialer.main.impl.MainActivity",
        "com.google.android.dialer.extensions.GoogleDialtactsActivity",
    ),
    idHints = setOf("navigation_bar_layout", "bottom_nav", "navigation_bar", "bottom_navigation_view"),
    classHints = setOf("BottomNavigationView"),
    textHints = setOf(
        "Home", "Keypad", "Favorites", "Recents", "Contacts", "Voicemail",
        "Preferiti", "Recenti", "Contatti", "Segreteria", "Tastierino",
    ),
    preferredSlots = 2..5,
    defaultAccentColor = 0xFF1A73E8.toInt(),
    glassAccentColor = 0xFF1A73E8.toInt(),
    sceneVisibilityAnimation = true,
)
