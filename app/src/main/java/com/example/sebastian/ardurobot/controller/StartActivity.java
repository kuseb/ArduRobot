package com.example.sebastian.ardurobot.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sebastian.ardurobot.R;

public class StartActivity extends ActionBarActivity {

    private Button mConnectionButton;
    private Button mManualButton;
    private Button mAutonomicButton;
    private final static int REQUEST_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mConnectionButton=(Button)findViewById(R.id.connectionButton);
        mConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(ListDevicesActivity.class);
            }
        });

        mManualButton=(Button)findViewById(R.id.manualButton);
        mManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(ManualActivity.class);
            }
        });

        mAutonomicButton=(Button)findViewById(R.id.autonomicButton);
        mAutonomicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(AutonomicActivity.class);
            }
        });

    }

    private void start(Class c)
    {
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }


    protected void OnResume()
    {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        Toast.makeText(this, "Dziala!", Toast.LENGTH_LONG);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_BLUETOOTH && resultCode==RESULT_CANCELED)
            startActivity(new Intent(this,StartActivity.class));

    }

}
