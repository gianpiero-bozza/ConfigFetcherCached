package com.example.configfetchercached;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView mExternalConfigContent;
    int mUpdateIntervalMs = 0;
    UUID mPeriodicRequestUUID;
    SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExternalConfigContent = findViewById(R.id.externalConfigContent);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.external_config_file_key), Context.MODE_PRIVATE);
        mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
                if (key.equals(getString(R.string.external_config_content_key))) {
                    String defaultValue = getResources().getString(R.string.external_config_content_empty_default);
                    String configContent = sharedPrefs.getString(getString(R.string.external_config_content_key), defaultValue);
                    mExternalConfigContent.setText(configContent);

                    JsonObject parsedJson = JsonParser.parseString(configContent).getAsJsonObject();
                    JsonObject common = parsedJson.getAsJsonObject("common");
                    int updateIntervalMs = common.get("updateIntervalMs").getAsInt();

                    if (updateIntervalMs != mUpdateIntervalMs && mPeriodicRequestUUID != null) {
                        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
                        workManager.cancelWorkById(mPeriodicRequestUUID);
                        mUpdateIntervalMs = updateIntervalMs;
                        mPeriodicRequestUUID = Util.scheduleConfigFetching(getApplicationContext(), mUpdateIntervalMs);
                    }
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

        String defaultEmptyConfigContent = getResources().getString(R.string.external_config_content_empty_default);
        String configContent = sharedPref.getString(getString(R.string.external_config_content_key), defaultEmptyConfigContent);

        mExternalConfigContent.setText(configContent);

        if (configContent.equals(defaultEmptyConfigContent)) {
            Util.oneTimeConfigFetching(this);
        }

        mPeriodicRequestUUID = Util.scheduleConfigFetching(this, mUpdateIntervalMs);
    }
}