package com.artioml.yandex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class DesktopFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_desktop, container, false);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        int cols = Integer.parseInt(sharedPreferences.getString("PREF_SIZE", "4"));
        setRecyclerView(cols);

        setSwipeRefresh();

        PreferenceManager.getDefaultSharedPreferences(getActivity()).
                registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        return view;
    }

    private void setRecyclerView(int cols) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            cols += 2;
        final int finalCols = cols;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int iconHeight = (metrics.widthPixels - 8 * metrics.densityDpi / 160) / cols
                - 16 * metrics.densityDpi / 160;

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layout = new GridLayoutManager(getActivity(), cols);

        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == finalCols + 1)
                    return finalCols;
                return 1;
            }
        });

        recyclerView.setLayoutManager(layout);

        DesktopAdapter adapter = new DesktopAdapter(getActivity(), cols, iconHeight);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Paint paint = new Paint();

            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    int position = parent.getChildAdapterPosition(child);
                    if (position == finalCols + 2)
                        drawBackground(canvas, parent, i);

                }
            }

            private void drawBackground(Canvas canvas, RecyclerView parent, int index) {
                int l = parent.getLeft();
                int r = parent.getRight();
                int t = parent.getChildAt(index).getBottom();
                int b = parent.getChildAt(index).getBottom() + 5;
                //(int) getResources().getDimensionPixelOffset(R.dimen.fourth_margin);
                drawBackground(canvas, l, t, r, b);
            }

            private void drawBackground(Canvas c, int l, int t, int r, int b) {
                //paint.setShader(new LinearGradient(l, t, r, b, Color.RED, Color.BLUE, Shader.TileMode.CLAMP));
                paint.setColor(getResources().getColor(R.color.colorAccent));
                c.drawRect(l, t, r, b, paint);
            }

        });
    }

    private void setSwipeRefresh() {
        final SwipeRefreshLayout mSwipeRefresh =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_desktop);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(false);
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        setCustomInicator(mSwipeRefresh);
    }

    private void setCustomInicator(SwipeRefreshLayout mSwipeRefresh) {
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

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {

                    switch (key) {
                        case "PREF_SIZE":
                            setRecyclerView(Integer.parseInt(sharedPreferences.getString(key, "5")));
                            break;
                    }
                }
            };

}
