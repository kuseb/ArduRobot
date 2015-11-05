package com.example.sebastian.ardurobot.model;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Sebastian on 02.11.2015.
 */
public class BluetoothClient extends Thread {
    private final BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mBluetoothDevice;
    private final UUID mUUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Thread receiver;

    public BluetoothClient(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mBluetoothDevice = device;
        try {
            tmp = device.createRfcommSocketToServiceRecord(mUUID);
        } catch (Exception e) { }
        mBluetoothSocket = tmp;

        receiver=new Thread() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted())
                {
                    try {
                        int data = receiveData();
                    }
                    catch (IOException e) {
                        Log.d("BLUETOOTHCLIENT", "IOException");
                    }
                }
            }
        };

        receiver.start();
    }

    @Override
    public void run() {
        try {
            Log.d("BLUETOOTHCLIENT", "Starting connection...");
            mBluetoothSocket.connect();
            Log.d("BLUETOOTHCLIENT","Connected");
            BufferedReader in = new BufferedReader(new InputStreamReader(mBluetoothSocket.getInputStream()));
            String input = in.readLine();

        } catch (Exception ce) {
            try {
                mBluetoothSocket.close();
            } catch (Exception cle) { }
        }

    }

    public void sendData(int data) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4);
        output.write(data);
        OutputStream outputStream = mBluetoothSocket.getOutputStream();
        outputStream.write(output.toByteArray());
    }

    public int receiveData() throws IOException
    {
        byte[] buffer = new byte[4];
        ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        InputStream inputStream = mBluetoothSocket.getInputStream();
        inputStream.read(buffer);
        return input.read();
    }

}
