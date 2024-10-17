package com.vhenriquez.txwork.model.repository

import android.content.Context
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.InstrumentEntity

interface WorkerRepository {
    fun generateReport(context: Context, instrument: InstrumentEntity, activity: ActivityEntity)
    fun generateReports(context: Context, instruments: List<InstrumentEntity>, activity: ActivityEntity)
}