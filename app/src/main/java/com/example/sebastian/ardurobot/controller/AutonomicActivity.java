package com.example.sebastian.ardurobot.controller;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.sebastian.ardurobot.R;
import com.example.sebastian.ardurobot.model.Timer;

public class AutonomicActivity extends ActionBarActivity {

    private Button mStartButton;
    private Button mStopButton;
    private EditText mTimeEditText;
    private TextView mRemainValueTextView;
    private ProgressBar mProgressBar;
    private ToggleButton mCleanButton;
    private Timer mTimer;

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
                    int time = Integer.valueOf(timeString);
                    mTimer = new Timer(mRemainValueTextView, mProgressBar, mStartButton, mStopButton, mCleanButton);
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
            }
        });



    }

}
