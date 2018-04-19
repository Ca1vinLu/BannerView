package com.meitu.lyz.bannerview.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.meitu.lyz.bannerview.R;
import com.meitu.lyz.bannerview.widget.BannerView;
import com.meitu.lyz.bannerview.widget.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LYZ 2018.04.19
 */
public class MainActivity extends AppCompatActivity {

    private BannerView mBannerView;
    private RelativeLayout mRlDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBannerView = findViewById(R.id.banner_view);
        mRlDownload = findViewById(R.id.rl_download);
        initData();
        initListener();
    }


    private void initData() {
        List<ImageView> mViews = new ArrayList<>();
        List<String> titleStr = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            AppCompatImageView imageView = new AppCompatImageView(this);
            imageView.setImageResource(R.mipmap.ic_launcher_round);
            ViewCompat.setElevation(imageView, getResources().getDimensionPixelOffset(R.dimen.iv_elevation));
            imageView.setBackgroundColor(Color.WHITE);
            mViews.add(imageView);
            titleStr.add("text" + i);
        }

        MyPagerAdapter adapter = new MyPagerAdapter(mViews);
        mBannerView.setAdapter(adapter, titleStr);
        mBannerView.setPageTransformer(false, new ZoomOutPageTransformer());
    }

    private void initListener() {
        mRlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    class MyPagerAdapter extends PagerAdapter {

        private static final String TAG = "MyPagerAdapter";
        private List<ImageView> mViews;

        public MyPagerAdapter(List<ImageView> mViews) {
            this.mViews = mViews;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public float getPageWidth(int position) {
            return 1f;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem: position" + position);
            container.addView(mViews.get(position));
            return mViews.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            Log.d(TAG, "destroyItem: position " + position);
            container.removeView(mViews.get(position));
        }
    }

}
