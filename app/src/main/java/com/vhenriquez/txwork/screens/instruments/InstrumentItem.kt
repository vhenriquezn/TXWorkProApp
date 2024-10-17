package com.vhenriquez.txwork.screens.instruments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.PopUpMenu
import com.vhenriquez.txwork.common.composable.TextSpannable
import com.vhenriquez.txwork.model.InstrumentEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InstrumentItem(
    instrument: InstrumentEntity,
    selectedActivityId: String? = null,
    containerColor: Color? = null,
    onActionClick: (Int) -> Unit,
    onClick: (InstrumentEntity) -> Unit) {

    var isExpandedMenu by remember { mutableStateOf(false) }
    val options = listOf("Copiar", "Editar", "Eliminar", "Cancelar")

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor ?: CardDefaults.cardColors().containerColor),
        modifier = Modifier
            //.then(modifier)
            .fillMaxWidth()
            .padding(3.dp, 2.dp, 3.dp, 2.dp)
            .combinedClickable(
                onClick = { onClick(instrument) }, onLongClick = { isExpandedMenu = true }
            ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (selectedActivityId != null){
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(92.dp)
                        .background(Color.Green)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(1f)
            ) {
                Text(
                    text = instrument.tag,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextSpannable(textSpannable = R.string.card_instrumentType, text = instrument.instrumentType, modifier = Modifier.weight(0.6f))
                    TextSpannable(textSpannable = R.string.card_instrumentMagnitude, text = instrument.magnitude, modifier = Modifier.weight(0.6f))
                }
                TextSpannable(textSpannable = R.string.card_instrumentVerification, text = instrument.getRangeVerification(), modifier = Modifier)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextSpannable(textSpannable = R.string.card_instrumentBusiness, text = instrument.business, modifier = Modifier.weight(0.6f))
                    TextSpannable(textSpannable = R.string.card_instrumentArea, text = instrument.area, modifier = Modifier.weight(0.6f))
                }

            }
        }
        PopUpMenu(options, Modifier.wrapContentWidth(), onActionClick, isExpandedMenu) {
            isExpandedMenu = !isExpandedMenu
        }
    }
}