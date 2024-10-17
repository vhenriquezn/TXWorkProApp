package com.vhenriquez.txwork.model.repository.impl

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.repository.WorkerRepository
import com.vhenriquez.txwork.worker.*
import com.vhenriquez.txwork.worker.PDFWorker
import javax.inject.Inject

class WorkerRepositoryImpl @Inject constructor(
): WorkerRepository {
    override fun generateReport(context: Context, instrument: InstrumentEntity, activity: ActivityEntity) {
        val workManager = WorkManager.getInstance(context)
        val generatePdfWorker = OneTimeWorkRequestBuilder<PDFWorker>()
            .setInputData(createInputDataForWorkRequest(instrument, activity, null, true ))
            .build()
        workManager.enqueue(generatePdfWorker)
    }

    @SuppressLint("EnqueueWork")
    override fun generateReports(context: Context, instruments: List<InstrumentEntity>, activity: ActivityEntity) {
        val workManager = WorkManager.getInstance(context)
        val lis = mutableListOf<OneTimeWorkRequest>()
        instruments.forEachIndexed { index, instrument ->
            val generatePdfWorker = OneTimeWorkRequestBuilder<PDFWorker>()
                .addTag(instrument.tag)
                .setInputData(createInputDataForWorkRequest(instrument, activity, (index + 1) * 100 / instruments.size, false))
                .build()
            lis.add(generatePdfWorker)
        }
        workManager.beginWith(lis).enqueue()
    }

    private fun createInputDataForWorkRequest(instrument: InstrumentEntity, activity: ActivityEntity, progress: Int?, open: Boolean): Data {
        val builder = Data.Builder()
        builder.putString(KEY_INSTRUMENT, instrument.getInstrumentForReport(activity.id))
        builder.putString(KEY_ACTIVITY_ID, activity.id)
        builder.putString(KEY_ACTIVITY_NAME, activity.name)
        builder.putBoolean(KEY_OPEN_PDF, open)
        progress?.let {
            builder.putInt(KEY_PROGRESS, it)
        }
        return builder.build()
    }
}