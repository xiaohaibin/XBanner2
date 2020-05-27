package com.stx.xbanner2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author: xiaohaibin.
 * @time: 2020/4/3
 * @mail:xhb_199409@163.com
 * @github:https://github.com/xiaohaibin
 * @describe: XBanner2 基于ViewPager2优化版本
 */
public class XBanner2 extends RelativeLayout {

    private static final int RMP = LayoutParams.MATCH_PARENT;
    private static final int RWC = LayoutParams.WRAP_CONTENT;
    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;
    /**
     * 指示点位置
     */
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    @IntDef({LEFT, CENTER, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INDICATOR_GRAVITY {
    }

    private ViewPager2 mViewPager2;
    private ViewPager2.OnPageChangeCallback mOnPageChangeListener;
    private OnItemClickListener mOnItemClickListener;
    private AutoSwitchTask mAutoSwitchTask;
    private BannerPageAdapter mAdapter;
    /**
     * 非自动轮播状态下是否可以循环切换
     */
    private boolean mIsHandLoop = false;
    /**
     * 是否开启自动轮播
     */
    private boolean mIsAutoPlay = true;
    /**
     * 自动播放时间
     */
    private int mAutoPalyTime = 5000;
    /**
     * 是否允许用户滑动
     */
    private boolean mIsAllowUserScroll = true;
    /**
     * 数据资源集合
     */
    private List<?> mDatas;

    public XBanner2(Context context) {
        this(context, null);
    }

    public XBanner2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XBanner2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mAutoSwitchTask = new AutoSwitchTask(this);
        if (mViewPager2 != null && this.equals(mViewPager2.getParent())) {
            this.removeView(mViewPager2);
            mViewPager2 = null;
        }
        mViewPager2 = new ViewPager2(context);
        LayoutParams viewPagerlayoutParams = new LayoutParams(RMP, RMP);
        mAdapter = new BannerPageAdapter();
        mViewPager2.setLayoutParams(viewPagerlayoutParams);
        mViewPager2.registerOnPageChangeCallback(new OnPageChangeListener());
        mViewPager2.setUserInputEnabled(mIsAllowUserScroll);
        addView(mViewPager2, 0, viewPagerlayoutParams);
    }

    private void initViewPager() {
        RecyclerView.Adapter adapter = mViewPager2.getAdapter();
        if (adapter == null) {
            mViewPager2.setAdapter(mAdapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        /*当图片多于1张时开始轮播*/
        if (getRealCount() > 1 && mIsAutoPlay) {
            int zeroItem = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
            mViewPager2.setCurrentItem(zeroItem, false);
            startAutoPlay();
        } else {
            if (mIsHandLoop && getRealCount() != 0) {
                int zeroItem = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
                mViewPager2.setCurrentItem(zeroItem, false);
            }
        }
    }

    public BannerPageAdapter getAdapter() {
        return mAdapter;
    }

    public ViewPager2 getViewPager2() {
        return mViewPager2;
    }

    public void setOnPageChangeListener(ViewPager2.OnPageChangeCallback onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAutoPlay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoPlay();
    }

    public void startAutoPlay() {
        stopAutoPlay();
        if (mIsAutoPlay) {
            postDelayed(mAutoSwitchTask, mAutoPalyTime);
        }
    }

    public void stopAutoPlay() {
        if (mAutoSwitchTask != null) {
            removeCallbacks(mAutoSwitchTask);
        }
    }

    public void setBannerData(List<?> bannerData) {
        this.mDatas = bannerData;
        initViewPager();
    }

    private int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public void setAdapter(@Nullable RecyclerView.Adapter adapter) {
        mAdapter.registerAdapter(adapter);
    }

    private static class AutoSwitchTask implements Runnable {
        private final WeakReference<XBanner2> mXBanner;

        private AutoSwitchTask(XBanner2 mXBanner) {
            this.mXBanner = new WeakReference<>(mXBanner);
        }

        @Override
        public void run() {
            XBanner2 banner = mXBanner.get();
            if (banner != null) {
                if (banner.mViewPager2 != null) {
                    int currentItem = banner.mViewPager2.getCurrentItem() + 1;
                    banner.mViewPager2.setCurrentItem(currentItem);
                }
                banner.startAutoPlay();
            }
        }
    }

    private class BannerPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private RecyclerView.Adapter adapter;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            adapter.onBindViewHolder(holder, toRealPosition(position));
        }

        @Override
        public int getItemCount() {
            return mIsAutoPlay ? Integer.MAX_VALUE : (mIsHandLoop ? Integer.MAX_VALUE : getRealCount());
        }

        @Override
        public int getItemViewType(int position) {
            return adapter.getItemViewType(toRealPosition(position));
        }

        void registerAdapter(RecyclerView.Adapter adapter) {
            if (this.adapter != null) {
                this.adapter.unregisterAdapterDataObserver(itemDataSetChangeObserver);
            }
            this.adapter = adapter;
            if (this.adapter != null) {
                this.adapter.registerAdapterDataObserver(itemDataSetChangeObserver);
            }
        }
    }

    private int toRealPosition(int position) {
        return position % getRealCount();
    }

    private class OnPageChangeListener extends ViewPager2.OnPageChangeCallback {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

    private RecyclerView.AdapterDataObserver itemDataSetChangeObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public final void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public final void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            onChanged();
        }

        @Override
        public final void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public final void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public final void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }

        @Override
        public void onChanged() {
            if (mViewPager2 != null && mAdapter != null) {
                initViewPager();
            }
        }
    };

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(XBanner2 banner, Object model, View view, int position);
    }

}
