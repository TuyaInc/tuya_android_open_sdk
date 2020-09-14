package com.tuya.smart.android.demo.device;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.activity.BaseActivity;
import com.tuya.smart.android.demo.base.presenter.SwitchPresenter;
import com.tuya.smart.android.demo.base.utils.CommonUtil;
import com.tuya.smart.android.demo.base.utils.DialogUtil;
import com.tuya.smart.android.demo.base.utils.ToastUtil;
import com.tuya.smart.android.demo.base.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by letian on 16/7/21.
 */
public class SwitchActivity extends BaseActivity implements ISwitchView {

    public static final String INTENT_DEVID = "intent_devid";
    @BindView(R.id.iv_switch)
    public ImageView mSwitchButton;

    @BindView(R.id.rl_switch_bg)
    public View mBgView;

    @BindView(R.id.v_title_down_line)
    public View mLine;

    private SwitchPresenter mPresenter;
    @BindView(R.id.v_off_line)
    public View mOffLineView;
    @BindView(R.id.network_tip)
    public TextView mOffLineTip;
    private Unbinder mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        mBind = ButterKnife.bind(this);
        initToolbar();
        initPresenter();
        initTitle();
        initView();
        initMenu();
    }

    private void initView() {
        showCloseView();
        setViewGone(mLine);
        mOffLineView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    private void initTitle() {
        setTitle(mPresenter.getTitle());
        mToolBar.setTitleTextColor(Color.WHITE);
    }

    private void initPresenter() {
        mPresenter = new SwitchPresenter(this, this);
    }

    private void initMenu() {
        setDisplayHomeAsUpEnabled();
        setMenu(R.menu.toolbar_top_smart_device, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_rename:
                        mPresenter.renameDevice();
                        break;
                    case R.id.action_close:
                        finish();
                        break;
                    case R.id.action_check_update:
                        break;
                    case R.id.action_resume_factory_reset:
                        mPresenter.resetFactory();
                        break;
                    case R.id.action_unconnect:
                        mPresenter.removeDevice();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void showOpenView() {
        mSwitchButton.setImageResource(R.drawable.switch_on);
        mBgView.setBackgroundColor(ViewUtils.getColor(this, R.color.switch_on_color));
        mToolBar.setBackgroundColor(ViewUtils.getColor(this, R.color.switch_on_color));
        CommonUtil.initSystemBarColor(this, ViewUtils.getColor(this, R.color.switch_on_color));
    }

    @Override
    public void showCloseView() {
        mSwitchButton.setImageResource(R.drawable.switch_off);
        mBgView.setBackgroundColor(ViewUtils.getColor(this, R.color.switch_off_color));
        mToolBar.setBackgroundColor(ViewUtils.getColor(this, R.color.switch_off_color));
        CommonUtil.initSystemBarColor(this, ViewUtils.getColor(this, R.color.switch_off_color));
    }

    @Override
    public void showErrorTip() {
        ToastUtil.showToast(this, R.string.operation_failure);
    }

    @Override
    public void showRemoveTip() {
        DialogUtil.simpleSmartDialog(this, R.string.device_has_unbinded, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    @Override
    public void changeNetworkErrorTip(boolean status) {
        if (mOffLineView == null) return;
        if (status) {
            setViewGone(mOffLineView);
        } else {
            setViewVisible(mOffLineView);
            mOffLineTip.setText(getString(R.string.device_network_error));
        }
    }

    @Override
    public void statusChangedTip(boolean online) {
        if (online) {
            setViewGone(mOffLineView);
        } else {
            setViewVisible(mOffLineView);
            mOffLineTip.setText(getString(R.string.device_offLine));
        }
    }

    @Override
    public void devInfoUpdateView() {
        initTitle();
    }

    @Override
    public void updateTitle(String titleName) {
        setTitle(titleName);
    }

    @OnClick(R.id.iv_switch)
    public void onClickSwitch() {
        mPresenter.onClickSwitch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBind.unbind();
        mPresenter.onDestroy();
    }
}
