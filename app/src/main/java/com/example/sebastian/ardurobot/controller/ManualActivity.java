package com.example.sebastian.ardurobot.controller;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.ardurobot.ArduRobotApplication;
import com.example.sebastian.ardurobot.R;
import com.example.sebastian.ardurobot.model.BluetoothClient;
import com.zerokol.views.JoystickView;

import java.io.IOException;

public class ManualActivity extends ActionBarActivity {

    private TextView angleValueTextView;
    private TextView powerValueTextView;
    private TextView direcValueTextView;
    // Importing also other views
    private JoystickView joystick;
    private BluetoothClient mBluetoothClient;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        angleValueTextView = (TextView) findViewById(R.id.angleValueTextView);
        powerValueTextView = (TextView) findViewById(R.id.powerValueTextView);
        direcValueTextView = (TextView) findViewById(R.id.direcValueTextView);
        //Referencing also other views
        joystick = (JoystickView) findViewById(R.id.joystickView);
        mBluetoothClient=((ArduRobotApplication) getApplication()).getBluetoothClient();
        if(mBluetoothClient == null)
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_connected), Toast.LENGTH_LONG).show();
        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
                // TODO Auto-generated method stub
                if(angle<0)
                    angle+=360;

                angleValueTextView.setText(" " + String.valueOf(angle));
                powerValueTextView.setText(" " + String.valueOf(power) + "%");

                if(mBluetoothClient!=null) {
                    try {
                        mBluetoothClient.sendData(toDataSend(angle, power));
                    } catch (IOException e) {
                    }
                }

                switch (direction) {
                    case JoystickView.FRONT:
                        direcValueTextView.setText("N");
                        break;
                    case JoystickView.FRONT_RIGHT:
                        direcValueTextView.setText("NE");
                        break;
                    case JoystickView.RIGHT:
                        direcValueTextView.setText("E");
                        break;
                    case JoystickView.RIGHT_BOTTOM:
                        direcValueTextView.setText("SE");
                        break;
                    case JoystickView.BOTTOM:
                        direcValueTextView.setText("S");
                        break;
                    case JoystickView.BOTTOM_LEFT:
                        direcValueTextView.setText("SW");
                        break;
                    case JoystickView.LEFT:
                        direcValueTextView.setText("W");
                        break;
                    case JoystickView.LEFT_FRONT:
                        direcValueTextView.setText("NW");
                        break;
                    default:
                        direcValueTextView.setText("0");
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    private int toDataSend(int angle, int power)
    {
        int res;
        res=toAngleSend(angle);
        res=res << 3;
        res+=power/12.5;
        return res;
    }

    private int toAngleSend(int angle)
    {
        if(angle>=347 || angle<=13)
            angle=0;
        else if(angle>13 && angle<=35)
            angle=24;
        else if(angle>35 && angle<61)
            angle=48;
        else if(angle>=61 && angle<83)
            angle=72;
        else if(angle>=83 && angle<=109)
            angle=96;
        else if(angle>109 && angle<=131)
            angle=120;
        else if(angle>131 && angle<=157)
            angle=144;
        else if(angle>157 && angle<179)
            angle=168;
        else if(angle>=179 && angle<=205)
            angle=192;
        else if(angle>205 && angle<=227)
            angle=216;
        else if(angle>227 && angle<250)
            angle=240;
        else if(angle>=250 && angle<270)
            angle=264;
        else if(angle>=270 && angle<290)
            angle=288;
        else if(angle>=290 && angle<313)
            angle=312;
        else if(angle>=313 && angle<337)
            angle=336;
        else if(angle>=337 && angle<347)
            angle=360;

        return angle/24;
    }

    @Override
    protected void onStop()
    {
        if(mBluetoothClient!=null) {
            try {
                mBluetoothClient.sendData(128);
            } catch (IOException e) {
            }
        }
    }
}
