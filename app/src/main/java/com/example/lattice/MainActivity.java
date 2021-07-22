package com.example.lattice;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ImageView bluetoothOnOffBtn, searchDeviceBtn, enableDiscoverable;
    public static List<DeviceModel> bondedDeviceModelList, searchDeviceModelList;
    private RecyclerView recyclerViewBonded, searchRecycleView;
    public static DeviceAdapter bondedAdapter;
    private SearchAdapter searchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bluetoothOnOffBtn = findViewById(R.id.bluetooth_on_off_btn);
        searchDeviceBtn = findViewById(R.id.search_device);
        recyclerViewBonded = findViewById(R.id.device_recycle_view);
        searchRecycleView = findViewById(R.id.search_recycle);
        enableDiscoverable = findViewById(R.id.discoverDevice);
//bonded device
        bondedDeviceModelList = new ArrayList<>();

        LinearLayoutManager bondManager = new LinearLayoutManager(this);
        bondManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewBonded.setLayoutManager(bondManager);

        bondedAdapter = new DeviceAdapter(bondedDeviceModelList);
        recyclerViewBonded.setAdapter(bondedAdapter);

//search device
        searchDeviceModelList = new ArrayList<>();

        LinearLayoutManager searchManager = new LinearLayoutManager(this);
        searchManager.setOrientation(RecyclerView.VERTICAL);
        searchRecycleView.setLayoutManager(searchManager);

        //broadcast when bond state changes
        IntentFilter bondStateChangeIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondStateChangeBroadcastReceiver, bondStateChangeIntent);


        if (myBluetoothAdapter.isEnabled()) {
            bluetoothOnOffBtn.setImageResource(R.drawable.ic_baseline_bluetooth_24);
            bondedDevice();
        } else {
            bluetoothOnOffBtn.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
        }


        bluetoothOnOffBtn.setOnClickListener(v -> {
            bluetoothOnOFFMethod();

        });


        enableDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "making device discoverable for 300 sec", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "making device discoverable for 300 sec");
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

                IntentFilter discoverIntentFilter = new IntentFilter(myBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(discoverOnOffBroadcastReceiver, discoverIntentFilter);
            }
        });

        searchDeviceBtn.setOnClickListener(v -> {
            checkBTPermission();

            if (myBluetoothAdapter.isDiscovering()) {
                myBluetoothAdapter.cancelDiscovery();

                checkBTPermission();
                Toast.makeText(this, "Starting to discover device", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Starting to discover device");
                myBluetoothAdapter.startDiscovery();
                IntentFilter searchDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(searchDeviceBroadcastReceiver, searchDeviceIntent);

            }
            if (!myBluetoothAdapter.isDiscovering()) {
                checkBTPermission();
                Toast.makeText(this, "Starting to discover device", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Starting to discover device");
                myBluetoothAdapter.startDiscovery();
                IntentFilter searchDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(searchDeviceBroadcastReceiver, searchDeviceIntent);

            }
            searchDevice();
        });
    }

    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {

        }
    }

    private void bluetoothOnOFFMethod() {

        if (myBluetoothAdapter == null) {
            //Device does not support Bluetooth
            Toast.makeText(this, "Bluetooth does not support on this devices", Toast.LENGTH_SHORT).show();
        } else {
            if (!myBluetoothAdapter.isEnabled()) {
                //code to enable bluetooth
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBluetoothIntent);

                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(onoffBroadcastReceiver, BTIntent);
            }

            if (myBluetoothAdapter.isEnabled()) {
                myBluetoothAdapter.disable();
                bluetoothOnOffBtn.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
            }
        }
    }


    private void bondedDevice() {
        Set<BluetoothDevice> bondedDevices = myBluetoothAdapter.getBondedDevices();
        bondedDeviceModelList.clear();

        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                bondedDeviceModelList.add(new DeviceModel(device));
                //Toast.makeText(this, strings[index - 1] + "...." + device.getBluetoothClass(), Toast.LENGTH_SHORT).show();
            }
        }
        bondedAdapter.notifyDataSetChanged();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT) {
//            if (resultCode == RESULT_OK) {
//                //Bluetooth is Enabled
//                Toast.makeText(this, "Bluetooth is Enable", Toast.LENGTH_SHORT).show();
//                bondedDevice();
//                bluetoothOnOffBtn.setImageResource(R.drawable.ic_baseline_bluetooth_24);
//            } else if (resultCode == RESULT_CANCELED) {
//                // bluetooth enable is cancelled
//                Toast.makeText(this, "Bluetooth Enable is Cancelled", Toast.LENGTH_LONG).show();
//                bluetoothOnOffBtn.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
//            }
//        } else {
//            Toast.makeText(this, "mmm", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void searchDevice() {


    }

    ////////////////////////// all the broad cast /////////////////////////////

    private final BroadcastReceiver onoffBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, myBluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "onoffBroadcastReceiver: STATE_OFF");
                    bluetoothOnOffBtn.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "onoffBroadcastReceiver: STATE_TURNING_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    bluetoothOnOffBtn.setImageResource(R.drawable.ic_baseline_bluetooth_24);
                    bondedDevice();
                    Log.d(TAG, "onoffBroadcastReceiver: STATE_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:


                    Log.d(TAG, "onoffBroadcastReceiver: STATE_TURNING_ON");
                    break;
            }
        }
    };

    private final BroadcastReceiver discoverOnOffBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (mode) {
                    //device is in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "discoverability enabling");
                        Toast.makeText(context, "discoverability enabling", Toast.LENGTH_SHORT).show();
                        break;
                    //device is not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "discoverability disable, able to receive connection");
                        Toast.makeText(context, "discoverability disable, able to receive connection", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "discoverability disable, unable to receive connection");
                        Toast.makeText(context, "discoverability disable, unable to receive connection", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "Connecting");
                        Toast.makeText(context, "connecting", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "Connected");
                        Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        }
    };


    private final BroadcastReceiver searchDeviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "searching device");
            Toast.makeText(context, "searching", Toast.LENGTH_SHORT).show();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, device.getName());
                Toast.makeText(context, device.getName().toString(), Toast.LENGTH_SHORT).show();
                searchDeviceModelList.add(new DeviceModel(device));
                searchAdapter = new SearchAdapter(searchDeviceModelList, myBluetoothAdapter);
                searchRecycleView.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "No Device Found");
                Toast.makeText(context, "no device found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final BroadcastReceiver bondStateChangeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //case 1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BOND_BONDED");
                    if (!bondedDeviceModelList.contains(mDevice)){
                        bondedDeviceModelList.add(new DeviceModel(mDevice));
                        //Toast.makeText(this, strings[index - 1] + "...." + device.getBluetoothClass(), Toast.LENGTH_SHORT).show();
                        bondedAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(context, "Bonded", Toast.LENGTH_SHORT).show();
                }
                //case 2: creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BOND_BONDING");
                    Toast.makeText(context, "bonding", Toast.LENGTH_SHORT).show();
                }
                //case 3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BOND_NONE");
                    Toast.makeText(context, "breaking bond", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onoffBroadcastReceiver);
        unregisterReceiver(discoverOnOffBroadcastReceiver);
        unregisterReceiver(searchDeviceBroadcastReceiver);
        unregisterReceiver(bondStateChangeBroadcastReceiver);
    }
}