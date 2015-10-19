package vn.edu.hcmut.cse.smartads.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.service.RemoteSettingService;
import vn.edu.hcmut.cse.smartads.settings.SettingServiceRequestType;

public class SettingsActivity extends AppCompatActivity {
    public static final String QUEUE_SYNC = "QUEUE_SYNC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private boolean mChange = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            if (mChange) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(QUEUE_SYNC, true).apply();

                Intent intent = new Intent(getActivity(), RemoteSettingService.class);
                intent.putExtra(RemoteSettingService.SERVICE_REQUEST_TYPE, SettingServiceRequestType.UPDATE_TO_SERVER);
                getActivity().startService(intent);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!mChange) {
                mChange = true;
            }
        }
    }
}
