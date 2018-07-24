package com.tikou.seekbarwithinstruct;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;


import com.tikou.mylibrary.UiSeeKBar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    UiSeeKBar uiSeeKBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiSeeKBar= (UiSeeKBar) findViewById(R.id.ui_seekbar);
        uiSeeKBar.setProgress(20);


    }



}
