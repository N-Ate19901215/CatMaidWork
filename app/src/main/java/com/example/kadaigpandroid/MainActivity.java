package com.example.kadaigpandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.nk.AndroidBaseSystem.R;

public class MainActivity extends AppCompatActivity {
    NKGLView glView;                // 画面描画用
    static SoundPlayer snd;
    public static Point DisplaySize;
    public static Point RealSize;
    public static Point ViewSize;
    DisplaySizeCheck disp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disp = new DisplaySizeCheck();
        DisplaySize = disp.getDisplaySize(this);
        RealSize = disp.getRealSize(this);

        snd = new SoundPlayer(this);

        glView = new NKGLView(this);
        setContentView(glView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ViewSize = DisplaySizeCheck.getViewSize(glView);
    }

}

