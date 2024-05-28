package app.vitune.android.ui.components.themed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import app.vitune.core.ui.LocalAppearance

@Composable
fun Scaffold(
    topIconButtonId: Int,
    onTopIconButtonClick: () -> Unit,
    tabIndex: Int,
    onTabChange: (Int) -> Unit,
    tabColumnContent: @Composable ColumnScope.(@Composable (Int, String, Int) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val (colorPalette) = LocalAppearance.current

    Row(
        modifier = modifier
            .background(colorPalette.background0)
            .fillMaxSize()
    ) {
        NavigationRail(
            topIconButtonId = topIconButtonId,
            onTopIconButtonClick = onTopIconButtonClick,
            tabIndex = tabIndex,
            onTabIndexChange = onTabChange,
            content = tabColumnContent
        )

        AnimatedContent(
            targetState = tabIndex,
            transitionSpec = {
                val slideDirection = when (targetState > initialState) {
                    true -> AnimatedContentTransitionScope.SlideDirection.Up
                    false -> AnimatedContentTransitionScope.SlideDirection.Down
                }

                val animationSpec = spring(
                    dampingRatio = 0.9f,
                    stiffness = Spring.StiffnessLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )

                slideIntoContainer(slideDirection, animationSpec) togetherWith
                        slideOutOfContainer(slideDirection, animationSpec)
            },
            content = content,
            label = ""
        )
    }
}
