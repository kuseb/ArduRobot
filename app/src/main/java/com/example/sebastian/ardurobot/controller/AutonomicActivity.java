package com.example.sebastian.ardurobot.controller;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sebastian.ardurobot.ArduRobotApplication;
import com.example.sebastian.ardurobot.R;
import com.example.sebastian.ardurobot.model.BluetoothClient;
import com.example.sebastian.ardurobot.model.Timer;

import java.io.IOException;

public class AutonomicActivity extends ActionBarActivity {

    private Button mStartButton;
    private Button mStopButton;
    private EditText mTimeEditText;
    private TextView mRemainValueTextView;
    private ProgressBar mProgressBar;
    private ToggleButton mCleanButton;
    private Timer mTimer;
    private BluetoothClient mBluetoothClient;
    private final int START=128;
    private final int STOP=255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomic);

        mTimeEditText=(EditText) findViewById(R.id.timeEditText);
        mRemainValueTextView=(TextView) findViewById((R.id.remainValueTextView));

        mProgressBar=(ProgressBar) findViewById(R.id.progressBar);
        mCleanButton=(ToggleButton)findViewById(R.id.cleanToggleButton);

        mStartButton=(Button)findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeString=mTimeEditText.getText().toString();
                if(!timeString.matches("")) {
                    if(mBluetoothClient!=null) {
                        try {
                            mBluetoothClient.sendData(START);
                        }
                        catch(IOException  e){}
                    }
                    else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_connected), Toast.LENGTH_LONG).show();

                    int time = Integer.valueOf(timeString);
                    mTimer = new Timer(mRemainValueTextView, mProgressBar, mStartButton, mStopButton, mCleanButton,mBluetoothClient);
                    mTimer.setInitSeconds(time);
                    mTimer.start();
                }
            }
        });

        mStopButton=(Button)findViewById(R.id.stopButton);
        mStopButton.setEnabled(false);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer.stop();
                if (mBluetoothClient != null) {
                    try {
                        mBluetoothClient.sendData(STOP);
                    } catch (IOException e) {
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_connected), Toast.LENGTH_LONG).show();
            }
        });

        mBluetoothClient=((ArduRobotApplication) getApplication()).getBluetoothClient();


    }

}
