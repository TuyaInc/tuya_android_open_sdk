package com.tuya.smart.android.demo.device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.activity.BaseActivity;
import com.tuya.smart.android.demo.base.adapter.ViewHolder;
import com.tuya.smart.android.shortcutparser.api.IShortcutParserManager;
import com.tuya.smart.android.shortcutparser.api.IClientParser;
import com.tuya.smart.android.shortcutparser.api.IDpStatus;
import com.tuya.smart.android.shortcutparser.impl.ShortcutParserManager;
import com.tuya.smart.sdk.TuyaSdk;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by letian on 16/7/18.
 */
public class DeviceShortcutAdapter extends BaseAdapter {
    private final List<DeviceBean> mDevs;
    private final Map<String,List<IDpStatus>> mDpStatusBeanMap;
    private final LayoutInflater mInflater;
    private Context mContext;
    private IShortcutParserManager mIShortcutParserManager;

    public DeviceShortcutAdapter(Context context) {
        mDevs = new ArrayList<>();
        mDpStatusBeanMap = new HashMap<>();
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mIShortcutParserManager = new ShortcutParserManager();

    }

    @Override
    public int getCount() {
        return mDevs.size();
    }

    @Override
    public DeviceBean getItem(int position) {
        return mDevs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_device_shortcut_item, null);
            holder = new DeviceViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (DeviceViewHolder) convertView.getTag();
        }
        holder.updateData(mDevs.get(position));
        holder.updateShortcutStatus(mDevs.get(position).getDevId(),mDpStatusBeanMap);
        return convertView;
    }

    public void setData(List<DeviceBean> myDevices) {
        mDevs.clear();
        mDpStatusBeanMap.clear();

        if (myDevices != null) {
            mDevs.addAll(myDevices);
        }

        for (DeviceBean deviceBean : myDevices){
            IClientParser clientParserBean = mIShortcutParserManager.getDeviceParseData(deviceBean);
            if(!clientParserBean.getDpShortcutStatusList().isEmpty()){
                mDpStatusBeanMap.put(deviceBean.getDevId(),clientParserBean.getDpShortcutStatusList());
            }
        }
        notifyDataSetChanged();
    }

    private static class DeviceViewHolder extends ViewHolder<DeviceBean> {
        ImageView connect;
        ImageView deviceIcon;
        TextView device;
        TextView shortcutStatus;

        DeviceViewHolder(View contentView) {
            super(contentView);
            connect = (ImageView) contentView.findViewById(R.id.iv_device_list_dot);
            deviceIcon = (ImageView) contentView.findViewById(R.id.iv_device_icon);
            device = (TextView) contentView.findViewById(R.id.tv_device);
            shortcutStatus = contentView.findViewById(R.id.tv_shortcut_status);
        }

        @Override
        public void updateData(DeviceBean deviceBean) {
            Picasso.with(TuyaSdk.getApplication()).load(deviceBean.getIconUrl()).into(deviceIcon);
            final int resId;
            if (deviceBean.getIsOnline()) {
                if (deviceBean.getIsShare() != null && deviceBean.getIsShare()) {
                    resId = R.drawable.ty_devicelist_share_green;
                } else {
                    resId = R.drawable.ty_devicelist_dot_green;
                }
            } else {
                if (deviceBean.getIsShare() != null && deviceBean.getIsShare()) {
                    resId = R.drawable.ty_devicelist_share_gray;
                } else {
                    resId = R.drawable.ty_devicelist_dot_gray;
                }
            }
            connect.setImageResource(resId);
            device.setText(deviceBean.getName());
        }

        public void updateShortcutStatus(String devId, Map<String, List<IDpStatus>> dpStatusBeanMap){
            List<IDpStatus> dpStatusBeanList = dpStatusBeanMap.get(devId);
            if(dpStatusBeanList != null){
                BaseActivity.setViewVisible(shortcutStatus);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("shortcut status:");
                for (IDpStatus statusBean : dpStatusBeanList){
                    stringBuilder.append(statusBean.getDisplayStatus());
                    stringBuilder.append("  ");
                }
                shortcutStatus.setText(stringBuilder.toString());
            }else {
                BaseActivity.setViewGone(shortcutStatus);
            }
        }
    }
}
