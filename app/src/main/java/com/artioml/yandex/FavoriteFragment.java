package com.artioml.yandex;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.artioml.yandex.data.DatabaseDescription.*;
import com.artioml.yandex.data.DatabaseHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private SQLiteDatabase db;
    private ArrayList<String> recentRequests;
    private int itemsToShow;
    private AutoCompleteTextView textView;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        /*RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.view_favorites);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setLayoutManager(layoutManager);
        //userAdapter = new UserAdapter(getActivity(), onUserClickListener);
        //mRecyclerView.setAdapter(userAdapter);*/

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        itemsToShow = Integer.parseInt("0" + sharedPreferences.getString("PREF_URI_COUNT", "5"));

        db = new DatabaseHelper(getActivity()).getWritableDatabase();
        loadResentRequests();

        textView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, recentRequests);
        textView.setAdapter(adapter);
        textView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                textView.showDropDown();
                return false;
            }
        });

        textView.setOnEditorActionListener(onEditorActionListener);

        setSwipeRefresh(view);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).
                registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        return view;
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

    public void updateHistory(String uri) {
        ContentValues cv = new ContentValues();
        cv.put(RecentRequests.COLUMN_REQUEST, uri);
        db.insert(RecentRequests.TABLE_NAME, null, cv);

        adapter.clear();
        loadResentRequests();
        adapter.addAll(recentRequests);
    }

    private void setSwipeRefresh(View view) {
        final SwipeRefreshLayout mSwipeRefresh =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_favorites);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(false);
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
        Cursor cursor = db.query(
                RecentRequests.TABLE_NAME,
                new String[] {RecentRequests.COLUMN_REQUEST},
                null, null, null, null,
                RecentRequests._ID + " DESC");

        recentRequests = new ArrayList<>();
        while (cursor.moveToNext() && recentRequests.size() < itemsToShow) {
            recentRequests.add(cursor.getString(cursor.getColumnIndex(RecentRequests.COLUMN_REQUEST)));
        }
        cursor.close();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {

                    switch (key) {
                        case "PREF_URI_COUNT":
                            itemsToShow = Integer.parseInt("0" + sharedPreferences.getString("PREF_URI_COUNT", "5"));
                            adapter.clear();
                            loadResentRequests();
                            adapter.addAll(recentRequests);
                            break;
                    }
                }
            };
}
