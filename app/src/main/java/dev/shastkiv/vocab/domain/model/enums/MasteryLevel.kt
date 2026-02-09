package dev.shastkiv.vocab.domain.model.enums

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

data class MasteryLevel(
    val score: Int,
    val startColor: Color,
    val endColor: Color
) {
    val percentage: Int = score.coerceIn(0, 100)

    val color: Color
        get() = MasteryColors.getColor(
            percentage,
            startColor,
            endColor)

    val stageName: String
        get() = when (percentage) {
            in 0..20 -> "Discovery"
            in 21..40 -> "Acquisition"
            in 41..60 -> "Retention"
            in 61..80 -> "Consolidation"
            in 81..100 -> "Mastery"
            else -> "Discovery"
        }

    companion object {
        fun fromScore(score: Int,
                      startColor: Color,
                      endColor: Color
        ) = MasteryLevel(
            score,
            startColor,
            endColor

        )

        fun getEnergyColor(score: Int,
                           startColor: Color,
                           endColor: Color
        ): Color {
            return MasteryColors.getColor(
                percentage = score.coerceIn(0, 100),
                startColor = startColor,
                endColor = endColor
            )
        }

        fun getEnergyColorUltraBright(score: Int,
                                      startColor: Color,
                                      endColor: Color): Color {
            val baseColor = getEnergyColor(score,
                startColor = startColor,
                endColor = endColor)
            return lerp(baseColor, Color.White, 0.3f)
        }

        fun getEnergyColorDeep(score: Int,
                               startColor: Color,
                               endColor: Color
        ): Color {
            val baseColor = getEnergyColor(score,
                startColor = startColor,
                endColor = endColor
            )
            return baseColor.copy(alpha = 0.7f)
        }
    }
}

object MasteryColors {

    fun getColor(
        percentage: Int,
        startColor: Color,
        endColor: Color
    ): Color {
        val fraction = percentage.coerceIn(0, 100) / 100f
        return lerp(startColor, endColor, fraction)
    }
}