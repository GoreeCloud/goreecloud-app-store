package com.goreecloud.appstore

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppStoreNavigationSemanticsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationDestinationsExposeAtLeast48DpSemanticTargets() {
        val density = InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.displayMetrics.density

        listOf("Discover", "Apps", "Services", "Updates", "Library").forEach { label ->
            val interaction = composeRule.onNode(hasText(label))
            interaction
                .assertExists()
                .assertIsDisplayed()

            if (label == "Discover") {
                interaction.assertIsSelected()
            } else {
                interaction.assertHasClickAction()
            }

            val bounds = interaction.fetchSemanticsNode().boundsInRoot
            val widthDp = bounds.width / density
            val heightDp = bounds.height / density

            assertTrue(
                "$label semantic target width is ${widthDp}dp; expected at least 48dp",
                widthDp >= 48.0f,
            )
            assertTrue(
                "$label semantic target height is ${heightDp}dp; expected at least 48dp",
                heightDp >= 48.0f,
            )

            println("APP_STORE_NAV_TARGET label=$label widthDp=$widthDp heightDp=$heightDp")
        }
    }

    @Test
    fun bottomNavigationDestinationsDriveExpectedContent() {
        val expectations = listOf(
            "Apps" to "Search apps",
            "Services" to "Search services",
            "Updates" to "Updates are unavailable in this development build",
            "Library" to "Library history is unavailable in this development build",
            "Discover" to "Search apps and services",
        )

        expectations.forEach { (label, expectedContent) ->
            composeRule.onNode(hasText(label) and hasClickAction()).performClick()
            composeRule.waitForIdle()
            composeRule.onNode(hasText(label)).assertIsSelected()
            composeRule.onNode(hasText(expectedContent)).assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun categoryFiltersAreTouchSizedAndNarrowOnlyTheEntitledCatalog() {
        val density = InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.displayMetrics.density
        val productivity = composeRule.onNode(hasText("Productivity") and hasClickAction())

        productivity.assertExists().assertIsDisplayed().assertHasClickAction()
        val bounds = productivity.fetchSemanticsNode().boundsInRoot
        assertTrue(bounds.width / density >= 48.0f)
        assertTrue(bounds.height / density >= 48.0f)

        productivity.performClick()
        composeRule.waitForIdle()
        composeRule.onNode(hasText("4 items in this development catalog"))
            .assertExists()
            .assertIsDisplayed()

        composeRule.onNode(hasText("All") and hasClickAction()).performClick()
        composeRule.waitForIdle()
        composeRule.onNode(hasText("10 items in this development catalog"))
            .assertExists()
            .assertIsDisplayed()
    }
}
