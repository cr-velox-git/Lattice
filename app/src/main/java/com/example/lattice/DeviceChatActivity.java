package com.example.lattice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class DeviceChatActivity extends AppCompatActivity {

    private TextView msg_box, status;
    private EditText writeMsg;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BT_CHAT";
    private static final UUID MY_UUID = UUID.fromString("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_chat);

        if (!bluetoothAdapter.isEnabled()) {

            if (bluetoothAdapter == null) {
                //Device does not support Bluetooth
                Toast.makeText(this, "Bluetooth does not support on this devices", Toast.LENGTH_SHORT).show();
            } else {
                //code to enable bluetooth
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);

            }
        }

        //start the server
        BluetoothServerClass serverClass = new BluetoothServerClass();
        serverClass.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                //Bluetooth is Enabled
                Toast.makeText(this, "Bluetooth is Enable", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // bluetooth enable is cancelled
                Toast.makeText(this, "Bluetooth Enable is Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }


    Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    //todo
                    break;
            }
            return true;
        }
    });

    public class BluetoothServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public void BluetoothServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    //Toast.makeText(DeviceChatActivity.this, e, Toast.LENGTH_LONG).show();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);

                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    //do something to send or receive
                    break;
                }
            }
        }

    }

    private class ClientClass extends Thread{

        private BluetoothSocket socket;
        private BluetoothDevice device;

        public ClientClass(BluetoothDevice device1){
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }

        public void run(){
            bluetoothAdapter.cancelDiscovery();
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
                //Something to send / receive message
            }catch (IOException ex){
                ex.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
            }
        }

    }
}