package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.appColors

@Composable
fun WidgetPromoCard(onClick: () -> Unit) {
    val colors = MaterialTheme.appColors

    val card = PromoCardData(
        title = stringResource(R.string.widget_promo_title),
        description = stringResource(R.string.widget_promo_subtitle),
        icon = Icons.Filled.Widgets,
        background = colors.promoCardBackgroundGradient
    )

    PromoCard(
        card,
        onClick = onClick
    )
}

@Composable
fun VocabPromoCard(onClick: () -> Unit) {
    val colors = MaterialTheme.appColors

    val card = PromoCardData(
        title = stringResource(R.string.vocab_promo_title),
        description = stringResource(R.string.vocab_promo_description),
        icon = Icons.Filled.AddCircle,
        background = colors.promoCardBackgroundGradient)

    PromoCard(
        card,
        onClick = onClick
    )
}