package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a crisp, DPI-scaled Islamic 8-pointed star (Rub el Hizb) geometric lattice watermark.
 */
@Composable
fun IslamicGeometricWatermark(
    modifier: Modifier = Modifier,
    tint: Color = IslamicGold.copy(alpha = 0.12f),
    stepDp: Dp = 72.dp
) {
    Canvas(modifier = modifier) {
        val step = stepDp.toPx()
        val stroke = Stroke(
            width = 1.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val cols = (size.width / step).toInt() + 2
        val rows = (size.height / step).toInt() + 2
        val radius = step * 0.34f

        for (r in 0..rows) {
            for (c in 0..cols) {
                val cx = c * step + if (r % 2 == 1) step / 2f else 0f
                val cy = r * step
                drawEightPointStar(cx, cy, radius, tint, stroke)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEightPointStar(
    cx: Float,
    cy: Float,
    radius: Float,
    color: Color,
    stroke: Stroke
) {
    val square1 = Path().apply {
        for (i in 0 until 4) {
            val angle = Math.toRadians((i * 90 + 45).toDouble())
            val x = cx + (radius * cos(angle)).toFloat()
            val y = cy + (radius * sin(angle)).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    val square2 = Path().apply {
        for (i in 0 until 4) {
            val angle = Math.toRadians((i * 90).toDouble())
            val x = cx + (radius * cos(angle)).toFloat()
            val y = cy + (radius * sin(angle)).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(square1, color = color, style = stroke)
    drawPath(square2, color = color, style = stroke)
    drawCircle(color = color, radius = radius * 0.22f, center = Offset(cx, cy), style = stroke)
}

@Composable
fun GoldCalligraphyDivider(
    modifier: Modifier = Modifier,
    color: Color = IslamicGold
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, color.copy(alpha = 0.85f))
                    )
                )
        )
        Canvas(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(16.dp)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.minDimension * 0.44f
            val diamond = Path().apply {
                moveTo(cx, cy - r)
                lineTo(cx + r, cy)
                lineTo(cx, cy + r)
                lineTo(cx - r, cy)
                close()
            }
            drawPath(diamond, color = color)
            drawCircle(
                color = color.copy(alpha = 0.55f),
                radius = r * 1.1f,
                style = Stroke(width = 1.dp.toPx())
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(color.copy(alpha = 0.85f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun RahabarLogoImage(
    modifier: Modifier = Modifier,
    contentDescription: String = "Monthly Rahabar Magazine Logo"
) {
    val resources = LocalContext.current.resources
    val logoBitmap = remember {
        ImageBitmap.imageResource(resources, R.drawable.rahabar_logo)
    }
    Image(
        bitmap = logoBitmap,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.High,
        modifier = modifier
    )
}

@Composable
fun RahabarLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    RahabarLogoImage(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(1.5.dp, IslamicGold, CircleShape)
    )
}

@Composable
fun DeveloperCreditCard(
    modifier: Modifier = Modifier,
    isBengali: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = IslamicNavyDeep
        ),
        border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.6f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IslamicGeometricWatermark(
                modifier = Modifier.matchParentSize(),
                tint = IslamicGold.copy(alpha = 0.09f),
                stepDp = 56.dp
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    RahabarLogoBadge(size = 40.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "MONTHLY RAHABAR MAGAZINE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = DisplayEnglishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGold
                        )
                    )
                }
                Spacer(Modifier.height(10.dp))
                GoldCalligraphyDivider()
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Developed & Owned by Ahnaf Salman",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = BodyEnglishFontFamily,
                        color = WarmCream
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Email: ahnafsalman555@gmail.com",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = BodyEnglishFontFamily,
                        color = IslamicGoldLight
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isBengali) {
                        "© ২০২৬ মাসিক রাহাবার ম্যাগাজিন • সর্বস্বত্ব সংরক্ষিত"
                    } else {
                        "© 2026 Monthly Rahabar Magazine • All Rights Reserved"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = if (isBengali) AdorsholipiFontFamily else BodyEnglishFontFamily,
                        color = TextOnDarkSecondary
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
