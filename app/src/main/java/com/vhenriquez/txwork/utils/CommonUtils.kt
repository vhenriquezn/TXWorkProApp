package com.vhenriquez.txwork.utils

import androidx.compose.ui.graphics.Color
import com.vhenriquez.txwork.model.CertificateEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object CommonUtils {
    fun getErrorRelativo(value_0: Float, value_1: Float, span: Float): Float {
        val error = abs((value_0 - value_1) / span * 100)
        return if (java.lang.Float.isNaN(error)) 0f else error
    }

    fun getColor(mDate : String): Color {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date : Date? = try {
            dateFormat.parse(mDate)
        }catch (e: Exception){
            null
        }
        val currentDate = Date()
        val diff = currentDate.time - (date?.time ?: 0)
        val days = diff / (24 * 60 * 60 * 1000)
        if (days > 365 || days <= 0)
            return Color.Red
        else if (days > 300)
            return Color.Yellow
        return Color.Green
    }
}