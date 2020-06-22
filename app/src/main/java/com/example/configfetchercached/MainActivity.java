package com.example.configfetchercached;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView mExternalConfigContent;

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
            if (key.equals(getString(R.string.external_config_content_key))) {
                String defaultValue = getResources().getString(R.string.external_config_content_empty_default);
                String configContent = sharedPrefs.getString(getString(R.string.external_config_content_key), defaultValue);
                mExternalConfigContent.setText(configContent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mExternalConfigContent = findViewById(R.id.externalConfigContent);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.external_config_file_key), Context.MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        String defaultEmptyConfigContent = getResources().getString(R.string.external_config_content_empty_default);
        String configContent = sharedPref.getString(getString(R.string.external_config_content_key), defaultEmptyConfigContent);

        mExternalConfigContent.setText(configContent);

        if (configContent.equals(defaultEmptyConfigContent)) {
            Util.oneTimeConfigFetching(this);
        }

        Util.scheduleConfigFetching(this);
    }
}