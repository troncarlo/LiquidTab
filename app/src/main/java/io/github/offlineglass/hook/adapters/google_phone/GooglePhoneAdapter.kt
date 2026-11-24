package io.github.offlineglass.hook.adapters.google_phone

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import de.robv.android.xposed.XposedBridge
import io.github.offlineglass.config.GlassConfig
import io.github.offlineglass.hook.NavigationCandidate
import io.github.offlineglass.hook.adapters.AdapterNavigationSearch
import io.github.offlineglass.hook.adapters.TargetAdapter

internal object GooglePhoneAdapter : TargetAdapter {
    override val key = "google_phone"
    override val usesContentDarkMode = true
    override val suppressPlatformElevation = true
    override val sourceIconScale: Float = 1.65f
    override val projectedNavigationScale: Float = 1.25f

    private const val ICON_SCALE = 1.65f

    override fun configureSourceText(view: TextView, config: GlassConfig, density: Float): Boolean {
        view.text = ""
        view.visibility = View.GONE
        return true
    }

    override fun configureSourceIcon(view: ImageView, config: GlassConfig, density: Float): Boolean {
        val drawable = view.drawable ?: return false
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) return false

        view.background = null
        view.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)

        val wrapped = DrawableCompat.wrap(drawable.mutate())
        val tintColor = if (view.isSelected) targetSpec.glassAccentColor else Color.WHITE
        DrawableCompat.setTint(wrapped, tintColor)
        view.setImageDrawable(wrapped)

        view.visibility = View.VISIBLE
        view.scaleX = ICON_SCALE
        view.scaleY = ICON_SCALE
        return true
    }

    override fun suppressNativeBottomChrome(navigation: ViewGroup) {
        fun cleanTree(group: ViewGroup) {
            for (i in 0 until group.childCount) {
                val child = group.getChildAt(i)
                if (child is TextView) {
                    child.text = ""
                    child.visibility = View.GONE
                } else if (child is ImageView) {
                    child.background = null
                    child.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                } else if (child is ViewGroup) {
                    val className = child.javaClass.name
                    if (className.contains("Indicator", ignoreCase = true) ||
                        className.contains("Badge", ignoreCase = true)
                    ) {
                        child.background = null
                        child.visibility = View.GONE
                    } else {
                        cleanTree(child)
                    }
                }
            }
        }
        cleanTree(navigation)
    }

    override fun onNavigationCandidates(candidates: List<NavigationCandidate>) {
        if (candidates.isEmpty()) return
        val filtered = candidates.filter { candidate ->
            candidate.view.width > 0 && candidate.view.height > 0
        }
        if (filtered.isEmpty()) return
        XposedBridge.log(
            "[OfflineGlass][PhoneDiag] candidates=${filtered.joinToString { candidate ->
                "${candidate.view.javaClass.name}/" +
                    "${AdapterNavigationSearch.resourceEntryName(candidate.view) ?: "no-id"}/" +
                    "slots=${candidate.slotCount}/score=${candidate.score}/" +
                    "${candidate.view.width}x${candidate.view.height}"
            }}",
        )
    }
}
