package com.vhenriquez.txwork.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownContextMenu(
  options: List<String>,
  modifier: Modifier,
  onActionClick: (String) -> Unit,
  isExpanded: Boolean,


) {
  var isExpanded by remember { mutableStateOf(true) }

  ExposedDropdownMenuBox(
    expanded = isExpanded,
    modifier = modifier,
    onExpandedChange = { isExpanded = !isExpanded }
  ) {
    Icon(
      modifier = Modifier.padding(8.dp, 0.dp),
      imageVector = Icons.Default.MoreVert,
      contentDescription = "More"
    )

    ExposedDropdownMenu(
      modifier = Modifier.width(180.dp),
      expanded = isExpanded,
      onDismissRequest = { isExpanded = false }
    ) {
      options.forEach{selectionOption ->
        DropdownMenuItem(
          text = {Text(text = selectionOption)},
          onClick = {
            isExpanded = false
            onActionClick(selectionOption)
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
  @StringRes label: Int,
  options: List<String>,
  selection: String,
  modifier: Modifier,
  onNewValue: (String, Int) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
    expanded = isExpanded,
    modifier = modifier,
    onExpandedChange = { isExpanded = !isExpanded }
  ) {
    OutlinedTextField(
      value = selection,
      label = { Text(stringResource(label)) },
      onValueChange = {},
      readOnly = true,
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isExpanded) },
      modifier = Modifier
        .menuAnchor()
        .fillMaxWidth(),
      //colors = dropdownColors()
    )

    ExposedDropdownMenu(
      expanded = isExpanded,
      onDismissRequest = { isExpanded = false }) {
      options.forEachIndexed {index, selectionOption ->
        DropdownMenuItem(
          text = {
            Text(text = selectionOption)
            },
          onClick = {
            onNewValue(selectionOption, index)
            isExpanded = false
          }
        )
      }
    }
  }
}

@Composable
fun PopUpMenu(options: List<String>,
              modifier: Modifier,
              onActionClick: (Int) -> Unit,
              isExpanded: Boolean,
              onDismiss: () -> Unit){
  DropdownMenu(modifier = modifier,
                expanded = isExpanded,
                onDismissRequest = { onDismiss() }) {
                options.forEachIndexed{index,  selectionOption ->
                  DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                      onActionClick(index)
                      onDismiss()
                    })
                }
  }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dropdownColors(): TextFieldColors {
  return ExposedDropdownMenuDefaults.textFieldColors(

    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.primary
  )
}
