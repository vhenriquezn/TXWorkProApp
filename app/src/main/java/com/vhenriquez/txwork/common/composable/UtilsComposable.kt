package com.vhenriquez.txwork.common.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.ui.theme.Purple40
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun TextSpannable(textSpannable: Int, text: String, modifier: Modifier) {
  Text(
    text= buildAnnotatedString {
      withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(stringResource(textSpannable))
      }
      append(text)},
    style = MaterialTheme.typography.bodyMedium,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    modifier = modifier
  )
}

@Composable
fun EditTextCalibration(value: String, label: String? = null, modifier: Modifier?, onValueChange: (String) -> Unit,
                        keyboardActions: KeyboardActions = KeyboardActions.Default,
                        imeAction: ImeAction = ImeAction.Default,){
  OutlinedTextField(value = value,
    onValueChange = {onValueChange(it)},
    modifier = Modifier.then(modifier ?: Modifier),
    maxLines = 1,
    keyboardActions = keyboardActions,
    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = imeAction),
    label = { label?.let { Text(text = it) } })
}

@Composable
fun EditTextInDialog(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier? = Modifier,
  keyboardType: KeyboardType? = null,){
  OutlinedTextField(
    value = value,
    modifier = Modifier.fillMaxWidth().then(modifier ?: Modifier),
    maxLines = 1,
    singleLine = true,
    onValueChange = onValueChange,
    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType ?: KeyboardType.Text),
    label = {
      Text(text = label) }
  )
}

@Composable
fun SearchTextField(
  searchText: String,
  onSearchTextChange: (String) -> Unit
){
  OutlinedTextField(
    modifier = Modifier.fillMaxWidth().padding(5.dp),
    value = searchText,
    onValueChange = onSearchTextChange,
    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "searchIcon") },
    trailingIcon = @Composable {
      when{
        searchText.isNotEmpty()-> IconButton(onClick = { onSearchTextChange("")}){
          Icon(imageVector = Icons.Default.Clear, contentDescription = "clearIcon")
        } }},
    placeholder = { Text(text = "Buscar") },
    shape = RoundedCornerShape(8.dp)
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
  onChangeShowDialogPicker: (Boolean) -> Unit,
  onDateChange: (String) -> Unit
){
  val today = LocalDate.now()
  val dateState = rememberDatePickerState(
    initialSelectedDateMillis = today.toEpochDay() * 24 * 60 * 60 * 1000)
  DatePickerDialog(
    onDismissRequest = {
      onChangeShowDialogPicker(false) // false
    },
    confirmButton = {
      Button(
        onClick = {
          val mDate = dateState.selectedDateMillis
          val localDate = Instant.ofEpochMilli(mDate!!).atZone(ZoneId.of("UTC")).toLocalDate()
          onDateChange("${localDate.dayOfMonth}/${localDate.monthValue}/${localDate.year}")
          onChangeShowDialogPicker(false) //false
        }
      ) {
        Text(text = "OK")
      }
    },
    dismissButton = {
      Button(
        onClick = {
          onChangeShowDialogPicker(false) // false
        }
      ) {
        Text(text = "Cancel")
      }
    }
  ) {
    DatePicker(
      state = dateState
    )
  }
}

@Composable
fun DeleteDialog(
  title: String,
  message: String,
  onConfirmDelete: () -> Unit,
  onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = { Text(message) },
    confirmButton = {
      Button(
        onClick = onConfirmDelete
      ) {
        Text("Aceptar")
      }
    },
    dismissButton = {
      Button(
        onClick = onDismiss
      ) {
        Text("Cancelar")
      }
    }
  )
}

@Composable
fun AuthLogo(text: String){
  Image(
    painter = painterResource(id = R.drawable.ic_tx_work),
    contentDescription = "Logo",
    modifier = Modifier
      .size(150.dp)
  )
  Spacer(modifier = Modifier.height(20.dp))
  Text(
    text = text,
    textAlign = TextAlign.Center,
    style = TextStyle(fontSize = 40.sp, color = Purple40)
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PopupBox(
  title: String,
  isFullScreen: Boolean = false,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  onFabClick: () -> Unit = {},
  fabVisibility: Boolean = false,
  content:@Composable () -> Unit) {
  if (isFullScreen){
    Dialog(
      onDismissRequest = onDismiss,
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Scaffold(modifier = Modifier.padding(1.dp),
      //modifier = (if (isFullScreen) Modifier else Modifier.requiredHeightIn(max = 500.dp)),
      topBar = {
        if (isFullScreen){
          TopAppBar(
            title = { Text(title) },
            navigationIcon = {
              IconButton(onClick = {
                onDismiss()
              }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close"
                )
              }
            },
            actions = {
              IconButton(onClick = {
                onConfirm()
              }) {
                Icon(
                  imageVector = Icons.Default.Done,
                  contentDescription = "Localized description"
                )
              }
            }
          )
        }else{
          Text(
            text = title,
            style = TextStyle(fontSize = 22.sp),
            modifier = Modifier.padding(top = 10.dp, start = 20.dp))
        }
      },
      floatingActionButton = {
        if (fabVisibility){
          FloatingActionButton(
            onClick = onFabClick) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
          }
        }
      },
      content = {paddingValues ->
          Box(Modifier.padding(paddingValues)) {
            content()
          }


      }
    )}
  }else{
    AlertDialog(onDismissRequest = onDismiss,
      title = { Text(title) },
      confirmButton = {
        TextButton(
          onClick = onConfirm,
          modifier = Modifier.padding(8.dp),
        ) {
          Text("Aceptar")
        }
      },
      dismissButton = {
        TextButton(
          onClick = onDismiss,
          modifier = Modifier.padding(8.dp),
        ) {
          Text("Cancelar")
        }
      },
      text = {content()})
  }
}

@Composable
fun AddItemDialog(title: String, value: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {

  var content by remember { mutableStateOf(value) }
  AlertDialog(
    onDismissRequest = {},
    title = { Text(text = title) },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(content)
          content = ""
          onDismiss()
        }
      ) {
        Text(text = "Agregar")
      }
    },
    dismissButton = {
      Button(onClick = onDismiss) {
        Text(text = "Cancelar")
      }
    },
    text = {
      Column {
        TextField(
          modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
          value = content,
          onValueChange = { content = it },
          keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
          maxLines = 4,
          label = { Text(text = "Contenido") }
        )

      }
    }
  )
}