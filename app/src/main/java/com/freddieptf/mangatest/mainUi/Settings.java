package com.freddieptf.mangatest.mainUi;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.service.DownloadMangaDatabase;
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 3/1/15.
 */
public class Settings extends AppCompatActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_actionBar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
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
            preference.setSummary(v);
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
                    findPreference(getString(R.string.pref_manga_sources_key)));


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
                            .content("Just a Simple Manga Downloader and Reader.")
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


            findPreference(getString(R.string.pref_fix_database_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title("Manga Databases")
                            .items(R.array.pref_manga_sources_Entries)
                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {

                                    Intent intent = new Intent(getActivity(), DownloadMangaDatabase.class);
                                    if(integers[0] == 4) materialDialog.setSelectedIndices(
                                            new Integer[]{0, 1, 2, 3, 4});

                                    if (integers.length == 1) {
                                        intent.putExtra(DownloadMangaDatabase.FIX_SELECTION, integers[0] + 1);
                                        startMeService(intent);
                                    } else if (integers.length > 1) {
                                        int[] ints = new int[integers.length];
                                        int inc = 0;
                                        for(Integer i : integers){
                                            ints[inc] = i + 1;
                                            inc++;
                                        }
                                        intent.putExtra(DownloadMangaDatabase.FIX_MULTIPLE_SELECTION, ints);
                                        startMeService(intent);
                                    } else {
                                        Toast.makeText(getActivity(), "Baka, you didn't choose anything!", Toast.LENGTH_SHORT).show();
                                    }


                                    return true;
                                }
                            })
                            .positiveText(getString(R.string.download))
                            .negativeText("Cancel")
                            .build();

                    dialog.show();

                    return false;
                }

                private void startMeService(Intent intent) {
                    if(Utilities.isOnline(getActivity())) getActivity().startService(intent);
                    else Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

}
