package com.example.lattice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    int ACTION_REQUEST_ENABLE = 2;
    int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter myBluetoothAdapter;
    private ImageView bluetoothOnOffBtn, refreshDevice;
    private List<DeviceModel> deviceModelList;
    private RecyclerView recyclerView;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bluetoothOnOffBtn = findViewById(R.id.bluetooth_on_off_btn);
        refreshDevice = findViewById(R.id.refresh_device);
        recyclerView = findViewById(R.id.device_recycle_view);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceModelList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new DeviceAdapter(deviceModelList);
        recyclerView.setAdapter(adapter);

        bluetoothOnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBluetoothAdapter.isEnabled()) {
                    bluetoothOffMethod();
                } else {
                    bluetoothOnMethod();
                }
            }
        });

        refreshDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBluetoothAdapter.startDiscovery();
            }
        });

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);

    }

    private void bluetoothOnMethod() {

        if (myBluetoothAdapter == null) {
            //Device does not support Bluetooth
            Toast.makeText(this, "Bluetooth does not support on this devices", Toast.LENGTH_SHORT).show();
        } else {
            if (!myBluetoothAdapter.isEnabled()) {
                //code to enable bluetooth
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void bluetoothOffMethod() {
        if (myBluetoothAdapter.isEnabled()) {
            myBluetoothAdapter.disable();
        }
    }

    private void bondedDevice() {
        Set<BluetoothDevice> bondedDevices = myBluetoothAdapter.getBondedDevices();

        String[] strings = new String[bondedDevices.size()];
        int index = 0;
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                strings[index] = device.getName();

                //                device.getBondState();
                //                device.getAddress();

                index++;
                deviceModelList.add(new DeviceModel(device.getName(), device.getAddress()));
                Toast.makeText(this, strings[index - 1] + "...." + device.getBluetoothClass(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice discoverDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context, discoverDevice.getName(), Toast.LENGTH_SHORT).show();
                deviceModelList.add(new DeviceModel(discoverDevice.getName(), "recent"));
                //adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //Bluetooth is Enabled
                Toast.makeText(this, "Bluetooth is Enable", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // bluetooth enable is cancelled
                Toast.makeText(this, "Bluetooth Enable is Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}