package com.example.lattice;

import android.bluetooth.BluetoothDevice;

public class DeviceModel {

    private BluetoothDevice Device;

    public DeviceModel(BluetoothDevice device) {
        Device = device;
    }


    public BluetoothDevice getDevice() {
        return Device;
    }

    public void setDevice(BluetoothDevice device) {
        Device = device;
    }
}
