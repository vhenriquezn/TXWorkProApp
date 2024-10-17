package com.vhenriquez.txwork.worker

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.InstrumentEntity.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI

class PDFWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        makeStatusNotification(
            "Generando Certificados $tags",
            applicationContext, inputData.getInt(KEY_PROGRESS, 0)
        )

        return withContext(Dispatchers.IO) {
            return@withContext try {
                val gson = Gson()
                val instrument = gson.fromJson(inputData.getString(KEY_INSTRUMENT), InstrumentEntity::class.java)

                ContextCompat.getMainExecutor(applicationContext).execute {
                    val templatePdf = TemplatePDFCertificate()
                    val file = templatePdf.generatePdf(applicationContext, instrument,
                        inputData.getString(KEY_ACTIVITY_ID)!!, inputData.getString(KEY_ACTIVITY_NAME)!!
                    )

                    if(inputData.getBoolean(KEY_OPEN_PDF, false))
                        templatePdf.openPdfFile(applicationContext, file)
                }



//                val fileUri = inputData.getString(COMPRESSED_IMAGE_URI)
//                val filed = File(URI.create(fileUri))
//
//                // Read the compressed image from the file
//                val byteArray = file.readBytes()
//
//                // Write the byte array to a file on the device
//                val externalFile =
//                    File(applicationContext.getExternalFilesDir(null), "compressed_image.jpeg")
//                Log.d(TAG,externalFile.path)
//                externalFile.writeBytes(byteArray)
                Result.success()
            } catch (exception: Exception) {
                Log.e(TAG,exception.message.toString())
                Result.failure()
            }
        }

    }
}