package com.raijin.demoboy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortOut;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    // OSC
    final static String SERVER_IP = "192.168.1.149";
    final static int OSC_PORT = 6448;
    private OSCPortOut oscPortOut;

    final static String SENSOR_FORMAT = "%+.3f";

    // Pure Data
    private PdUiDispatcher dispatcher;

    // Sensors
    WifiManager wifiManager;
    private BroadcastReceiver rssiReceiver;
    private static final float MAX_ACC = 9.8F;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener accListener;
    private float[] gravity, linear_acceleration, acceleration, distance, velocity;

    // UI
    private TextView accXView, accYView, accZView, wifiView, velView, dispView;
    private FloatingActionButton holdBtn1, holdBtn2, holdBtn3, holdBtn4;

    // Sound triggers
    private boolean isBGMChecked = false;
    private boolean isFMChecked = true;
    private boolean[] isHoldBtn = new boolean[4];

    private void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    private void initGUI() {
        // Audio Toggle Switch
        Switch audio_toggle = (Switch) findViewById(R.id.audio_toggle);
        audio_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PdBase.sendFloat("start", 1);
                } else {
                    PdBase.sendFloat("start", 0);
                }
                isBGMChecked = isChecked;
            }
        });

        // FM Toggle Switch
        Switch fm_toggle = (Switch) findViewById(R.id.fm_toggle);
        fm_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PdBase.sendFloat("startFM", 1);
                } else {
                    PdBase.sendFloat("startFM", 0);
                }
                isFMChecked = isChecked;
            }
        });

        // Wifi Data Display
        wifiView = (TextView) findViewById(R.id.wifi_strength);

        // Velocity & Displacement
        velView = (TextView) findViewById(R.id.vel_view);
        dispView = (TextView) findViewById(R.id.disp_view);

        // Accelerometer Data Displays
        accXView = (TextView) findViewById(R.id.acc_x);
        accYView = (TextView) findViewById(R.id.acc_y);
        accZView = (TextView) findViewById(R.id.acc_z);

        // Hold Buttons
        holdBtn1 = (FloatingActionButton) findViewById(R.id.holdbtn1);
        holdBtn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldBtn[0] = true;
                        PdBase.sendFloat("note", 48);
                        return true;
                    case MotionEvent.ACTION_UP:
                        isHoldBtn[0] = false;
                        PdBase.sendFloat("holdBtn1", 0);
                        return true;
                }
                return false;
            }
        });

        holdBtn2 = (FloatingActionButton) findViewById(R.id.holdbtn2);
        holdBtn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldBtn[1] = true;
                        PdBase.sendFloat("note", 52);
                        return true;
                    case MotionEvent.ACTION_UP:
                        isHoldBtn[1] = false;
                        PdBase.sendFloat("holdBtn2", 0);
                        return true;
                }
                return false;
            }
        });

        holdBtn3 = (FloatingActionButton) findViewById(R.id.holdbtn3);
        holdBtn3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldBtn[2] = true;
                        PdBase.sendFloat("note", 55);
                        return true;
                    case MotionEvent.ACTION_UP:
                        isHoldBtn[2] = false;
                        return true;
                }
                return false;
            }
        });

        holdBtn4 = (FloatingActionButton) findViewById(R.id.holdbtn4);
        holdBtn4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldBtn[3] = true;
                        PdBase.sendFloat("note", 59);
                        return true;
                    case MotionEvent.ACTION_UP:
                        isHoldBtn[3] = false;
                        PdBase.sendFloat("holdBtn4", 0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGUI();
        initSensors();

        // Initialize PD
        try {
            initPD();
            loadPdPatch();
        } catch (IOException e) {
            finish();
        }
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Initialize sensor data
        gravity = new float[3];
        linear_acceleration = new float[3];
        acceleration = new float[3];
        velocity = new float[3];
        distance = new float[3];

        // Set Listeners
        accListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                acceleration[0] = event.values[0];
                acceleration[1] = event.values[1];
                acceleration[2] = event.values[2];

                if (isBGMChecked) {
                    float tempo = 250 + 500*(normalize(acceleration[2]));
                    PdBase.sendFloat("sequencerBeat", tempo);
                }

                if (isFMChecked) {
                    float modIndex = (float) (abs(acceleration[0]) / 9.8 * 5);
                    PdBase.sendFloat("fs", modIndex*440);
                }
                // Refactor this shit

                if (isHoldBtn[3]) {
                    PdBase.sendNoteOn(1, 59, 127);
                }

                if (isHoldBtn[2]) {
                    PdBase.sendNoteOn(1, 55, 127);
                }

                if (isHoldBtn[1]) {
                    PdBase.sendNoteOn(1, 52, 127);
                }

                if (isHoldBtn[0]) {
                    PdBase.sendNoteOn(1, 48, 127);
                }

                final double alpha = 0.8;

                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = (float) (alpha * gravity[0] + (1 - alpha) * event.values[0]);
                gravity[1] = (float) (alpha * gravity[1] + (1 - alpha) * event.values[1]);
                gravity[2] = (float) (alpha * gravity[2] + (1 - alpha) * event.values[2]);

                // Remove the gravity contribution with the high-pass filter.
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                velocity[0] = velocity[0] + linear_acceleration[0];
                velocity[1] = velocity[1] + linear_acceleration[1];
                velocity[2] = velocity[2] + linear_acceleration[2];

                distance[0] = distance[0] + velocity[0];
                distance[1] = distance[1] + velocity[1];
                distance[2] = distance[2] + velocity[2];

                velView.setText("Velocity: X: " + String.format(SENSOR_FORMAT, velocity[0])
                                + "\tY: " + String.format(SENSOR_FORMAT, velocity[1])
                                + "\tZ: " + String.format(SENSOR_FORMAT, velocity[2]));

                /*
                dispView.setText("Displacement\n X: " + String.format(SENSOR_FORMAT, distance[0])
                        + "\tY: " + String.format(SENSOR_FORMAT, distance[1])
                        + "\tZ: " + String.format(SENSOR_FORMAT, distance[2]));

                */

                accXView.setText("X: " + String.format(SENSOR_FORMAT, linear_acceleration[0])
                                 + "\t\t\tRaw X: " + String.format(SENSOR_FORMAT, acceleration[0]));
                accYView.setText("Y: " + String.format(SENSOR_FORMAT, linear_acceleration[1])
                        + "\t\t\tRaw Y: " + String.format(SENSOR_FORMAT, acceleration[1]));
                accZView.setText("Z: " + String.format(SENSOR_FORMAT, linear_acceleration[2])
                        + "\t\t\tRaw Z: " + String.format(SENSOR_FORMAT, acceleration[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Set Wifi Sensors
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        rssiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int newRssi = wifiManager.getConnectionInfo().getRssi();
                wifiView.setText("Wifi strength: " + String.valueOf(newRssi));
                wifiManager.startScan();
            }
        };

        IntentFilter rssiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // this.registerReceiver(rssiReceiver, rssiFilter);

        // Set Motion Sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(accListener, accelerometer, 100000);
    }

    private float normalize(float in) {
        // Normalize sensor value between -1 to 1
        return in / MAX_ACC;
    }

    private void loadPdPatch() throws IOException {
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.main), dir, true);
        File pdPatch = new File(dir, "main.pd");
        PdBase.openPatch(pdPatch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PdAudio.startAudio(this);
        IntentFilter rssiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(rssiReceiver, rssiFilter);
        wifiManager.startScan();


    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
        this.unregisterReceiver(rssiReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Destroy instances of PD to prevent multiple instances which causes distortion
        PdAudio.release();
        PdBase.release();
    }
}
