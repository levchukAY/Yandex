package com.artioml.yandex;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DesktopActivity extends AppCompatActivity {

    private DesktopAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);

        if (getIntent().getBooleanExtra("theme", false)) {
            setTheme(R.style.AppTheme_Dark);
        }

        int cols = 6;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            cols = 4;
        if (getIntent().getBooleanExtra("size", false))
            cols++;
        final int finalCols = cols;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int iconHeight = metrics.widthPixels / cols - 33 * metrics.densityDpi / 160;

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager layout = new GridLayoutManager(this, cols);

        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == finalCols + 1)
                    return finalCols;
                return 1;
            }
        });

        recyclerView.setLayoutManager(layout);

        adapter = new DesktopAdapter(getApplicationContext(),
                getIntent().getIntegerArrayListExtra("icons"),
                getIntent().getIntegerArrayListExtra("positions"),
                getIntent().getIntegerArrayListExtra("popular_keys"),
                getIntent().getIntegerArrayListExtra("popular_vals"),
                getIntent().getIntegerArrayListExtra("deleted"),
                cols, iconHeight/*, (TextView) findViewById(R.id.textView)*/);
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

    @Override
    protected void onPause() {
        super.onPause();
        adapter.savePopular();
    }
}


        /*HashMap<Integer, Integer> icons = new HashMap<>();
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 14; i++)
            nums.add(i);
        for (int i = 0; i < 100; i++) {
            Collections.shuffle(nums);
            for (int j = 0; j < 14; j++)
                icons.put(i * 14 + j, nums.get(j));
        }*/

// inuuutDef
// UnderLineSpan
// SuggestionSpan

// Как устроен цвет Николаев