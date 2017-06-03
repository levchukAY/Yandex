package com.artioml.yandex;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.artioml.yandex.data.DatabaseDescription.RecentRequests;
import com.artioml.yandex.data.DatabaseHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FavoriteFragment extends Fragment
        //implements LoaderManager.LoaderCallbacks<Cursor>
{

    private static final int REQUESTS_LOADER = 0; // Идентификатор Loader

    private ArrayList<String> recentRequests;
    private AutoCompleteTextView textView;
    private ArrayAdapter<String> arrayAdapter;
    private FavoriteAppsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        //itemsToShow = Integer.parseInt("0" + sharedPreferences.getString("PREF_URI_COUNT", "5"));
        //db = new DatabaseHelper(getActivity()).getWritableDatabase();
        loadResentRequests();

        int cols = Integer.parseInt(sharedPreferences.getString("PREF_SIZE", "4"));
        setRecyclerView(cols, view);

        recentRequests = new ArrayList<>();
        textView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, recentRequests);
        textView.setAdapter(arrayAdapter);
        textView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                textView.showDropDown();
                return false;
            }
        });

        textView.setOnEditorActionListener(onEditorActionListener);

        setSwipeRefresh(view);

        PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadResentRequests();
        //getLoaderManager().initLoader(REQUESTS_LOADER, null, FavoriteFragment.this);
        updateFavoriteApps();
    }

    private void setRecyclerView(int cols, View view) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            cols += 2;

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.view_favorites);

        GridLayoutManager layout = new GridLayoutManager(getActivity(), cols);
        recyclerView.setLayoutManager(layout);

        adapter = new FavoriteAppsAdapter(getActivity(), cols);
        recyclerView.setAdapter(adapter);
    }

    private void setSwipeRefresh(View view) {
        final SwipeRefreshLayout mSwipeRefresh =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_favorites);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(false);
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        setCustomIndicator(mSwipeRefresh);
    }

    private void setCustomIndicator(SwipeRefreshLayout mSwipeRefresh) {
        try {
            Field f = mSwipeRefresh.getClass().getDeclaredField("mCircleView");
            f.setAccessible(true);
            ImageView img = (ImageView)f.get(mSwipeRefresh);
            img.setImageResource(R.drawable.ic_settings);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void loadResentRequests() {
        Cursor cursor = new DatabaseHelper(getActivity()).getReadableDatabase().query(
                RecentRequests.TABLE_NAME,
                new String[] {RecentRequests.COLUMN_REQUEST},
                null, null, null, null,
                RecentRequests._ID + " DESC");

        recentRequests = new ArrayList<>();
        while (cursor.moveToNext()) {
            recentRequests.add(
                    cursor.getString(cursor.getColumnIndex(RecentRequests.COLUMN_REQUEST)));
        }
        cursor.close();
    }

    public void updateHistory(String uri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecentRequests.COLUMN_REQUEST, uri);

        Log.d("TAG_FRAGMENT", "before insert");
        Uri newContactUri = getActivity().getContentResolver().insert(
                RecentRequests.CONTENT_URI, contentValues);
        Log.d("TAG_FRAGMENT", "after insert");

        //getLoaderManager().initLoader(REQUESTS_LOADER, null, this);

        if (newContactUri == null) {
            /*Snackbar.make(coordinatorLayout,
                    R.string.contact_not_added, Snackbar.LENGTH_LONG).show();*/
        }

        arrayAdapter.clear();
        loadResentRequests();
        arrayAdapter.addAll(recentRequests);
        //db.insert(RecentRequests.TABLE_NAME, null, cv);

        /*arrayAdapter.clear();
        //loadResentRequests();
        getLoaderManager().initLoader(REQUESTS_LOADER, null, this);
        arrayAdapter.addAll(recentRequests);*/
    }

    public void updateFavoriteApps() {
        adapter.loadInstalledApps();
        adapter.loadFavoriteApps();
    }

    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getView().getWindowToken(), 0);

            String currentQuery = textView.getText().toString().trim();

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentQuery)));
                updateHistory(currentQuery);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Invalid URI", Toast.LENGTH_SHORT).show();
            } finally {
                textView.setText("");
            }
            return true;
        }
    };

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {

                    switch (key) {
                        case "PREF_URI_COUNT":
                            /*itemsToShow = Integer.parseInt("0" + sharedPreferences.getString("PREF_URI_COUNT", "5"));
                            arrayAdapter.clear();
                            //getLoaderManager().initLoader(REQUESTS_LOADER, null, FavoriteFragment.this);
                            arrayAdapter.addAll(recentRequests);*/
                            break;
                    }
                }
            };

    /*// Инициализация Loader при создании активности этого фрагмента
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(REQUESTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case REQUESTS_LOADER:
                Log.d("TAG_FRAGMENT", "REQUESTS_LOADER");
                return new CursorLoader(getActivity(),
                        RecentRequests.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        RecentRequests._ID + " DESC"); // сортировка
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("TAG_FRAGMENT", "onLoadFinished " + data.getCount());
        arrayAdapter.clear();
        recentRequests = new ArrayList<>();
        while (data.moveToNext()) {
            recentRequests.add(
                    data.getString(data.getColumnIndex(RecentRequests.COLUMN_REQUEST)));
        }
        Log.d("TAG_FRAGMENT", recentRequests.toString());
        arrayAdapter.addAll(recentRequests);

        //loadResentRequests(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }*/
}
