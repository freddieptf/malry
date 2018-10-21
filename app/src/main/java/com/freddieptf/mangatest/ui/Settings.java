package com.freddieptf.mangatest.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freddieptf.mangalibrary.data.ArchiveCacheManager;
import com.freddieptf.mangatest.App;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.utils.Utilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by fred on 3/1/15.
 */
public class Settings extends AppCompatActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_actionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getFragmentManager().beginTransaction().replace(
                R.id.preferencesContainer,
                new SettingsFragment()).commit();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String v = o.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(v);
            if (index >= 0) preference.setSummary(listPreference.getEntries()[index].toString());
        } else {
            if(preference.getKey().equals(getString(R.string.chapter_cache_size_pref_key))) {
                preference.setSummary(v + "MB");
                ((App) getApplication()).initChCache(Long.parseLong(v));
            }
            else preference.setSummary(v);
        }

        return true;
    }

    public void bindPrefToSummary(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), null));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);

    }

    public static class SettingsFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            ((Settings) getActivity()).bindPrefToSummary(
                    findPreference(getString(R.string.chapter_cache_size_pref_key)));

            findPreference(getString(R.string.clear_cache_key))
                    .setSummary(String.valueOf(ArchiveCacheManager.INSTANCE.getCacheDirSize()/(1024*1024)) + "MB used");

            findPreference(getString(R.string.clear_cache_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ArchiveCacheManager.INSTANCE.clearAll();
                    preference.setSummary("0MB Used");
                    return true;
                }
            });

            findPreference(getString(R.string.pref_about_title)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    PackageManager packageManager = getActivity().getPackageManager();
                    String packageName = getActivity().getPackageName();
                    String version;

                    try {
                        PackageInfo info = packageManager.getPackageInfo(packageName, 0);
                        version = info.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        version = "";
                    }

                    MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                            .title(getString(R.string.app_name) + " v" + version)
                            .content("Just a Manga/Comic Reader.")
                            .positiveText("ok")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    dialog.dismiss();
                                }
                            }).build();

                    materialDialog.show();

                    return true;
                }
            });


        }
    }

}
