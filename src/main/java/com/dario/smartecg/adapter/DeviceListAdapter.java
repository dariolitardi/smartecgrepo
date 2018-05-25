package com.dario.smartecg.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dario.smartecg.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    private final static String LOG_TAG = DeviceListAdapter.class.getSimpleName();

    private LayoutInflater inflater;

    private OnItemClickListener onItemClickListener;

    private SortedList<BluetoothDevice> devices;

    public DeviceListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setItems(SortedList<BluetoothDevice> devices) {
        this.devices = devices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(inflater.inflate(R.layout.device_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);

        holder.name.setText(device.getName());
        holder.mac.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.device_name)
        TextView name;
        @BindView(R.id.device_address)
        TextView mac;

        DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (onItemClickListener != null) {
                layout.setOnClickListener(v -> onItemClickListener.onItemClick(v, getAdapterPosition()));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
