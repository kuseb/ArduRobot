package com.example.sebastian.ardurobot.controller;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.ardurobot.ArduRobotApplication;
import com.example.sebastian.ardurobot.R;
import com.example.sebastian.ardurobot.model.BluetoothClient;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class ListDevicesAndConnectActivity extends ListActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private final static int REQUEST_BLUETOOTH = 1;
    private BluetoothDevice[] mPairedDevices;
    private BluetoothDevice[] mFoundDevices;
    private Set<BluetoothDevice> mDevices;
    private ArrayAdapter<String> mBluetoothArrayAdapter;
    private Handler mHandler;
    private BluetoothClient mBluetoothClient;

    private Button mSearchButton;
    private TextView mListTextView;
    private ProgressBar mSearchProgressBar;
    private int mFoundDevicesIterator;
    private final int maxSize=20;
    private UUID mUUID;
    private boolean isPairedDevicesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listdevices);

        setVariables();
        enableBluetoothAdapter();
        setPairedDevicesIntent();
    }

    private void enableBluetoothAdapter()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBT.putExtra("BluetoothResult",true);
              startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        }
    }

    private void setVariables()
    {
        mUUID=UUID.randomUUID();
        mPairedDevices=new BluetoothDevice[maxSize];
        mFoundDevices=new BluetoothDevice[maxSize];
        mFoundDevicesIterator =0;

        mSearchProgressBar=(ProgressBar)findViewById(R.id.searchProgressBar);
        mSearchProgressBar.setVisibility(View.INVISIBLE);

        setAndRegisterBroadcastReceiver();
        setSearchButton();;

        isPairedDevicesList=true;
        mListTextView=(TextView)findViewById(R.id.listTextView);
        mBluetoothArrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);

    }

    private void setAndRegisterBroadcastReceiver()
    {
        mBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    mBluetoothArrayAdapter.add(device.getName()+"\n"+device.getAddress());
                    if(mFoundDevicesIterator<maxSize) {
                        mFoundDevices[mFoundDevicesIterator] = device;
                        mFoundDevicesIterator++;
                    }
                }
                else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                    mSearchButton.setEnabled(true);
                    mSearchProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        IntentFilter mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction((BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        this.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }


    private void setSearchButton()
    {
        mSearchButton=(Button)findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFoundDevicesIntent();
            }
        });
    }

    private void setPairedDevicesIntent()
    {
        isPairedDevicesList=true;
        mPairedDevices=new BluetoothDevice[maxSize];
        mListTextView.setText(getResources().getString(R.string.list_paired_devices));

        if(!mBluetoothArrayAdapter.isEmpty())
        mBluetoothArrayAdapter.clear();

        stopSearch();
        postGetDevicesMethod();
    }

    private void setFoundDevicesIntent()
    {
        isPairedDevicesList = false;
        mFoundDevices=new BluetoothDevice[maxSize];
        mFoundDevicesIterator = 0;
        mListTextView.setText(getResources().getString(R.string.list_found_devices));

        if(!mBluetoothArrayAdapter.isEmpty())
        mBluetoothArrayAdapter.clear();

        startSearch();
    }

    private void stopSearch()
    {
        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        mSearchButton.setEnabled(true);
        mSearchProgressBar.setVisibility(View.INVISIBLE);
    }

    private void startSearch()
    {
        mSearchButton.setEnabled(false);
        mSearchProgressBar.setVisibility(View.VISIBLE);
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
       //// Toast.makeText(this, "Dziala!", Toast.LENGTH_LONG);
       // super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_BLUETOOTH) {
            if (mBluetoothAdapter.isEnabled())
                postGetDevicesMethod();
            else
                startActivity(new Intent(this, StartActivity.class));
        }
        //startActivity(new Intent(this,StartActivity.class));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        stopSearch();

        if(isPairedDevicesList) {
            ProgressDialog dialog = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title), getResources().getString(R.string.progress_dialog_message), true);
            if(connect(mPairedDevices[position]))
            {
                this.finish();
                Toast.makeText(this,"Connected",Toast.LENGTH_LONG);
            }

        }
        else
        {
            if(!isPairedDevicesContain(mFoundDevices[position])) {
                pairDevice(mFoundDevices[position]);
            }
            else {
                Toast.makeText(this, "Sparowany", Toast.LENGTH_LONG).show();
            }

            setPairedDevicesIntent();
        }

        //setResult(RESULT_OK, intent);
       // finish();
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postGetDevicesMethod()
    {
        mHandler=new Handler();
        mHandler.postDelayed(getDevices, 100);
    }

    private Runnable getDevices=new Runnable() {
        @Override
        public void run() {
            mDevices = mBluetoothAdapter.getBondedDevices();
            mPairedDevices=setToArray(mDevices);
            setDevicesList();
            mHandler.removeCallbacks(getDevices);
        }
    };

    private static BluetoothDevice[] setToArray(Set<BluetoothDevice> source)
    {
        BluetoothDevice[] destination=new BluetoothDevice[source.size()];
        int pom=0;
        for(BluetoothDevice b: source)
        {
            destination[pom]=b;
            pom++;
        }

        return destination;
    }

    private void setDevicesList()
    {
        if (mPairedDevices[0] !=null) {

            for (BluetoothDevice device : mPairedDevices) {
                String deviceName = device.getName();
                String deviceAddress=device.getAddress();
                mBluetoothArrayAdapter.add(deviceName + "\n" + deviceAddress);
            }

        }
        setListAdapter(mBluetoothArrayAdapter);
    }

    private boolean connect(BluetoothDevice device) {
        mBluetoothClient = ((ArduRobotApplication) getApplication()).getBluetoothClient();
        mBluetoothClient = new BluetoothClient(device);
        mBluetoothClient.start();

        ((ArduRobotApplication) getApplication()).setBluetoothClient(mBluetoothClient);
        try {
            mBluetoothClient.sendData(1);
        } catch (IOException e) {
            Log.d("DATASENDERROR", "Could not send msg");
        }

        return true;
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    private boolean isPairedDevicesContain(BluetoothDevice bluetoothDevice)
    {
     for(BluetoothDevice b: mPairedDevices) {
         if (b.getAddress().equals(bluetoothDevice.getAddress()))
             return true;
     }
        return false;
    }


}


