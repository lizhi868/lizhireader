package com.lizhi.reader.base;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.reader.R;

import java.util.List;

import butterknife.BindView;

/**
 * Created by newbiechen on 17-4-24.
 */

public abstract class BaseTabActivity<T extends IPresenter> extends MBaseActivity<T> {
    /**************View***************/
    @BindView(R.id.tab_tl_indicator)
    protected TabLayout mTlIndicator;
    @BindView(R.id.tab_vp)
    protected ViewPager mVp;
    /**************Adapter***************/
    protected TabFragmentPageAdapter tabFragmentPageAdapter;
    /************Params*******************/
    protected List<Fragment> mFragmentList;
    private List<String> mTitleList;

    /**************abstract***********/
    protected abstract List<Fragment> createTabFragments();

    protected abstract List<String> createTabTitles();

    @Override
    protected void bindView() {
        super.bindView();
        setUpTabLayout();
    }

    /*****************rewrite method***************************/


    private void setUpTabLayout() {
        mFragmentList = createTabFragments();
        mTitleList = createTabTitles();

        checkParamsIsRight();

        tabFragmentPageAdapter = new TabFragmentPageAdapter(getSupportFragmentManager());
        mVp.setAdapter(tabFragmentPageAdapter);
        mVp.setOffscreenPageLimit(3);
        mTlIndicator.setupWithViewPager(mVp);
    }

    /**
     * 检查输入的参数是否正确。即Fragment和title是成对的。
     */
    private void checkParamsIsRight() {
        if (mFragmentList == null || mTitleList == null) {
            throw new IllegalArgumentException("fragmentList or titleList doesn't have null");
        }

        if (mFragmentList.size() != mTitleList.size())
            throw new IllegalArgumentException("fragment and title size must equal");
    }

    /******************inner class*****************/
    public class TabFragmentPageAdapter extends FragmentPagerAdapter {

        TabFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

    }

    public void modifyTabIndicatorWidth(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //设置最小宽度为0，防止指示线没有padding没有为0
                tabView.setMinimumWidth(0);
                // 将padding设置为0，将指示符封装为标题
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // 在制表符之间设置自定义边距
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // 左边
                        settingMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        //右边
                        settingMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // 内部
                        settingMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }
            tabLayout.requestLayout();
        }
    }

    private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }
}
