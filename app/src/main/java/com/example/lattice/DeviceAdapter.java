package com.example.lattice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.Viewholder> {

    List<DeviceModel> deviceModelList;

    public DeviceAdapter(List<DeviceModel> deviceModelList) {
        this.deviceModelList = deviceModelList;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_info,parent,false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {
        String name = deviceModelList.get(position).getDeviceName();
        String address = deviceModelList.get(position).getDeviceAddress();

        holder.setData(name, address);
    }

    @Override
    public int getItemCount() {
        return deviceModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        private TextView deviceName, deviceAddress;

        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            deviceAddress= itemView.findViewById(R.id.device_address);
            deviceName = itemView.findViewById(R.id.device_name);
        }

        public void setData(String name, String address) {

            deviceName.setText(name);
            deviceAddress.setText(address);

        }
    }
}
