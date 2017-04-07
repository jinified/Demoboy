package com.raijin.demoboy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class GameActivity extends AppCompatActivity {

    //UI
    Toolbar _toolbar;
    Button _button1, _button2, _button3, _button4, _button5, _button6;
    Button _buttonC, _buttonD, _buttonE, _buttonF, _buttonG;

    private  void initGUI() {
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);

        _button1 = (Button) findViewById(R.id.button_1);
        _button1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _button2 = (Button) findViewById(R.id.button_2);
        _button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _button3 = (Button) findViewById(R.id.button_3);
        _button3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _button4 = (Button) findViewById(R.id.button_4);
        _button4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _button5 = (Button) findViewById(R.id.button_5);
        _button5.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _button6 = (Button) findViewById(R.id.button_6);
        _button6.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _buttonC = (Button) findViewById(R.id.button_c);
        _buttonC.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _buttonD = (Button) findViewById(R.id.button_d);
        _buttonD.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _buttonE = (Button) findViewById(R.id.button_e);
        _buttonE.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _buttonF = (Button) findViewById(R.id.button_f);
        _buttonF.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });

        _buttonG = (Button) findViewById(R.id.button_g);
        _buttonG.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initGUI();
    }

}
