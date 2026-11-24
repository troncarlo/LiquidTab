package io.github.offlineglass.hook.adapters.telegram

import io.github.offlineglass.targets.AdapterSource
import io.github.offlineglass.targets.TargetSpec

internal val targetSpec: TargetSpec = TargetSpec(
    key = "telegram",
    packageName = "org.telegram.messenger",
    displayName = "Telegram",
    source = AdapterSource.DEDICATED,
    activityHints = setOf(
        "org.telegram.ui.LaunchActivity",
    ),
    idHints = setOf("bottom_navigation", "navigation_bar", "tab_bar"),
    classHints = setOf("LaunchActivity", "DialogsActivity"),
    textHints = setOf("Chat", "Contatti", "Chiamate", "Profilo", "Contacts", "Calls", "Profile"),
    preferredSlots = 3..5,
    defaultAccentColor = 0xFF2AABEE.toInt(),
    glassAccentColor = 0xFF2AABEE.toInt(),
    sceneVisibilityAnimation = true,
)
