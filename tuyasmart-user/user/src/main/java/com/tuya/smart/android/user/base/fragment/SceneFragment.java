package com.tuya.smart.android.demo.base.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tuya.smart.android.common.utils.NetworkUtil;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.presenter.SceneListPresenter;
import com.tuya.smart.android.demo.scene.adapter.SmartAdapter;
import com.tuya.smart.android.demo.scene.view.ISceneListFragmentView;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.List;

import static com.tuya.smart.android.demo.base.presenter.SceneListPresenter.SMART_TYPE_AUTOMATION;
import static com.tuya.smart.android.demo.base.presenter.SceneListPresenter.SMART_TYPE_SCENE;


/**
 * create by nielev on 2019-10-28
 */
public class SceneFragment extends BaseFragment implements ISceneListFragmentView, View.OnClickListener {

    private volatile static SceneFragment mSceneFragment;
    private View mContentView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SceneListPresenter mPresenter;
    private View mEmptyView;
    private SmartAdapter mSceneAdapter;
    private SmartAdapter mAutoAdapter;
    private View mTv_scene;
    private View mTv_automation;
    private RecyclerView mRcv_scene_list;
    private RecyclerView mRcv_auto_list;

    public static Fragment newInstance() {
        if (mSceneFragment == null) {
            synchronized (SceneFragment.class) {
                if (mSceneFragment == null) {
                    mSceneFragment = new SceneFragment();
                }
            }
        }
        return mSceneFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_scene_list, container, false);
        initToolbar(mContentView);
        initMenu();
        initView();
        initAdapter();
        initSwipeRefreshLayout();
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPresenter();
        mPresenter.getSceneList();
    }

    private void initPresenter() {
        mPresenter = new SceneListPresenter(getActivity(), this);
    }

    private void initMenu() {
        setTitle(getString(R.string.home_scene));
        setMenu(R.menu.toolbar_add_smart, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_add_scene:
                        mPresenter.addScene();
                        break;
                    case R.id.action_add_auto:
                        mPresenter.addAuto();
                        break;
                        default:break;
                }
                return false;
            }
        });
    }


    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipe_container);
        mEmptyView = mContentView.findViewById(R.id.list_background_tip);
        mContentView.findViewById(R.id.tv_add_scene).setOnClickListener(this);
        mContentView.findViewById(R.id.tv_add_auto).setOnClickListener(this);
        mTv_scene = mContentView.findViewById(R.id.tv_scene);
        mTv_automation = mContentView.findViewById(R.id.tv_automation);
        mRcv_scene_list = mContentView.findViewById(R.id.rcv_scene_list);
        mRcv_auto_list = mContentView.findViewById(R.id.rcv_auto_list);
    }

    private void initAdapter() {
        mSceneAdapter = new SmartAdapter(getActivity(), SMART_TYPE_SCENE);
        mAutoAdapter = new SmartAdapter(getActivity(), SMART_TYPE_AUTOMATION);
        mRcv_scene_list.setAdapter(mSceneAdapter);
        mRcv_auto_list.setAdapter(mAutoAdapter);
        mRcv_scene_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRcv_auto_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSceneAdapter.setOnExecuteListener(new SmartAdapter.OnExecuteListener() {
            @Override
            public void onExecute(SceneBean bean) {
                mPresenter.execute(bean);
            }

        });
        mAutoAdapter.setOnSwitchListener(new SmartAdapter.OnSwitchListener() {


            @Override
            public void onSwitchAutomation(SceneBean bean) {
                mPresenter.switchAutomation(bean);
            }
        });
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    mPresenter.getSceneList();
                } else {
                    loadFinish();
                }
            }
        });
    }


    @Override
    public void showSceneListView(List<SceneBean> scenes, List<SceneBean> autos) {
        mEmptyView.setVisibility(View.GONE);
        if(scenes.isEmpty()){
            mTv_scene.setVisibility(View.GONE);
            mTv_automation.setVisibility(View.VISIBLE);
        } else if(autos.isEmpty()){
            mTv_scene.setVisibility(View.VISIBLE);
            mTv_automation.setVisibility(View.GONE);
        } else {
            mTv_scene.setVisibility(View.VISIBLE);
            mTv_automation.setVisibility(View.VISIBLE);
        }
        mSceneAdapter.setData(scenes);
        mAutoAdapter.setData(autos);

    }

    @Override
    public void loadFinish() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mTv_scene.setVisibility(View.GONE);
        mTv_automation.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_add_scene:
                mPresenter.addScene();
                break;
            case R.id.tv_add_auto:
                mPresenter.addAuto();
                break;
                default:break;
        }
    }
}
