package com.vhenriquez.txwork.screens.certificates

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.PopUpMenu
import com.vhenriquez.txwork.common.composable.TextSpannable
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.PatternEntity
import com.vhenriquez.txwork.utils.CommonUtils.getColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CertificateItem(
    certificate: CertificateEntity,
    onActionClick: (Int) -> Unit,
    onClick: () -> Unit) {
    var isExpandedMenu by remember { mutableStateOf(false) }
    val options = listOf("Eliminar","Cancelar")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp, 2.dp, 3.dp, 2.dp)
            .combinedClickable(
                onClick = { onClick() }, onLongClick = {isExpandedMenu = true }),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(92.dp)
                    .background(getColor(certificate.broadcastDate))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(
                    text = certificate.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TextSpannable(textSpannable = R.string.card_certificateLaboratory, text = certificate.laboratory, modifier = Modifier)
                TextSpannable(textSpannable = R.string.card_certificateDate, text = certificate.broadcastDate, modifier = Modifier)
                TextSpannable(textSpannable = R.string.card_certificateId, text = certificate.certificateId, modifier = Modifier)
            }
        }

        PopUpMenu(options, Modifier.wrapContentWidth(), onActionClick, isExpandedMenu) {
            isExpandedMenu = !isExpandedMenu
        }
    }
}