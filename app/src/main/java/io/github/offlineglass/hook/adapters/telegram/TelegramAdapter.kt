package io.github.offlineglass.hook.adapters.telegram

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.robv.android.xposed.XposedBridge
import io.github.offlineglass.config.GlassConfig
import io.github.offlineglass.hook.NavigationCandidate
import io.github.offlineglass.hook.adapters.AdapterNavigationSearch
import io.github.offlineglass.hook.adapters.TargetAdapter
import java.util.Collections
import java.util.WeakHashMap

internal object TelegramAdapter : TargetAdapter {
    override val key = "telegram"
    override val usesContentDarkMode = true
    override val suppressPlatformElevation = true

    private val blockedLabels = setOf(
        "impostazioni",
        "settings",
        "impo",
        "cat",
    )

    private val configuredViews: MutableSet<View> =
        Collections.newSetFromMap(WeakHashMap())
    private val seenLabels: MutableSet<String> = mutableSetOf()
    private var lastSignature: String = ""

    override fun configureSourceText(view: TextView, config: GlassConfig, density: Float): Boolean {
        val label = view.text?.toString()?.trim().orEmpty()
        val normalized = label.lowercase()
        if (normalized.isEmpty()) {
            view.visibility = View.GONE
            return false
        }

        if (normalized in blockedLabels) {
            view.visibility = View.GONE
            return false
        }

        if (view in configuredViews) {
            return true
        }

        if (normalized in seenLabels) {
            view.visibility = View.GONE
            return false
        }

        seenLabels.add(normalized)
        configuredViews.add(view)
        view.maxLines = 1
        view.ellipsize = TextUtils.TruncateAt.END
        view.textSize = config.textSize
        view.scaleX = 1f
        view.scaleY = 1f
        return true
    }

    override fun configureSourceIcon(view: ImageView, config: GlassConfig, density: Float): Boolean {
        if (view.drawable == null || view.visibility != View.VISIBLE) return false
        if (view in configuredViews) {
            return true
        }
        configuredViews.add(view)
        view.scaleX = 1f
        view.scaleY = 1f
        return true
    }

    override fun onNavigationCandidates(candidates: List<NavigationCandidate>) {
        if (candidates.isEmpty()) return
        val filtered = candidates.filter { candidate ->
            candidate.view.width > 0 && candidate.view.height > 0
        }
        if (filtered.isEmpty()) return

        val hasInnerTelegramBar = filtered.any { candidate ->
            candidate.view.javaClass.name.startsWith("org.telegram.ui.") &&
                candidate.view.javaClass.name != "android.widget.FrameLayout"
        }

        if (hasInnerTelegramBar) {
            filtered.forEach { candidate ->
                val className = candidate.view.javaClass.name
                if (className == "android.widget.FrameLayout" && candidate.view is ViewGroup) {
                    for (i in 0 until candidate.view.childCount) {
                        val child = candidate.view.getChildAt(i)
                        if (!child.javaClass.name.startsWith("org.telegram.ui.")) {
                            child.visibility = View.GONE
                        }
                    }
                }
            }
        }

        val signature = filtered.joinToString("|") { candidate ->
            "${candidate.view.javaClass.name}:${candidate.slotCount}:${candidate.view.width}x${candidate.view.height}"
        }
        if (signature != lastSignature) {
            lastSignature = signature
            seenLabels.clear()
        }

        XposedBridge.log(
            "[OfflineGlass][TelegramDiag] candidates=${filtered.joinToString { candidate ->
                "${candidate.view.javaClass.name}/" +
                    "${AdapterNavigationSearch.resourceEntryName(candidate.view) ?: "no-id"}/" +
                    "slots=${candidate.slotCount}/score=${candidate.score}/" +
                    "${candidate.view.width}x${candidate.view.height}"
            }}",
        )
    }
}
