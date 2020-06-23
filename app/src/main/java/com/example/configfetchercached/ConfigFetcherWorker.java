package com.example.configfetchercached;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConfigFetcherWorker extends Worker {
    private Context mContext;
    OkHttpClient client = new OkHttpClient();

    public ConfigFetcherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("ConfigFetcher", "doWork");

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.external_config_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mContext.getString(R.string.external_config_content_key),
                mContext.getResources().getString(R.string.external_config_content_empty_default));

        String content = null;
        try {
            content = getConfigJson("https://cdn.stroeerdigitalgroup.de/sdk/live/t-online/config.json");
            editor.putString(mContext.getString(R.string.external_config_content_key), content);
            editor.apply();

            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private String getConfigJson(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
