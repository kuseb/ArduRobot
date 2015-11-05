package com.example.sebastian.ardurobot.model;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

/**
 * Created by Sebastian on 08.10.2015.
 */
public class Timer {
    private int mInitSeconds;
    private long mRemainInMilliseconds;
    private long mStartTime;
    private Handler mHandler;

    private int mRemainInSeconds;
    private int mRemainInMinutes;
    private int mRemainInHours;

   private TextView mRemainValueTextView;
   private ProgressBar mProgressBar;
   private Button mStartButton;
    private Button mStopButton;
    private ToggleButton mCleanButton;
    private BluetoothClient mBluetoothClient;
    private final int STOP=255;

    public Timer(TextView remainValueTextView, ProgressBar progressBar, Button startButton, Button stopButton,ToggleButton cleanButton, BluetoothClient bluetoothClient)
    {
        mRemainValueTextView=remainValueTextView;
        mProgressBar=progressBar;
        mStartButton =startButton;
        mStopButton=stopButton;
        mCleanButton=cleanButton;
        mBluetoothClient=bluetoothClient;
    }

    public long getInitSeconds() {
        return mInitSeconds;
    }

    public void setInitSeconds(int initSeconds) {
        mInitSeconds = initSeconds;
    }

    public long getRemainInMilliseconds() {
        return mRemainInMilliseconds;
    }

   public void start()
   {
       mStartButton.setEnabled(false);
       mStopButton.setEnabled(true);
       mCleanButton.setEnabled(false);

       mStartTime= SystemClock.uptimeMillis();
       mHandler=new Handler();
       mHandler.post(updateTimer);
   }

    public void stop()
    {
        mRemainValueTextView.setText("0 s");
        mProgressBar.setProgress(100);
        mStartButton.setEnabled(true);
        mStopButton.setEnabled(false);
        mCleanButton.setEnabled(true);

        if(mBluetoothClient!=null) {
            try {
                mBluetoothClient.sendData(STOP);
            } catch (IOException e) {
            }
        }

        mHandler.removeCallbacks(updateTimer);
    }

    private Runnable updateTimer=new Runnable() {
        @Override
        public void run() {
            mRemainInMilliseconds=mInitSeconds*1000+mStartTime-SystemClock.uptimeMillis();
            mRemainInSeconds=(int)(mRemainInMilliseconds/1000);
            mRemainInMinutes=mRemainInSeconds/60;
            mRemainInSeconds=mRemainInSeconds%60;
            mRemainInHours=mRemainInMinutes/60;
            mRemainInMinutes=mRemainInMinutes%60;

            if(mRemainInMilliseconds<=0)
            {
                mHandler.removeCallbacks(updateTimer);
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
            }
            else
            {
                mRemainValueTextView.setText(Print());
                mProgressBar.setProgress(100-(mRemainInSeconds*100/mInitSeconds));

                mHandler.post(updateTimer);
            }

        }
    };

    private String Print()
    {
        if(mRemainInHours>0)
            return mRemainInHours + " h " + mRemainInMinutes + " m "+mRemainInSeconds+" s ";
        else if(mRemainInMinutes>0)
            return mRemainInMinutes+" m "+mRemainInSeconds+ " s ";
        else
            return mRemainInSeconds+" s";
    }



}
