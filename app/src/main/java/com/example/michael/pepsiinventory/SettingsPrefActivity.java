package com.example.michael.pepsiinventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsPrefActivity.class.getSimpleName();

    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    String intentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

        intentFragment = getIntent().getStringExtra("frgToLoad");

    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            // background Switch change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_background_color)));

            // crash report Switch change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_crash_report)));

            Preference pref_send_feedback = findPreference(getString(R.string.key_send_feedback));
            pref_send_feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

//            Preference pref_privacy_policy = findPreference(getString(R.string.key_about_us));
//            pref_privacy_policy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    startActivity(new Intent(getActivity(),AboutApp.class));
//                    return true;
//                }
//            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsPrefActivity.this,MainActivity.class);
        intent.putExtra("frgToLoad",intentFragment);
        startActivity(intent);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean state = (Boolean) newValue;
            if(preference instanceof SwitchPreference) {
                if (preference.getKey().equals("key_background_color")) {
                    editor.putBoolean("key_background_choice", state);
                    editor.commit();
                    Log.d(TAG, "onPreferenceChange: "+pref.getBoolean("key_background_choice",false));
                } else if (preference.getKey().equals("key_crash_report")) {
//                    editor.putBoolean("key_crash_choice", state);
//                    editor.commit();
                }
            }
            return true;
        }
    };


    public static void sendFeedback(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@pepsiInventory.org"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}

