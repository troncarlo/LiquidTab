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
    textHints = setOf("Chats", "Chat", "Contacts", "Settings", "Calls", "Profilo"),
    preferredSlots = 2..6,
    defaultAccentColor = 0xFF2AABEE.toInt(),
    glassAccentColor = 0xFF2AABEE.toInt(),
    sceneVisibilityAnimation = true,
)
