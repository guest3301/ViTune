package app.vitune.android.ui.components.themed

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.vitune.android.LocalPlayerAwareWindowInsets
import app.vitune.android.utils.center
import app.vitune.android.utils.color
import app.vitune.android.utils.semiBold
import app.vitune.core.ui.Dimensions
import app.vitune.core.ui.LocalAppearance
import app.vitune.core.ui.utils.isLandscape
import app.vitune.core.ui.utils.roundedShape

@Composable
inline fun NavigationRail(
    topIconButtonId: Int,
    noinline onTopIconButtonClick: () -> Unit,
    tabIndex: Int,
    crossinline onTabIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(@Composable (Int, String, Int) -> Unit) -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current

    val isLandscape = isLandscape

    val paddingValues = LocalPlayerAwareWindowInsets.current
        .only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)
        .asPaddingValues()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .size(
                    width = if (isLandscape) Dimensions.navigationRail.widthLandscape
                    else Dimensions.navigationRail.width,
                    height = Dimensions.items.headerHeight
                )
        ) {
            Image(
                painter = painterResource(topIconButtonId),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorPalette.textSecondary),
                modifier = Modifier
                    .offset(
                        x = if (isLandscape) 0.dp else Dimensions.navigationRail.iconOffset,
                        y = 48.dp
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onTopIconButtonClick)
                    .padding(all = 12.dp)
                    .size(22.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(if (isLandscape) Dimensions.navigationRail.widthLandscape else Dimensions.navigationRail.width)
        ) {
            val transition = updateTransition(targetState = tabIndex, label = null)

            content { index, text, icon ->
                val dothAlpha by transition.animateFloat(label = "") {
                    if (it == index) 1f else 0f
                }

                val textColor by transition.animateColor(label = "") {
                    if (it == index) colorPalette.text else colorPalette.textDisabled
                }

                val iconContent: @Composable () -> Unit = {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.text),
                        modifier = Modifier
                            .vertical(enabled = !isLandscape)
                            .graphicsLayer {
                                alpha = dothAlpha
                                translationX = (1f - dothAlpha) * -48.dp.toPx()
                                rotationZ = if (isLandscape) 0f else -90f
                            }
                            .size(Dimensions.navigationRail.iconOffset * 2)
                    )
                }

                val textContent: @Composable () -> Unit = {
                    BasicText(
                        text = text,
                        style = typography.xs.semiBold.center.color(textColor),
                        modifier = Modifier
                            .vertical(enabled = !isLandscape)
                            .rotate(if (isLandscape) 0f else -90f)
                            .padding(horizontal = 16.dp)
                    )
                }

                val contentModifier = Modifier
                    .clip(24.dp.roundedShape)
                    .clickable(onClick = { onTabIndexChange(index) })

                if (isLandscape) Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = contentModifier.padding(vertical = 8.dp)
                ) {
                    iconContent()
                    textContent()
                } else Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = contentModifier.padding(horizontal = 8.dp)
                ) {
                    iconContent()
                    textContent()
                }
            }
        }
    }
}

fun Modifier.vertical(enabled: Boolean = true) =
    if (enabled)
        layout { measurable, constraints ->
            val placeable = measurable.measure(constraints.copy(maxWidth = Int.MAX_VALUE))
            layout(placeable.height, placeable.width) {
                placeable.place(
                    x = -(placeable.width / 2 - placeable.height / 2),
                    y = -(placeable.height / 2 - placeable.width / 2)
                )
            }
        } else this
