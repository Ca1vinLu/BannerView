package com.meitu.lyz.bannerview.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.meitu.lyz.bannerview.R;
import com.meitu.lyz.bannerview.util.ConvertUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.civ_btn)
    CircleImageView mCivBtn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);


//        mCivBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.layout((int) (v.getLeft() + event.getX()), (int) (v.getTop() + event.getY()), (int) (v.getRight() + v.getWidth() + event.getX()), (int) (v.getBottom() + v.getHeight() + event.getY()));
//                return true;
//            }
//        });
    }

    @OnClick(R.id.civ_btn)
    public void onClick() {
        mCivBtn.animate().translationXBy(ConvertUtils.dp2px(20, this));
        mCivBtn.animate().translationYBy(ConvertUtils.dp2px(20, this));
    }
}
