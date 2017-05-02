package com.artioml.yandex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private Map<String, ResolveInfo>  appsMap;
    //private List<ResolveInfo> list;
    private DesktopFragment desktopFragment;
    private FavoriteFragment favoriteFragment;
    private boolean isFavoriteHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("PREF_IS_FIRST", true))
            startActivity(new Intent(this, StartActivity.class));

        if (sharedPreferences.getString("PREF_THEME", "Light").equals("Dark"))
            setTheme(R.style.AppTheme_Dark);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        isFavoriteHidden = sharedPreferences.getBoolean("PREF_HIDE_FAVORITE", false);

        //boolean isLarge = sharedPreferences.getBoolean("PREF_IS_LARGE", false);

        desktopFragment = new DesktopFragment();
        favoriteFragment = new FavoriteFragment();

        //updateAppList();
        showViewPager();

        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(preferencesChangeListener);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getString("PREF_THEME", "Light").equals("Dark"))
            setTheme(R.style.AppTheme_Dark);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
    }*/

    void showViewPager() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(desktopFragment);
        adapter.addFragment(favoriteFragment);
        mViewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            desktopFragment.updateAppList();
            favoriteFragment.updateFavoriteApps();
        }
    }

    /*void updateAppList() {
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = getPackageManager();
        list = packageManager.queryIntentActivities(launcher, 0);
        Collections.sort(list, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo info1, ResolveInfo info2) {
                return info1.loadLabel(packageManager).toString()
                        .compareTo(info2.loadLabel(packageManager).toString());
            }
        });

        appsMap = new HashMap<>();
        for (ResolveInfo info : list) {
            appsMap.put(info.activityInfo.packageName, info);
        }
    }*/

    public void updateFavoriteApps() {
        favoriteFragment.updateFavoriteApps();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        //private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            if (isFavoriteHidden)
                return 1;
            return 2;
            //return mFragmentList.size();
        }

        /*@Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }*/

        void addFragment(Fragment fragment/*, String title*/) {
            mFragmentList.add(fragment);
            //mFragmentTitleList.add(title);
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {

                    switch (key) {
                        case "PREF_THEME":
                            MainActivity.this.recreate();
                            /*Toast.makeText(MainActivity.this, "theme", Toast.LENGTH_SHORT).show();
                            if (sharedPreferences.getString("PREF_THEME", "Light").equals("Light"))
                                MainActivity.this.setTheme(R.style.AppTheme);
                            else MainActivity.this.setTheme(R.style.AppTheme_Dark);
                            MainActivity.this.setContentView(R.layout.activity_main);*/
                            break;
                        case "PREF_HIDE_FAVORITE":
                            isFavoriteHidden = !isFavoriteHidden;
                            showViewPager();
                            break;
                    }
            }
        };

}

/* Cursor cursor = db.query(
                                Favorites.TABLE_NAME,
                                new String[] {Favorites.COLUMN_APP},
                                Favorites.COLUMN_APP + " = ? ",
                                new String[] {app}, null, null, null);
                        int size = cursor.getCount();

                        if (size == 0) {
                            ContentValues cv = new ContentValues();
                            cv.put(PopularApps.COLUMN_APP, app);
                            cv.put(PopularApps.COLUMN_COUNT, 1);
                            db.insert(PopularApps.TABLE_NAME, null, cv);
                            Log.d("QWERTYU", "NEW");
                        } else {
                            cursor.moveToNext();
                            int count = cursor.getInt(cursor.getColumnIndex(PopularApps.COLUMN_COUNT));
                            ContentValues cv = new ContentValues();
                            cv.put(PopularApps.COLUMN_COUNT, count + 1);
                            db.update(PopularApps.TABLE_NAME, cv,
                                    PopularApps.COLUMN_APP + " = ? ", new String[] {app});
                            Log.d("QWERTYU", "UPDATE");
                        }

cursor.close();*/