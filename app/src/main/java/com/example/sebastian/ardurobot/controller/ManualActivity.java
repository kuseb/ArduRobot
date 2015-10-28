package com.example.sebastian.ardurobot.controller;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.example.sebastian.ardurobot.R;
import com.zerokol.views.JoystickView;

public class ManualActivity extends ActionBarActivity {

    private TextView angleValueTextView;
    private TextView powerValueTextView;
    private TextView direcValueTextView;
    // Importing also other views
    private JoystickView joystick;

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

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
                // TODO Auto-generated method stub
                angleValueTextView.setText(" " + String.valueOf(angle));
                powerValueTextView.setText(" " + String.valueOf(power) + "%");
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
}
