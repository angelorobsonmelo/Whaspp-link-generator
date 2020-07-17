package br.com.angelorobson.whatsapplinkgenerator.ui.worker

import android.content.Context
import androidx.work.ListenableWorker.Result.success
import androidx.work.RxWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.Single

class ScheduleMessageWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return success()
    }
}