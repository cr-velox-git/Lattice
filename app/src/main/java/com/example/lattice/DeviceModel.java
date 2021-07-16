package com.example.lattice;

public class DeviceModel {

    private String DeviceName;
    private String DeviceAddress;

    public DeviceModel(String deviceName, String deviceAddress) {
        DeviceName = deviceName;
        DeviceAddress = deviceAddress;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getDeviceAddress() {
        return DeviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        DeviceAddress = deviceAddress;
    }
}
