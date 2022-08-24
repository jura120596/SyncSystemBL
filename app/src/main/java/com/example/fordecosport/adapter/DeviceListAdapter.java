package com.example.fordecosport.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fordecosport.AppConstants;
import com.example.fordecosport.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<DeviceListItem> {
    public static final String DEF_ITEM_TYPE = "normal";
    public static final String TITLE_ITEM_TYPE = "tytle";
    public static final String DISCOVERY_ITEM_TYPE = "discovery";
    private List<DeviceListItem> mainList;
    private List<ViewHolder> viewHolderList;
    private SharedPreferences preferences;
    private boolean scanProgress;

    class ViewHolder {
        TextView tvBtName;
        CheckBox checkBox;
    }

    public DeviceListAdapter(@NonNull Context context, int resource, List<DeviceListItem> btList) {
        super(context, resource, btList);
        mainList = btList;
        viewHolderList = new ArrayList<>();
        preferences = context.getSharedPreferences(AppConstants.MY_PREF, context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        switch (mainList.get(position).getItemType()) {
            case TITLE_ITEM_TYPE:
                convertView = titleItem(convertView, parent);
                break;
            default:
                DEF_ITEM_TYPE:
                convertView = defaultItem(convertView, position, parent);
                break;
        }
        return convertView;
    }


    private void savePref(int pos) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstants.MAC_KEY, mainList.get(pos).getBluetoothDevice().getAddress());
        editor.apply();
    }


    private View defaultItem(View convertView, int position, ViewGroup parent) {
        ViewHolder viewHolder;
        boolean hasViewHolder = false;
        if (convertView != null) hasViewHolder = (convertView.getTag() instanceof ViewHolder);
        if (convertView == null || !hasViewHolder) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item, null, false);
            viewHolder.tvBtName = convertView.findViewById(R.id.tvBtName);
            viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
            viewHolderList.add(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.checkBox.setChecked(false);
        }
        if (mainList.get(position).getItemType().equals(DeviceListAdapter.DISCOVERY_ITEM_TYPE)) {
            viewHolder.checkBox.setVisibility(View.GONE);
            scanProgress = true;
        } else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            scanProgress = false;
        }
        try {
            viewHolder.tvBtName.setText(mainList.get(position).getBluetoothDevice().getName());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        viewHolder.checkBox.setOnClickListener(view -> {
            if (!scanProgress) {
                for (ViewHolder holder : viewHolderList) {
                    holder.checkBox.setChecked(false);
                }
                viewHolder.checkBox.setChecked(true);
                savePref(position);
            }
        });
        if (preferences.getString(AppConstants.MAC_KEY, "No bt selected")
                .equals(mainList.get(position).getBluetoothDevice().getAddress())) {
            viewHolder.checkBox.setChecked(true);
        }
        scanProgress = false;
        return convertView;
    }

    private View titleItem(View convertView, ViewGroup parent) {
        boolean hasViewHolder = false;
        if (convertView != null) hasViewHolder = (convertView.getTag() instanceof ViewHolder);
        if (convertView == null || hasViewHolder) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_title_tem, null, false);
        }
        return convertView;
    }
}
