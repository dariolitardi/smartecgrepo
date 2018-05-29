package com.dario.smartecg.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.dario.smartecg.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    private final static String LOG_TAG = DeviceListAdapter.class.getSimpleName();

    private LayoutInflater inflater;

    private OnDeviceClickListener onDeviceClickListener;

    private SortedList<BleDevice> bleDeviceList;

    public DeviceListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);

        bleDeviceList = new SortedList<>(BleDevice.class, new SortedListAdapterCallback<BleDevice>(this) {
            @Override
            public int compare(BleDevice n1, BleDevice n2) {
                return n1.getName().compareToIgnoreCase(n2.getName());
            }

            @Override
            public boolean areContentsTheSame(BleDevice oldItem, BleDevice newItem) {
                return oldItem.getName().equals(newItem.getName()) && oldItem.getMac().equals(newItem.getMac());
            }

            @Override
            public boolean areItemsTheSame(BleDevice item1, BleDevice item2) {
                return item1.getName().equals(item2.getName()) && item1.getMac().equals(item2.getMac());
            }
        });
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.onDeviceClickListener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(inflater.inflate(R.layout.adapter_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BleDevice bleDevice = bleDeviceList.get(position);

        boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
        String name = bleDevice.getName();
        String mac = bleDevice.getMac();
        int rssi = bleDevice.getRssi();
        holder.textName.setText(name);
        holder.textMac.setText(mac);
        holder.textRssi.setText(String.valueOf(rssi));
        if (isConnected) {
            holder.layoutIdle.setVisibility(View.GONE);
            holder.layoutConnected.setVisibility(View.VISIBLE);
        } else {
            holder.layoutIdle.setVisibility(View.VISIBLE);
            holder.layoutConnected.setVisibility(View.GONE);
        }

        holder.connectButton.setOnClickListener(view -> {
            if (onDeviceClickListener != null) {
                onDeviceClickListener.onConnect(bleDevice);
            }
        });

        holder.disconnectButton.setOnClickListener(view -> {
            if (onDeviceClickListener != null) {
                onDeviceClickListener.onDisconnect(bleDevice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bleDeviceList.size();
    }

    public void addDevice(BleDevice bleDevice) {
        bleDeviceList.add(bleDevice);
    }

    public void removeDevice(BleDevice bleDevice) {
        bleDeviceList.remove(bleDevice);
    }

    public void updateDevice(BleDevice bleDevice) {
        bleDeviceList.updateItemAt(bleDeviceList.indexOf(bleDevice), bleDevice);
    }

    public void removeAllDevices() {
        bleDeviceList.clear();
    }

    public void replaceItems(List<BleDevice> deviceList) {
        bleDeviceList.beginBatchedUpdates();
        bleDeviceList.clear();
        bleDeviceList.addAll(deviceList);
        bleDeviceList.endBatchedUpdates();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView textName;
        @BindView(R.id.text_mac)
        TextView textMac;
        @BindView(R.id.text_rssi)
        TextView textRssi;
        @BindView(R.id.layout_idle)
        LinearLayout layoutIdle;
        @BindView(R.id.layout_connected)
        LinearLayout layoutConnected;
        @BindView(R.id.disconnect_button)
        Button disconnectButton;
        @BindView(R.id.connect_button)
        Button connectButton;

        DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisconnect(BleDevice bleDevice);
    }
}
