package com.vhenriquez.txwork.screens.activities

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.DropdownContextMenu
import com.vhenriquez.txwork.common.composable.PopUpMenu
import com.vhenriquez.txwork.common.composable.TextSpannable
import com.vhenriquez.txwork.model.ActivityEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityItem(
    activity: ActivityEntity,
    onActionClick: (Int) -> Unit,
    onClick: () -> Unit) {
    var isExpandedMenu by remember { mutableStateOf(false) }
    val options = listOf("Editar","Eliminar","Usuarios","Reports", if (activity.status == "open") "Cerrar Actividad" else "Abrir Actividad","Cancelar")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp, 2.dp, 3.dp, 2.dp)
            .combinedClickable(
                onClick = { onClick() }, onLongClick = {isExpandedMenu = true }),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextSpannable(textSpannable = R.string.card_business, text = activity.business, modifier = Modifier.weight(0.6f))
                TextSpannable(textSpannable = R.string.card_date, text = activity.date, modifier = Modifier.weight(0.6f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextSpannable(textSpannable = R.string.card_serviceOrder, text = activity.serviceOrder, modifier = Modifier.weight(0.6f))
                TextSpannable(textSpannable = R.string.card_workOrder, text = activity.workOrder, modifier = Modifier.weight(0.6f))
            }
            TextSpannable(textSpannable = R.string.card_author, text = activity.ownerName, modifier = Modifier)
        }

        PopUpMenu(options, Modifier.wrapContentWidth(), onActionClick, isExpandedMenu) {
            isExpandedMenu = !isExpandedMenu
        }
    }
}