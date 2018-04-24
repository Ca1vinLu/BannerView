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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meitu.lyz.bannerview.R;
import com.meitu.lyz.bannerview.widget.BannerView;
import com.meitu.lyz.bannerview.widget.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LYZ 2018.04.19
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BannerView mBannerView;
    private RelativeLayout mRlDownload;
    private Button mBtnChange;
    private boolean mIsDouble = false;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBannerView = findViewById(R.id.banner_view);
        mRlDownload = findViewById(R.id.rl_download);
        mBtnChange = findViewById(R.id.btn_change);
        initData();
        initListener();
    }


    private void initData() {
        mAdapter = new MyPagerAdapter();
        mAdapter.setViewPager(mBannerView.getViewPager());
        mBannerView.setAdapter(mAdapter);
        mBannerView.setPageTransformer(false, new ZoomOutPageTransformer(mBannerView.getViewPager()));

        generateData(15);
    }

    private void generateData(int size) {
        Log.d(TAG, "generateData: " + size);
        List<ImageView> views = new ArrayList<>();
        List<String> titleStr = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            AppCompatImageView imageView = new AppCompatImageView(this);
            imageView.setImageResource(R.mipmap.ic_launcher_round);
            ViewCompat.setElevation(imageView, getResources().getDimensionPixelOffset(R.dimen.iv_elevation));
            imageView.setBackgroundColor(Color.WHITE);
            views.add(imageView);
            titleStr.add("text" + i);
        }
        mAdapter.setNewData(views);
        mBannerView.setTitleStr(titleStr);
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
                generateData(new Random().nextInt(15));
            }
        });
    }

    class MyPagerAdapter extends PagerAdapter {

        private static final String TAG = "MyPagerAdapter";
        private List<ImageView> mViews;
        private ViewPager mViewPager;

        public MyPagerAdapter(List<ImageView> mViews) {
            this.mViews = mViews;

        }

        public MyPagerAdapter() {
        }

        public MyPagerAdapter(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        public void setNewData(List<ImageView> mViews) {
            this.mViews = mViews;
            notifyDataSetChanged();
        }

        public void setViewPager(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public int getCount() {
            return mViews == null ? 0 : mViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public float getPageWidth(int position) {
            return 1f;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem: position" + position);
            if (mViews != null && mViews.size() > 0) {
                View view = mViews.get(position);
                view.setTag(position);
                container.addView(view);
                return view;
            } else return null;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            Log.d(TAG, "destroyItem: position " + position);
            container.removeView(((View) object));
        }

        @Override
        public void notifyDataSetChanged() {
            int curItem = mViewPager.getCurrentItem();
            super.notifyDataSetChanged();
            if (mViews != null && curItem < mViews.size())
                mViewPager.setCurrentItem(curItem);
            else mViewPager.setCurrentItem(0);


        }
    }

}
