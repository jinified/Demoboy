package com.raijin.demoboy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    final static String SENSOR_FORMAT = "%+.3f";
    final static float THRESH = 0.2f;

    // Pure Data
    private PdUiDispatcher dispatcher;
    private float effect_id = 0;
    Float[] intList = new Float[] {56f, 59f, 62f, 65f, 68f};
    List<Float> notes = new ArrayList<Float>(Arrays.asList(intList));
    private String[] symbol = {"tempo", "volume"};
    private float[] xrange = {-0.7f, 1.3f}; // Default control tempo
    private float[] yrange = {1f, 2f}; // Default control volume
    private float[] zrange = {};

    // Sensors
    WifiManager wifiManager;
    private BroadcastReceiver rssiReceiver;
    private static final float MAX_ACC = 9.8F;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener accListener;
    private float[] gravity, linear_acceleration, acceleration, distance, velocity,
                    norm_acc, norm_linear_acc, norm_velocity;

    //UI
    Toolbar _toolbar;
    Button _button1, _button2, _button3, _button4, _button5, _button6;
    Button _buttonC, _buttonD, _buttonE, _buttonF, _buttonG;
    CheckBox _checkBox;

    // Sound triggers
    private boolean isModulating = false;

    private void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    private  void initGUI() {
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);

        _button1 = (Button) findViewById(R.id.button_1);
        _button1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                effect_id = 1;
                symbol = new String[] {"vibrato_freq", "vibrato_delay"};
                xrange = new float[] {1.f, 5f};
                yrange = new float[] {1f, 5f};
                zrange = new float[] {};
                PdBase.sendFloat("effect_id", 1);
            }
        });

        _button2 = (Button) findViewById(R.id.button_2);
        _button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                effect_id = 2;
                symbol = new String[] {"tremolo_freq", "tremolo_gain", "tremolo_depth"};
                xrange = new float[] {15, 25f};
                yrange = new float[] {8f, 15f};
                zrange = new float[] {0.1f, 0.2f};
                PdBase.sendFloat("effect_id", 2);
            }
        });

        _button3 = (Button) findViewById(R.id.button_3);
        _button3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                effect_id = 3;
                symbol = new String[] {"delay_length", "delay_feedback"};
                xrange = new float[] {50f, 300f};
                yrange = new float[] {0f, 0.5f};
                zrange = new float[] {};
                PdBase.sendFloat("effect_id", 3);
            }
        });

        _button4 = (Button) findViewById(R.id.button_4);
        _button4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                effect_id = 4;
                symbol = new String[] {"wha_maxfreq", "wha_minfreq", "wha_q"};
                xrange = new float[] {700f, 6000f};
                yrange = new float[] {0f, 700f};
                zrange = new float[] {0f, 20f};
                PdBase.sendFloat("effect_id", 4);
            }
        });

        _button5 = (Button) findViewById(R.id.button_5);
        _button5.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                effect_id = 5;
                symbol = new String[] {"phaser_speed", "phaser_depth", "phaser_feedback"};
                xrange = new float[] {0f, 10f};
                yrange = new float[] {0f, 3f};
                zrange = new float[] {-0.9f, 0.9f};
                PdBase.sendFloat("effect_id", 5);
            }
        });

        _button6 = (Button) findViewById(R.id.button_6);
        _button6.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                effect_id = 6;
                PdBase.sendFloat("effect_id", 6);
            }
        });

        _buttonC = (Button) findViewById(R.id.button_c);
        _buttonC.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
                if (!isModulating) {
                    Log.i("NOTE", "SEND");
                    PdBase.sendFloat("note", notes.get(0));
                }
            }
        });

        _buttonD = (Button) findViewById(R.id.button_d);
        _buttonD.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!isModulating) {
                    Log.i("NOTE", "SEND");
                    PdBase.sendFloat("note", notes.get(1));
                }
            }
        });

        _buttonE = (Button) findViewById(R.id.button_e);
        _buttonE.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
                if (!isModulating) {
                    Log.i("NOTE", "SEND");
                    PdBase.sendFloat("note", notes.get(2));
                }
            }
        });

        _buttonF = (Button) findViewById(R.id.button_f);
        _buttonF.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
                if (!isModulating) {
                    Log.i("NOTE", "SEND");
                    PdBase.sendFloat("note", notes.get(3));
                }
            }
        });

        _buttonG = (Button) findViewById(R.id.button_g);
        _buttonG.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!isModulating) {
                    Log.i("NOTE", "SEND");
                    PdBase.sendFloat("note", notes.get(4));
                }

            }
        });

        _checkBox = (CheckBox) findViewById(R.id.checkBox);
        _checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    isModulating = true;
                    symbol = new String[] {"tempo", "volume"};
                    xrange = new float[] {-0.7f, 1.3f};
                    yrange = new float[] {1f, 2f};
                    zrange = new float[] {};
                    PdBase.sendFloat("effect_id", 0);
                    PdBase.sendFloat("start", 1);
                } else {
                    isModulating = false;
                    PdBase.sendFloat("effect_id", 0);
                    PdBase.sendFloat("start", 0);
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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

    private boolean isYChanged(float acc_z) {
        return acc_z >= -2.2 && acc_z <= 2.2;
    }

    private boolean isXChanged(float acc_y) {
        return acc_y >= -1  && acc_y <= 1.2;
    }

    private boolean isZChanged(float acc_x) {
        return acc_x >= 7;
    }

    private boolean isSignificantChange(float curr ,float prev, float thresh) {
        float change = Math.abs(curr - prev) / prev;
        // Log.i("changed", String.valueOf(change));
        return change >= thresh;
    }
    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Initialize sensor data
        gravity = new float[3];
        linear_acceleration = new float[3];
        acceleration = new float[3];
        velocity = new float[3];
        distance = new float[3];
        norm_acc = new float[3];
        norm_linear_acc = new float[3];
        norm_velocity = new float[3];

        // Set Listeners
        accListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                acceleration[0] = event.values[0];
                acceleration[1] = event.values[1];
                acceleration[2] = event.values[2];
                Log.i("ACC", String.format("X: %f, Y: %f, Z:%f", acceleration[0], acceleration[1],
                                            acceleration[2]));


                final double alpha = 0.8;

                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = (float) (alpha * gravity[0] + (1 - alpha) * event.values[0]);
                gravity[1] = (float) (alpha * gravity[1] + (1 - alpha) * event.values[1]);
                gravity[2] = (float) (alpha * gravity[2] + (1 - alpha) * event.values[2]);

                // Remove the gravity contribution with the high-pass filter.
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                Log.i("LIN_ACC", String.format("X: %f, Y: %f, Z:%f", linear_acceleration[0],
                        linear_acceleration[1], linear_acceleration[2]));

                velocity[0] = velocity[0] + linear_acceleration[0];
                velocity[1] = velocity[1] + linear_acceleration[1];
                velocity[2] = velocity[2] + linear_acceleration[2];

                Log.i("V", String.format("X: %f, Y: %f, Z:%f", velocity[0], velocity[1], velocity[2]));

                distance[0] = distance[0] + velocity[0];
                distance[1] = distance[1] + velocity[1];
                distance[2] = distance[2] + velocity[2];

                if (isYChanged(acceleration[2])) {
                    float curr = (float) ((9.8 - acceleration[0]) / 9.8);
                    Log.i("norm_Y", String.valueOf(curr));
                    float sent = normalize(curr, yrange[0], yrange[1]);
                    Log.i("SENT_Y", String.valueOf(sent));
                    PdBase.sendFloat(symbol[1], sent);
                    /*
                    if (isSignificantChange(curr, norm_acc[1], THRESH)) {
                        Log.i("YCHANGED", String.valueOf(norm_acc[1]));
                    }
                    */

                }

                if (isXChanged(acceleration[1])) {
                    float sent = (float) (((xrange[1] - xrange[0]) * acceleration[0] / 9.8) + xrange[0]);
                    Log.i("SENT_X", String.valueOf(sent));
                    PdBase.sendFloat(symbol[0], sent);
                    /*
                    if (isSignificantChange(curr, norm_acc[0], THRESH)) {
                        Log.i("XCHANGED", String.valueOf(norm_acc[0]));
                    }
                    */

                }

                if (isZChanged(acceleration[0])) {
                    float curr = (float) ((velocity[2] + 5) / 10);
                    if (symbol.length == 3) {
                        curr = (curr - zrange[0]) / (zrange[1] - zrange[0]);
                        PdBase.sendFloat(symbol[2], curr);
                    }
                    /*
                    if (isSignificantChange(curr, norm_velocity[2], THRESH)) {
                        Log.i("ZCHANGED", String.valueOf(norm_velocity[2]));
                    }
                    */
                }

                if (Math.abs(linear_acceleration[0]) >= 10 && !isModulating) {
                    float playedNote = 48 + Math.abs(linear_acceleration[0]);
                    PdBase.sendFloat("note", playedNote);
                }

                norm_acc[0] = (float) ((9.8 - acceleration[0]) / 9.8);
                norm_acc[1] = (float) (acceleration[1] / 9.8);
                norm_velocity[2] = (float) ((velocity[2] + 5) / 10);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Set Motion Sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(accListener, accelerometer, 100000);
    }

    private float normalize(float x, float a, float b) {
        // Normalize [0 - 1] to [b -a]
        return (b - a) * (x) + a;
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Destroy instances of PD to prevent multiple instances which causes distortion
        PdAudio.release();
        PdBase.release();
    }
}
