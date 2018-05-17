package com.meitu.lyz.bannerview.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.meitu.lyz.bannerview.R;
import com.meitu.lyz.bannerview.util.ConvertUtils;
import com.meitu.lyz.bannerview.widget.ZoomPageTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.iv_ring)
    ImageView mIvRing;


    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
        initData();
    }


    private void initView() {
        ButterKnife.bind(this);
        mAdapter = new MyAdapter();
        mViewPager.setAdapter(mAdapter);

        ZoomPageTransformer zoomPageTransformer = new ZoomPageTransformer();
        zoomPageTransformer.setRatio(1);
        zoomPageTransformer.setItemMargin(ConvertUtils.dp2px(0, this));
        zoomPageTransformer.bindViewPager(mViewPager);
        zoomPageTransformer.bindCircleView(mIvRing);
        mViewPager.setPageTransformer(false, zoomPageTransformer);

    }


    private void initData() {
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                generateData(10);

            }
        });
    }

    private void generateData(int size) {
        Log.d(TAG, "generateData: " + size);
        List<View> views = new ArrayList<>();
        List<String> titleStr = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            CircleImageView imageView = new CircleImageView(this);
            imageView.setImageResource(R.color.module_download_btn_bg);
            views.add(imageView);
            titleStr.add("text" + i);
        }
        mViewPager.setOffscreenPageLimit(size);
        mAdapter.setViews(views);
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
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
            if (mViews != null && mViews.size() > 0) {
                View view = mViews.get(position);
//            view.setTag(position);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(CameraActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                        ((ViewPager) v.getParent()).setCurrentItem(position, true);
                    }
                });
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
