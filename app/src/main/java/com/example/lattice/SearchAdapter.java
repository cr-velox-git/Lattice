package com.example.lattice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Viewholder> {

    List<DeviceModel> deviceModelList;
    BluetoothAdapter bluetoothAdapter;

    public SearchAdapter(List<DeviceModel> deviceModelList, BluetoothAdapter bluetoothAdapter) {
        this.deviceModelList = deviceModelList;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_info, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {
        BluetoothDevice bluetoothDevice = deviceModelList.get(position).getDevice();

        holder.setData(bluetoothDevice, position);
    }

    @Override
    public int getItemCount() {
        return deviceModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView deviceName, deviceAddress;

        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            deviceAddress = itemView.findViewById(R.id.device_address);
            deviceName = itemView.findViewById(R.id.device_name);
        }

        public void setData(BluetoothDevice bluetoothDevice, int position) {

            deviceName.setText(bluetoothDevice.getName());
            deviceAddress.setText(bluetoothDevice.getAddress());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "clicked on", Toast.LENGTH_SHORT).show();
                    bluetoothAdapter.cancelDiscovery();
                    Toast.makeText(itemView.getContext(), deviceName + "__" + deviceAddress, Toast.LENGTH_SHORT).show();
                    //create bond
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bluetoothDevice.createBond();

                        if (!MainActivity.bondedDeviceModelList.contains(bluetoothDevice)) {
                            MainActivity.bondedDeviceModelList.add(new DeviceModel(bluetoothDevice));
                            //Toast.makeText(this, strings[index - 1] + "...." + device.getBluetoothClass(), Toast.LENGTH_SHORT).show();
                            MainActivity.bondedAdapter.notifyDataSetChanged();

                        }

                    }
                }
            });
        }
    }


}
