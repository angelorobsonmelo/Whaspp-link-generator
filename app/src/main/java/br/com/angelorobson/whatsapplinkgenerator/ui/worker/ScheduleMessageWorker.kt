package br.com.angelorobson.whatsapplinkgenerator.ui.worker

import android.content.Context
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters

class ScheduleMessageWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        println("INSIDE DO WORK")

        return success()
    }
}