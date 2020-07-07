package ru.arvata.pomor.util

import android.content.Context
import androidx.work.*
import ru.arvata.pomor.data.RemoteRepository
import java.util.concurrent.TimeUnit

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        loge("start scheduled sync work")
        RemoteRepository.syncWithServer()

//        loge("work success")
        return Result.success()
    }

    companion object {

        fun initSyncWork() {
            val constraints = Constraints.Builder()
                .setRequiresCharging(false)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance().enqueueUniquePeriodicWork("sync",
                ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}