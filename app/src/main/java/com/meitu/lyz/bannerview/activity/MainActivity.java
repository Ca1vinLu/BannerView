package com.meitu.lyz.bannerview.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meitu.lyz.bannerview.R;
import com.meitu.lyz.bannerview.ZoomPageTransformer;
import com.meitu.lyz.bannerview.widget.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LYZ 2018.04.19
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    private ViewPagerIndicator mViewPagerIndicator;
    private RelativeLayout mRlDownload;
    private Button mBtnChange;
    private boolean mIsDouble = false;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.multiple_item_view_pager);
        mViewPagerIndicator = findViewById(R.id.view_pager_indicator);
        mRlDownload = findViewById(R.id.rl_download);
        mBtnChange = findViewById(R.id.btn_change);
        mViewPagerIndicator.attachViewPager(mViewPager);

        initData();
        initListener();
    }


    private void initData() {
        mAdapter = new MyAdapter();
        mViewPager.setAdapter(mAdapter);
        ZoomPageTransformer zoomPageTransformer = new ZoomPageTransformer();
        zoomPageTransformer.bindViewPager(mViewPager);
        mViewPager.setPageTransformer(false, zoomPageTransformer);
        generateData(15);

    }

    private void generateData(int size) {
        Log.d(TAG, "generateData: " + size);
        List<View> views = new ArrayList<>();
        List<String> titleStr = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            AppCompatImageView imageView = new AppCompatImageView(this);
            imageView.setImageResource(R.mipmap.ic_launcher_round);
            ViewCompat.setElevation(imageView, getResources().getDimensionPixelOffset(R.dimen.iv_elevation));
            imageView.setBackgroundColor(Color.WHITE);
            views.add(imageView);
            titleStr.add("text" + i);
        }
        mAdapter.setViews(views);
    }

    private void initListener() {
        mRlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) mRlDownload.getLayoutParams());
                if (!mIsDouble)
                    layoutParams.height = mRlDownload.getHeight() * 2;
                else layoutParams.height = mRlDownload.getHeight() / 2;
                mIsDouble = !mIsDouble;
                mRlDownload.setLayoutParams(layoutParams);


            }
        });

        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                generateData(new Random().nextInt(14) + 1);
                mViewPager.setCurrentItem(5);
            }
        });
    }


    private class MyAdapter extends PagerAdapter {

        List<View> mViews;

        @Override
        public int getCount() {
            return mViews == null ? 0 : mViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (mViews != null && mViews.size() > 0) {
                View view = mViews.get(position);
//            view.setTag(position);
                container.addView(view);
                return view;
            } else return null;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(((View) object));
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        public void setViews(List<View> views) {
            mViews = views;
            notifyDataSetChanged();
        }
    }
}
