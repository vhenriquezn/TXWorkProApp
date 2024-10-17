package com.vhenriquez.txwork.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class MyXAxisFormatter  : ValueFormatter() {

    private val porcentajeArray = arrayOf("0%", "25%", "50%", "75%", "100%", "75%", "50%", "25%", "0%")

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return porcentajeArray.getOrNull(value.toInt()) ?: value.toString()
    }
}
