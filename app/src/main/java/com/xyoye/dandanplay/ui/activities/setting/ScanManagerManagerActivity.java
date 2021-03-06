package com.xyoye.dandanplay.ui.activities.setting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.xyoye.dandanplay.R;
import com.xyoye.dandanplay.base.BaseMvpActivity;
import com.xyoye.dandanplay.mvp.impl.ScanManagerPresenterImpl;
import com.xyoye.dandanplay.mvp.presenter.ScanManagerPresenter;
import com.xyoye.dandanplay.mvp.view.ScanManagerView;
import com.xyoye.dandanplay.ui.fragment.VideoScanFragment;
import com.xyoye.dandanplay.ui.weight.dialog.FileManagerDialog;
import com.xyoye.dandanplay.utils.CommonUtils;
import com.xyoye.dandanplay.utils.TabEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xyoye on 2019/3/30.
 */

public class ScanManagerManagerActivity extends BaseMvpActivity<ScanManagerPresenter> implements ScanManagerView {

    @BindView(R.id.tab_layout)
    CommonTabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.delete_tv)
    TextView deleteTv;

    private List<VideoScanFragment> fragmentList;
    private int selectedPosition = 0;

    @Override
    public void initView() {
        setTitle("文件扫描管理");
        fragmentList = new ArrayList<>();
        VideoScanFragment scanFragment = VideoScanFragment.newInstance(true);
        VideoScanFragment blockFragment = VideoScanFragment.newInstance(false);
        fragmentList.add(scanFragment);
        fragmentList.add(blockFragment);

        ScanManagerManagerActivity.OnFragmentItemCheckListener itemCheckListener = hasChecked -> {
            if (hasChecked) {
                deleteTv.setTextColor(CommonUtils.getResColor(R.color.immutable_text_theme));
                deleteTv.setClickable(true);
            } else {
                deleteTv.setTextColor(CommonUtils.getResColor(R.color.text_gray));
                deleteTv.setClickable(false);
            }
        };

        scanFragment.setOnItemCheckListener(itemCheckListener);
        blockFragment.setOnItemCheckListener(itemCheckListener);

        initTabLayout();

        initViewPager();
    }

    @Override
    public void initListener() {

    }

    @Override
    protected int initPageLayoutID() {
        return R.layout.activity_video_scan;
    }

    @NonNull
    @Override
    protected ScanManagerPresenter initPresenter() {
        return new ScanManagerPresenterImpl(this, this);
    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        mTabEntities.add(new TabEntity("扫描目录", 0, 0));
        mTabEntities.add(new TabEntity("屏蔽目录", 0, 0));
        tabLayout.setTabData(mTabEntities);
        tabLayout.setCurrentTab(selectedPosition);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void initViewPager() {
        VideoScanFragmentAdapter fragmentAdapter = new VideoScanFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(selectedPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
                selectedPosition = position;
                fragmentList.get(position).updateFolderList();
                resetButtonStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.scan_folder_tv, R.id.scan_file_tv, R.id.delete_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_folder_tv:
                new FileManagerDialog(ScanManagerManagerActivity.this, FileManagerDialog.SELECT_FOLDER,
                        path -> presenter.listFolder(path)
                ).show();
                break;
            case R.id.scan_file_tv:
                new FileManagerDialog(ScanManagerManagerActivity.this, FileManagerDialog.SELECT_VIDEO, path -> {
                    List<String> pathList = new ArrayList<>();
                    pathList.add(path);
                    presenter.saveNewVideo(pathList);
                }).show();
                break;
            case R.id.delete_tv:
                fragmentList.get(selectedPosition).deleteChecked();
                resetButtonStatus();
                break;
        }
    }

    private void resetButtonStatus() {
        VideoScanFragment videoScanFragment = fragmentList.get(selectedPosition);
        if (videoScanFragment.hasChecked()) {
            deleteTv.setTextColor(CommonUtils.getResColor(R.color.immutable_text_theme));
            deleteTv.setClickable(true);
        } else {
            deleteTv.setTextColor(CommonUtils.getResColor(R.color.text_gray));
            deleteTv.setClickable(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_scan:
                new FileManagerDialog(ScanManagerManagerActivity.this, FileManagerDialog.SELECT_FOLDER, path ->
                        fragmentList.get(selectedPosition).addPath(path)
                ).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public interface OnFragmentItemCheckListener {
        void onChecked(boolean hasChecked);
    }

    private class VideoScanFragmentAdapter extends FragmentPagerAdapter {
        private List<VideoScanFragment> list;

        private VideoScanFragmentAdapter(FragmentManager supportFragmentManager, List<VideoScanFragment> list) {
            super(supportFragmentManager);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
