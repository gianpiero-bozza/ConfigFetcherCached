package com.example.configfetchercached;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class Util {
    private static Integer mRepeatInterval = 15;
    private static Integer mFlexInterval = 5;

    private static Constraints constraints = new Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build();

    public static void scheduleConfigFetching(Context context) {
        PeriodicWorkRequest configDataPeriodicRequest =
            new PeriodicWorkRequest.Builder(
                ConfigFetcherWorker.class, mRepeatInterval, TimeUnit.MINUTES, mFlexInterval, TimeUnit.MINUTES)
                    // Constraints
                    .setConstraints(constraints)
                    // Backoff Criteria
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                    .build();

        WorkManager
            .getInstance(context)
            .enqueue(configDataPeriodicRequest);
    }

    public static void oneTimeConfigFetching(Context context) {
        WorkRequest configDataOneTimeRequest =
            new OneTimeWorkRequest.Builder(ConfigFetcherWorker.class)
                    // Constraints
                    .setConstraints(constraints)
                    // Backoff Criteria
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                    .build();

        WorkManager
            .getInstance(context)
            .enqueue(configDataOneTimeRequest);
    }

}
