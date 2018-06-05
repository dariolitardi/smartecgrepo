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
                if (n1.getName() != null && n2.getName() != null) {
                    return n1.getName().compareToIgnoreCase(n2.getName());
                } else if (n1.getMac() != null && n2.getMac() != null) {
                    return n1.getMac().compareToIgnoreCase(n2.getMac());
                } else {
                    return 0;
                }
            }

            @Override
            public boolean areContentsTheSame(BleDevice oldItem, BleDevice newItem) {
                if (oldItem.getName() != null && newItem.getName() != null) {
                    return oldItem.getName().equals(newItem.getName());
                } else if (oldItem.getMac() != null && newItem.getMac() != null) {
                    return oldItem.getMac().equals(newItem.getMac());
                } else {
                    return false;
                }
            }

            @Override
            public boolean areItemsTheSame(BleDevice item1, BleDevice item2) {
                if (item1.getName() != null && item2.getName() != null && item1.getMac() != null && item2.getMac() != null) {
                    return item1.getName().equals(item2.getName()) && item1.getMac().equals(item2.getMac());
                } else {
                    return false;
                }
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
        if (name == null || name.isEmpty()) {
            name = "Device";
        }
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

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisconnect(BleDevice bleDevice);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        private TextView textName;
        private TextView textMac;
        private TextView textRssi;
        private LinearLayout layoutIdle;
        private LinearLayout layoutConnected;
        private Button disconnectButton;
        private Button connectButton;

        DeviceViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_name);
            textMac = itemView.findViewById(R.id.text_mac);
            textRssi = itemView.findViewById(R.id.text_rssi);
            layoutIdle = itemView.findViewById(R.id.layout_idle);
            layoutConnected = itemView.findViewById(R.id.layout_connected);
            disconnectButton = itemView.findViewById(R.id.disconnect_button);
            connectButton = itemView.findViewById(R.id.connect_button);
        }
    }
}
