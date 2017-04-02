package com.artioml.yandex;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setTheme(R.style.AppTheme_Dark);

        UiModeManager uiManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        //uiManager.enableCarMode(UiModeManager.ENABLE_CAR_MODE_GO_CAR_HOME);
        //uiManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
        //setTheme(R.style.AppTheme_Dark);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // disable swiping
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= 17)
            if(getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)
                mViewPager.setRotationY(180);

        //mViewPager.setTranslationX(-1 * mViewPager.getWidth() * position);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        final Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mViewPager.getCurrentItem();
                if (position < 3)
                    mViewPager.setCurrentItem(position + 1);
                else {
                    boolean isLarge = ((RadioButton) findViewById(R.id.radioButton2)).isChecked();
                    boolean isDark = ((RadioButton) findViewById(R.id.radioButton4)).isChecked();
                    ArrayList<Integer> icons = new ArrayList<>();
                    ArrayList<Integer> positions = new ArrayList<>();
                    ArrayList<Integer> nums = new ArrayList<>();
                    for (int i = 0; i < 16; i++)
                        nums.add(i);
                    for (int i = 0; i < 10; i++) {
                        Collections.shuffle(nums);
                        for (int j = 0; j < 16; j++) {
                            icons.add(nums.get(j));
                            positions.add(i * 16 + j);
                        }
                    }
                    Intent desktopIntent = new Intent(MainActivity.this, DesktopActivity.class);
                    desktopIntent.putExtra("size", isLarge);
                    desktopIntent.putExtra("theme", isDark);
                    desktopIntent.putIntegerArrayListExtra("icons", icons);
                    desktopIntent.putIntegerArrayListExtra("positions", positions);
                    desktopIntent.putIntegerArrayListExtra("popular_keys", new ArrayList<Integer>());
                    desktopIntent.putIntegerArrayListExtra("popular_vals", new ArrayList<Integer>());
                    desktopIntent.putIntegerArrayListExtra("deleted", new ArrayList<Integer>());
                    startActivity(desktopIntent);
                    finish();
                }
            }
        });

    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sectionNumber = this.getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;
            switch (sectionNumber) {
                case 0:
                    rootView = inflater.inflate(R.layout.fragment_1, container, false);
                    break;
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_2, container, false);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_3, container, false);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_4, container, false);
                    break;
            }
            if (Build.VERSION.SDK_INT >= 17)
                if(getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)
                    rootView.setRotationY(180);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    /*public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }*/

    /*public boolean isRTL(Context ctx) {
        Configuration config = ctx.getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }*/

}