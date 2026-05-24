package com.predicta.app.ui.modifier

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a performant frosted "liquid glass" surface to a container.
 *
 * Important platform note: Jetpack Compose does not expose a generic backdrop-filter API that can
 * blur arbitrary pixels already drawn behind this composable. On Android 12+ this modifier uses
 * native [Modifier.blur] on the current layer; for text-heavy containers, apply this modifier to a
 * background Box behind the content if you want children to stay perfectly sharp. On older APIs the
 * modifier gracefully falls back to a stronger matte tint, animated liquid highlight, and luminous
 * border. If real pre-Android-12 backdrop blur is mandatory, capture/pre-blur a bitmap behind the
 * surface off the main thread, or wire in a dedicated backdrop library.
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    blurRadius: Dp = 18.dp,
    tintColor: Color = Color.Unspecified,
    tintAlpha: Float = Float.NaN,
    borderWidth: Dp = 1.dp,
    liquidIntensity: Float = 1f,
    isActive: Boolean = false,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "liquidGlass"
        properties["shape"] = shape
        properties["blurRadius"] = blurRadius
        properties["tintColor"] = tintColor
        properties["tintAlpha"] = tintAlpha
        properties["borderWidth"] = borderWidth
        properties["liquidIntensity"] = liquidIntensity
        properties["isActive"] = isActive
    },
) {
    val colorScheme = MaterialTheme.colorScheme
    val darkTheme = colorScheme.background.luminance() < 0.5f || isSystemInDarkTheme()
    val supportsNativeBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0.dp

    val defaultTint = if (darkTheme) colorScheme.surfaceVariant else Color.White
    val defaultTintAlpha = if (darkTheme) 0.34f else 0.54f
    val fallbackBoost = if (supportsNativeBlur) 0f else if (darkTheme) 0.12f else 0.18f
    val resolvedTint = (if (tintColor.isSpecified) tintColor else defaultTint).copy(
        alpha = (if (tintAlpha.isNaN()) defaultTintAlpha else tintAlpha)
            .plus(fallbackBoost)
            .coerceIn(0f, 0.92f),
    )

    val transition = rememberInfiniteTransition(label = "liquid_glass")
    val shimmerProgress by transition.animateFloat(
        initialValue = -0.85f,
        targetValue = 1.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isActive) 2800 else 6200,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "liquid_glass_shimmer",
    )
    val activePulse by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "liquid_glass_pulse",
    )
    val pulse = if (isActive) activePulse else 1f

    val blurModifier = if (supportsNativeBlur) {
        Modifier.blur(
            radius = blurRadius,
            edgeTreatment = BlurredEdgeTreatment(shape),
        )
    } else {
        Modifier
    }

    this
        .then(blurModifier)
        .clip(shape)
        .drawWithCache {
            val outline = shape.createOutline(size, layoutDirection, this)
            val width = size.width.coerceAtLeast(1f)
            val height = size.height.coerceAtLeast(1f)
            val diagonal = width + height
            val shimmerCenter = diagonal * shimmerProgress
            val glintWidth = width * 0.34f
            val tintOverlayAlpha = (resolvedTint.alpha * 0.32f * pulse).coerceIn(0f, 0.24f)
            val liquidAlpha = (0.105f * liquidIntensity * pulse).coerceIn(0f, 0.28f)
            val borderAlpha = (if (darkTheme) 0.42f else 0.52f) * pulse

            val liquidBrush = Brush.linearGradient(
                colorStops = arrayOf(
                    0.00f to Color.Transparent,
                    0.42f to Color.White.copy(alpha = 0.02f),
                    0.50f to Color.White.copy(alpha = 0.95f),
                    0.58f to Color.White.copy(alpha = 0.02f),
                    1.00f to Color.Transparent,
                ),
                start = Offset(shimmerCenter - glintWidth, height),
                end = Offset(shimmerCenter + glintWidth, 0f),
            )

            val borderBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (darkTheme) 0.18f else 0.74f),
                    colorScheme.primary.copy(alpha = if (darkTheme) 0.42f else 0.24f),
                    colorScheme.outline.copy(alpha = if (darkTheme) 0.18f else 0.32f),
                ),
                start = Offset.Zero,
                end = Offset(width, height),
            )

            onDrawWithContent {
                drawLiquidOutline(
                    outline = outline,
                    brush = SolidColor(resolvedTint),
                )
                drawContent()
                drawLiquidOutline(
                    outline = outline,
                    brush = SolidColor(resolvedTint),
                    alpha = tintOverlayAlpha,
                )
                drawLiquidOutline(
                    outline = outline,
                    brush = liquidBrush,
                    alpha = liquidAlpha,
                    blendMode = BlendMode.Screen,
                )
                drawLiquidOutline(
                    outline = outline,
                    brush = borderBrush,
                    alpha = borderAlpha.coerceIn(0f, 1f),
                    style = Stroke(width = borderWidth.toPx()),
                )
            }
        }
}

private fun DrawScope.drawLiquidOutline(
    outline: Outline,
    brush: Brush,
    alpha: Float = 1f,
    style: DrawStyle = Fill,
    blendMode: BlendMode = BlendMode.SrcOver,
) {
    when (outline) {
        is Outline.Rectangle -> drawRect(
            brush = brush,
            topLeft = outline.rect.topLeft,
            size = outline.rect.size,
            alpha = alpha,
            style = style,
            blendMode = blendMode,
        )

        is Outline.Rounded -> drawPath(
            path = Path().apply { addRoundRect(outline.roundRect) },
            brush = brush,
            alpha = alpha,
            style = style,
            blendMode = blendMode,
        )

        is Outline.Generic -> drawPath(
            path = outline.path,
            brush = brush,
            alpha = alpha,
            style = style,
            blendMode = blendMode,
        )
    }
}
