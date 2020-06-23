package com.example.configfetchercached;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Util {
    private static Integer mFlexDiff = 5;

    private static Constraints constraints = new Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build();

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

    public static UUID scheduleConfigFetching(Context context, int updateIntervalMs) {
        PeriodicWorkRequest configDataPeriodicRequest =
                new PeriodicWorkRequest.Builder(
                        ConfigFetcherWorker.class, updateIntervalMs, TimeUnit.MILLISECONDS, mFlexDiff, TimeUnit.MINUTES)
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
        return configDataPeriodicRequest.getId();
    }

}
