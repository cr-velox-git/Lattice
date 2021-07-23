package com.example.lattice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceChatActivity extends AppCompatActivity {

    private String TAG = "DeviceChatActivity:- ";

    private TextView status, chatDevice;
    private EditText writeMsg;
    private Button send;
    private RecyclerView recyclerView;

    SendReceive sendReceive;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice bluetoothDevice;

    //connection condition
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    int REQUEST_ENABLE_BLUETOOTH = 1;

    private List<ChatModel> chatModelList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private static final String APP_NAME = "LACTTICE";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_chat);

        Log.d(TAG,"Device Chat activity lunched");

        status = findViewById(R.id.status);
        send = findViewById(R.id.send);
        writeMsg = findViewById(R.id.writeMssg);
        recyclerView = findViewById(R.id.chat_recycleview);
        chatDevice = findViewById(R.id.chat_device_name);
        bluetoothDevice = (BluetoothDevice) getIntent().getExtras().get("BLUETOOTH_DEVICE");

        chatDevice.setText(bluetoothDevice.getName());
        //deviceUDID(this);
        bluetoothAdapter.cancelDiscovery();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        chatAdapter = new ChatAdapter(chatModelList);
        recyclerView.setAdapter(chatAdapter);


        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG,"checking bluetooth connection");
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
        ServerClass serverClass = new ServerClass();
        serverClass.start();

        //
        ClientClass clientClass = new ClientClass(bluetoothDevice);
        clientClass.start();

        //we will set the
        status.setText("Connecting");

        send.setOnClickListener(v -> {
            String string = String.valueOf(writeMsg.getText());
            sendReceive.write(string.getBytes());
            chatModelList.add(new ChatModel(string, ChatModel.SEND));

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                //Bluetooth is Enabled
                Log.d(TAG,"Bluetooth enabled");
                Toast.makeText(this, "Bluetooth is Enable", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // bluetooth enable is cancelled
                Log.d(TAG,"Bluetooth enabling canceled");
                Toast.makeText(this, "Bluetooth Enable is Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("Listening");
                    Log.d(TAG,"listenting for connection");
                    send.setEnabled(true);
                    break;
                case STATE_CONNECTING:
                    Log.d(TAG,"connecting to device");
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    Log.d(TAG,"connected to the device");
                    status.setText("Connected");
                    send.setEnabled(true);
                    break;
                case STATE_CONNECTION_FAILED:
                    Log.d(TAG,"connection failed with the device");
                    status.setText("Connection Failed");
                    send.setEnabled(false);
                    break;
                case STATE_MESSAGE_RECEIVED:
                    Log.d(TAG,"message received");
                    send.setEnabled(true);
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    //on message receive

                    chatModelList.add(new ChatModel(tempMsg, ChatModel.RECEIVE));
                    chatAdapter.notifyDataSetChanged();

                    break;
            }
            return true;
        }
    });


    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;
            Log.d(TAG,"listinting for incoming message");
            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    Log.d(TAG,"STATE CONNECTING");
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    Log.d(TAG,"STATE CONNECTION FAILED");
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    Log.d(TAG,"STATE CANNECTED");
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                writeMsg.setText("");
                chatAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}