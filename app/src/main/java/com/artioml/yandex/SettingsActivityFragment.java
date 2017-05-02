package com.artioml.yandex;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.artioml.yandex.data.DatabaseDescription;
import com.artioml.yandex.data.DatabaseHelper;

public class SettingsActivityFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);

        findPreference("PREF_CLEAR_FAVORITE_LIST")
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        SQLiteDatabase db = new DatabaseHelper(getActivity()).getWritableDatabase();
                        db.delete(DatabaseDescription.Favorites.TABLE_NAME, null, null);
                        return true;
            }
        });

        findPreference("PREF_CLEAR_URI_LIST")
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        SQLiteDatabase db = new DatabaseHelper(getActivity()).getWritableDatabase();
                        db.delete(DatabaseDescription.RecentRequests.TABLE_NAME, null, null);
                        return true;
                    }
                });

    }
}