package com.stx.xbanner2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowInsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Handler handler;//每几秒后执行下一张的切换
    private int delay = 4000; // 默认轮播时间
    private int WHEEL = 100; // 转动
    private int mCurrentPosition = 0; // 轮播当前位置
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(WHEEL);
        }
    };
    private ViewPager2 mViewPager2;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager2 = findViewById(R.id.view_pager);
        List<String> dataList = Arrays.asList("#CCFF99", "#41F1E5", "#8D41F1", "#FF99CC");
        mViewPager2.setAdapter(new MyAdapter(dataList));
//        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        mViewPager2.setOffscreenPageLimit(1);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position;
            }
        });
        CompositePageTransformer pageTransformer = new CompositePageTransformer();
        mViewPager2.setPageTransformer(pageTransformer);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == WHEEL) {
                    int posttion = (mCurrentPosition + 1) % 4;
                    if (mCurrentPosition == 3) {
                        mViewPager2.setCurrentItem(posttion, false);
                    } else {
                        mViewPager2.setCurrentItem(posttion, true);
                    }
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, delay);
                }
            }
        };
        handler.postDelayed(runnable, delay);
    }
}
