package com.example.sebastian.ardurobot;

import android.app.Application;

import com.example.sebastian.ardurobot.model.BluetoothServer;

/**
 * Created by Sebastian on 25.10.2015.
 */
public class ArduRobotApplication extends Application {
    private BluetoothServer mBluetoothServer;

    public BluetoothServer getBluetoothServer() {
        return mBluetoothServer;
    }

    public void setBluetoothServer(BluetoothServer bluetoothServer) {
        mBluetoothServer = bluetoothServer;
    }

}
