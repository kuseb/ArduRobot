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

import com.example.sebastian.ardurobot.ArduRobotApplication;
import com.example.sebastian.ardurobot.R;
import com.example.sebastian.ardurobot.model.BluetoothServer;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ListDevicesActivity extends ListActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private final static int REQUEST_BLUETOOTH = 1;
    private BluetoothDevice[] mPairedDevices;
    private BluetoothDevice[] mFoundDevices;
    private Set<BluetoothDevice> mDevices;
    private ArrayAdapter<String> mBluetoothArrayAdapter;
    private Handler mHandler;
    private BluetoothServer mBluetoothServer;

    private Button mSearchButton;
    private TextView mListTextView;
    private ProgressBar mSearchProgressBar;
    private int mFoundDevicesIterator;
    private final int maxSize=20;
    private UUID mUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listdevices);

        mUUID=UUID.randomUUID();
        mPairedDevices=new BluetoothDevice[maxSize];
        mFoundDevices=new BluetoothDevice[maxSize];

        mFoundDevicesIterator =0;
        //Tworzenie listy element?w sparowanych
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //Je?li jest wy??czony
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        }
        //Dodawanie na koniec kolejki wiadomo?ci
        mHandler=new Handler();
        mHandler.postDelayed(getDevices, 100);

        mSearchProgressBar=(ProgressBar)findViewById(R.id.searchProgressBar);
        mSearchProgressBar.setVisibility(View.INVISIBLE);


        //Tworzenie obiektu odpowiadaj?cego za wyszukiwanie urz?dze?
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
        this.registerReceiver(mBroadcastReceiver,mIntentFilter);

        mSearchButton=(Button)findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchButton.setEnabled(false);
                mSearchProgressBar.setVisibility(View.VISIBLE);
                mListTextView.setText(getResources().getString(R.string.list_found_devices));
                mBluetoothArrayAdapter.clear();
                mBluetoothAdapter.startDiscovery();
            }
        });

        mListTextView=(TextView)findViewById(R.id.listTextView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
       //// Toast.makeText(this, "Dziala!", Toast.LENGTH_LONG);
       // super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_BLUETOOTH && resultCode==RESULT_CANCELED)
            startActivity(new Intent(this,StartActivity.class));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        ProgressDialog dialog = ProgressDialog.show(this, "Po��czenie", "Prosz� czeka�...", true);
        mHandler=new Handler();
        mHandler.post(connect);

        //setResult(RESULT_OK, intent);
       // finish();
    }

    private Runnable getDevices=new Runnable() {
        @Override
        public void run() {

            while(!mBluetoothAdapter.isEnabled())
            {
               // finishActivity(REQUEST_BLUETOOTH);
                //startActivity(new Intent(getApplicationContext(),StartActivity.class));

            }

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
        mBluetoothArrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
        if (mPairedDevices[0] !=null) {

            for (BluetoothDevice device : mPairedDevices) {
                String deviceName = device.getName();
                String deviceAddress=device.getAddress();
                mBluetoothArrayAdapter.add(deviceName + "\n" + deviceAddress);
            }

        }
        setListAdapter(mBluetoothArrayAdapter);
    }

    private Runnable connect=new Runnable() {
        @Override
        public void run() {
            mBluetoothServer=((ArduRobotApplication)getApplication()).getBluetoothServer();
            mBluetoothServer =new BluetoothServer();
            mBluetoothServer.start();

            try {
                mBluetoothServer.sendData(1);
            }
            catch (IOException e)
            {
                Log.d("DATASENDERROR","Could not send msg");
            }
        }
    };

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }
}


