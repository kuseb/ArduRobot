package com.example.sebastian.ardurobot;

import android.app.Application;

import com.example.sebastian.ardurobot.model.BluetoothClient;

/**
 * Created by Sebastian on 25.10.2015.
 */
public class ArduRobotApplication extends Application {
    private BluetoothClient mBluetoothClient;

    public BluetoothClient getBluetoothClient() {
        return mBluetoothClient;
    }

    public void setBluetoothClient(BluetoothClient bluetoothClient) {
        mBluetoothClient = bluetoothClient;
    }

}
