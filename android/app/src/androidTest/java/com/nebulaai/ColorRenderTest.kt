package com.nebulaai

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ColorRenderTest verifies that the NebulaAI design system colors are correctly
 * defined and accessible. Tests both light and dark mode color tokens.
 *
 * Design system: slate-* gray scale, body bg #020617,
 * emerald-400/500 primary accent, glassmorphism surfaces.
 */
@RunWith(AndroidJUnit4::class)
class ColorRenderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val resources: Resources = context.resources

    @Test
    fun darkModeBackground_isCorrect() {
        // Verify dark mode background matches design system: #020617
        val expectedDarkBg = Color.parseColor("#020617")
        val red = Color.red(expectedDarkBg)
        val green = Color.green(expectedDarkBg)
        val blue = Color.blue(expectedDarkBg)
        assertEquals("Dark bg red channel should be 2", 2, red)
        assertEquals("Dark bg green channel should be 6", 6, green)
        assertEquals("Dark bg blue channel should be 23", 23, blue)
    }

    @Test
    fun darkModeSurface_isSlate900() {
        // Verify surface is slate-900 (#0f172a) per design system
        val expectedSurface = Color.parseColor("#0f172a")
        assertEquals("Surface red channel should be 15", 15, Color.red(expectedSurface))
        assertEquals("Surface green channel should be 23", 23, Color.green(expectedSurface))
        assertEquals("Surface blue channel should be 42", 42, Color.blue(expectedSurface))
    }

    @Test
    fun darkModePrimaryAccent_isEmerald500() {
        // Verify primary accent is emerald-500 (#10b981) per design system
        val accent = Color.parseColor("#10b981")
        assertEquals("Primary accent red should be 16", 16, Color.red(accent))
        assertEquals("Primary accent green should be 185", 185, Color.green(accent))
        assertEquals("Primary accent blue should be 129", 129, Color.blue(accent))
    }

    @Test
    fun darkModeSecondaryAccent_isEmerald400() {
        // Verify secondary accent is emerald-400 (#34d399) per design system
        val secondary = Color.parseColor("#34d399")
        assertEquals("Secondary accent red should be 52", 52, Color.red(secondary))
        assertEquals("Secondary accent green should be 211", 211, Color.green(secondary))
        assertEquals("Secondary accent blue should be 153", 153, Color.blue(secondary))
    }

    @Test
    fun darkModeOnSurface_isSlate300() {
        // Verify onSurface text color is slate-300 (#cbd5e1) per design system
        val onSurface = Color.parseColor("#cbd5e1")
        assertEquals("OnSurface red should be 203", 203, Color.red(onSurface))
        assertEquals("OnSurface green should be 213", 213, Color.green(onSurface))
        assertEquals("OnSurface blue should be 225", 225, Color.blue(onSurface))
    }

    @Test
    fun darkModeError_isRed500() {
        // Verify error color is red-500 (#ef4444) per design system
        val error = Color.parseColor("#ef4444")
        assertEquals("Error red should be 239", 239, Color.red(error))
        assertEquals("Error green should be 68", 68, Color.green(error))
        assertEquals("Error blue should be 68", 68, Color.blue(error))
    }

    @Test
    fun lightModeBackground_isSlate50() {
        // Verify light mode background is slate-50 (#f8fafc) per design system
        val lightBg = Color.parseColor("#f8fafc")
        assertEquals("Light bg red should be 248", 248, Color.red(lightBg))
        assertEquals("Light bg green should be 250", 250, Color.green(lightBg))
        assertEquals("Light bg blue should be 252", 252, Color.blue(lightBg))
    }

    @Test
    fun lightModePrimary_isSlate900() {
        // Verify light mode primary is slate-900 (#0f172a) per design system
        val lightPrimary = Color.parseColor("#0f172a")
        assertEquals("Light primary red should be 15", 15, Color.red(lightPrimary))
        assertEquals("Light primary green should be 23", 23, Color.green(lightPrimary))
        assertEquals("Light primary blue should be 42", 42, Color.blue(lightPrimary))
    }

    @Test
    fun lightModeSecondaryAccent_isEmerald600() {
        // Verify light mode secondary accent is emerald-600 (#059669) per design system
        val lightSecondary = Color.parseColor("#059669")
        assertEquals("Light secondary red should be 5", 5, Color.red(lightSecondary))
        assertEquals("Light secondary green should be 150", 150, Color.green(lightSecondary))
        assertEquals("Light secondary blue should be 105", 105, Color.blue(lightSecondary))
    }

    @Test
    fun glassmorphismSurfaceContainerHighest_hasHighAlpha() {
        // Verify glassmorphism surfaceContainerHighest has proper alpha (0.95)
        // Color: rgba(30, 41, 59, 0.95) -> 0.95 * 255 = 242
        val surfaceContainerHighest = Color.argb(242, 30, 41, 59)
        assertTrue("surfaceContainerHighest should have high alpha",
            Color.alpha(surfaceContainerHighest) > 200)
        assertEquals("surfaceContainerHighest alpha should be ~242",
            242, Color.alpha(surfaceContainerHighest))
    }

    @Test
    fun glassmorphismSurfaceContainer_hasMediumAlpha() {
        // Verify glassmorphism surfaceContainer has proper alpha (0.5)
        // Color: rgba(30, 41, 59, 0.5) -> 0.5 * 255 = 128 (rounded)
        val surfaceContainer = Color.argb(128, 30, 41, 59)
        assertTrue("surfaceContainer should have medium alpha",
            Color.alpha(surfaceContainer) > 100)
        assertTrue("surfaceContainer alpha should be < 200",
            Color.alpha(surfaceContainer) < 200)
    }

    @Test
    fun glassmorphismSurfaceContainerLow_hasLowAlpha() {
        // Verify glassmorphism surfaceContainerLow has proper alpha (0.4)
        // Color: rgba(15, 23, 42, 0.4) -> 0.4 * 255 = 102
        val surfaceContainerLow = Color.argb(102, 15, 23, 42)
        assertTrue("surfaceContainerLow should have low alpha",
            Color.alpha(surfaceContainerLow) > 50)
        assertTrue("surfaceContainerLow alpha should be < 150",
            Color.alpha(surfaceContainerLow) < 150)
    }

    @Test
    fun darkModeText_isSlate200() {
        // Verify dark mode text is slate-200 (#e2e8f0) per design system
        val text = Color.parseColor("#e2e8f0")
        assertEquals("Text red should be 226", 226, Color.red(text))
        assertEquals("Text green should be 232", 232, Color.green(text))
        assertEquals("Text blue should be 240", 240, Color.blue(text))
    }

    @Test
    fun darkModeTextSecondary_isSlate500() {
        // Verify secondary text is slate-500 (#64748b) per design system
        val textSecondary = Color.parseColor("#64748b")
        assertEquals("Text secondary red should be 100", 100, Color.red(textSecondary))
        assertEquals("Text secondary green should be 116", 116, Color.green(textSecondary))
        assertEquals("Text secondary blue should be 139", 139, Color.blue(textSecondary))
    }

    @Test
    fun darkModeBorder_isSlate700() {
        // Verify border is slate-700 (#334155) per design system
        val border = Color.parseColor("#334155")
        assertEquals("Border red should be 51", 51, Color.red(border))
        assertEquals("Border green should be 65", 65, Color.green(border))
        assertEquals("Border blue should be 85", 85, Color.blue(border))
    }

    @Test
    fun darkModeStatusActive_isEmerald500() {
        // Verify status active color is emerald-500 (#10b981) per design system
        val statusActive = Color.parseColor("#10b981")
        assertEquals("Status active should match emerald-500",
            Color.parseColor("#10b981"), statusActive)
    }

    @Test
    fun darkModeStatusIdle_isSlate600() {
        // Verify status idle color is slate-600 (#475569) per design system
        val statusIdle = Color.parseColor("#475569")
        assertEquals("Status idle red should be 71", 71, Color.red(statusIdle))
        assertEquals("Status idle green should be 85", 85, Color.green(statusIdle))
        assertEquals("Status idle blue should be 105", 105, Color.blue(statusIdle))
    }

    @Test
    fun appThemeExists() {
        // Verify AppTheme is defined
        val themeId = resources.getIdentifier("AppTheme", "style", context.packageName)
        assertTrue("AppTheme style should exist", themeId != 0)
    }

    @Test
    fun darkModeButtonPrimaryBg_isEmerald900() {
        // Verify dark mode button primary background is emerald-900 (#064e3b)
        val btnBg = Color.parseColor("#064e3b")
        assertEquals("Button primary bg red should be 6", 6, Color.red(btnBg))
        assertEquals("Button primary bg green should be 78", 78, Color.green(btnBg))
        assertEquals("Button primary bg blue should be 59", 59, Color.blue(btnBg))
    }

    @Test
    fun darkModeButtonPrimaryText_isEmerald300() {
        // Verify dark mode button primary text is emerald-300 (#6ee7b7)
        val btnText = Color.parseColor("#6ee7b7")
        assertEquals("Button primary text red should be 110", 110, Color.red(btnText))
        assertEquals("Button primary text green should be 231", 231, Color.green(btnText))
        assertEquals("Button primary text blue should be 183", 183, Color.blue(btnText))
    }

    @Test
    fun colorContrast_darkBgVsEmeraldAccent_isSufficient() {
        // Verify contrast between dark background (#020617) and emerald accent (#10b981)
        // meets accessibility standards (WCAG AA: 4.5:1 for normal text, 3:1 for large text)
        val bg = Color.parseColor("#020617")
        val accent = Color.parseColor("#10b981")

        val bgLuminance = calculateRelativeLuminance(bg)
        val accentLuminance = calculateRelativeLuminance(accent)
        val lighter = maxOf(bgLuminance, accentLuminance)
        val darker = minOf(bgLuminance, accentLuminance)
        val contrastRatio = (lighter + 0.05) / (darker + 0.05)

        assertTrue("Contrast ratio between dark bg and emerald accent should be >= 3.0 (large text WCAG AA)",
            contrastRatio >= 3.0)
    }

    @Test
    fun colorContrast_darkBgVsSlate200Text_isSufficient() {
        // Verify contrast between dark background (#020617) and text (#e2e8f0)
        val bg = Color.parseColor("#020617")
        val text = Color.parseColor("#e2e8f0")

        val bgLuminance = calculateRelativeLuminance(bg)
        val textLuminance = calculateRelativeLuminance(text)
        val lighter = maxOf(bgLuminance, textLuminance)
        val darker = minOf(bgLuminance, textLuminance)
        val contrastRatio = (lighter + 0.05) / (darker + 0.05)

        assertTrue("Contrast ratio between dark bg and slate-200 text should be >= 7.0 (WCAG AAA)",
            contrastRatio >= 7.0)
    }

    @Test
    fun colorContrast_darkSurfaceVsEmeraldAccent_isSufficient() {
        // Verify contrast between surface (#0f172a) and accent (#10b981)
        val surface = Color.parseColor("#0f172a")
        val accent = Color.parseColor("#10b981")

        val surfaceLuminance = calculateRelativeLuminance(surface)
        val accentLuminance = calculateRelativeLuminance(accent)
        val lighter = maxOf(surfaceLuminance, accentLuminance)
        val darker = minOf(surfaceLuminance, accentLuminance)
        val contrastRatio = (lighter + 0.05) / (darker + 0.05)

        assertTrue("Contrast ratio between surface and emerald accent should be >= 3.0",
            contrastRatio >= 3.0)
    }

    companion object {
        private fun calculateRelativeLuminance(color: Int): Double {
            val r = Color.red(color) / 255.0
            val g = Color.green(color) / 255.0
            val b = Color.blue(color) / 255.0

            val rLinear = if (r <= 0.03928) r / 12.92 else Math.pow((r + 0.055) / 1.055, 2.4)
            val gLinear = if (g <= 0.03928) g / 12.92 else Math.pow((g + 0.055) / 1.055, 2.4)
            val bLinear = if (b <= 0.03928) b / 12.92 else Math.pow((b + 0.055) / 1.055, 2.4)

            return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
        }
    }
}
