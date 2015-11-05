package com.example.sebastian.ardurobot.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Sebastian on 09.10.2015.
 */
public class BluetoothServer extends Thread {
    private BluetoothSocket mSocket;
    private UUID mUUID;
    private final String serviceName="ArduRobot";
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothServer() {
        mUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    public void run() {
        BluetoothServerSocket temp = null;
        try {
            temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(serviceName, mUUID);
        } catch (IOException e) {
            Log.d("SERVERCONNECT", "Could not get a BluetoothServerSocket:" + e.toString());
        }
        while (true) {

            try {
                mSocket = temp.accept();
            } catch (IOException e) {
                Log.d("SERVERCONNECT", "Could not accept an incoming connection.");
                break;
            }
            if (mSocket != null) {
                try {
                    temp.close();
                } catch (IOException e) {
                    Log.d("SERVERCONNECT", "Could not close ServerSocket:" + e.toString());
                }
                break;
            }
        }
    }

    public void closeConnect() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.d("SERVERCONNECT", "Could not close connection:" + e.toString());
        }
    }

    public void sendData(int data) throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream(4);
        output.write(data);
        OutputStream outputStream = mSocket.getOutputStream();
        outputStream.write(output.toByteArray());
    }

    public int receiveData() throws IOException{
        byte[] buffer = new byte[4];
        ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        InputStream inputStream = mSocket.getInputStream();
        inputStream.read(buffer);
        return input.read();
    }
}

