package com.meitu.lyz.bannerview.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meitu.lyz.bannerview.R;
import com.meitu.lyz.bannerview.util.ConvertUtils;
import com.meitu.lyz.bannerview.widget.PagerIndicator;
import com.meitu.lyz.bannerview.widget.ViewPagerIndicator;
import com.meitu.lyz.widget.FixPagerTransformer;
import com.meitu.lyz.widget.OffsetZoomPageTransformer;
import com.meitu.lyz.widget.ViewPagerMultipleItemPlugin;
import com.meitu.lyz.widget.ZoomPageTransformer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author LYZ 2018.04.19
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.multiple_item_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.view_pager_indicator)
    ViewPagerIndicator mViewPagerIndicator;
    @BindView(R.id.text_page_indicator)
    PagerIndicator mPagerIndicator;
    @BindView(R.id.rl_download)
    RelativeLayout mRlDownload;
    @BindView(R.id.btn_change)
    Button mBtnChange;

    private boolean mIsDouble = false;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mViewPagerIndicator.attachViewPager(mViewPager);
        mPagerIndicator.attachViewPager(mViewPager);

        initData();
        initListener();
    }


    private void initData() {
        mAdapter = new MyAdapter();
        mViewPager.setAdapter(mAdapter);
        ViewPagerMultipleItemPlugin plugin = new ViewPagerMultipleItemPlugin(mViewPager);
        plugin.setRatio(348 / 276f);
        FixPagerTransformer zoomPageTransformer = new ZoomPageTransformer();
//        ((OffsetZoomPageTransformer) zoomPageTransformer).setItemMargin(ConvertUtils.dp2px(10, this));
//        zoomPageTransformer.setRatio(1);
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
        mViewPager.setOffscreenPageLimit(size);
        mAdapter.setViews(views);
        mPagerIndicator.setTotalSize(views.size());
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
                mAdapter.notifyDataSetItemChanged(5);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, CameraActivity.class));
                break;
        }

        return true;
    }

    private class MyAdapter extends PagerAdapter {

        List<View> mViews;
        Queue<Integer> updatePosIndex = new ArrayDeque<>();

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
                view.setTag(position);
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
            if (((View) object).getTag() == updatePosIndex.peek()) {
                updatePosIndex.poll();
                return POSITION_NONE;
            } else return POSITION_UNCHANGED;
        }

        public void setViews(List<View> views) {
            mViews = views;
            notifyDataSetChanged();
        }

        public void notifyDataSetItemChanged(int pos) {
            updatePosIndex.add(pos);
            notifyDataSetChanged();
        }
    }
}
