package com.masteryoda117.flowerlife;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.masteryoda117.flowerlife.UIelement.CircleView;

import java.util.ArrayList;

public class HumanActivity extends AppCompatActivity {

    private CircleView circle;

    private Toolbar toolbar;

    private TextView plantView;

    private Button runButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human);
        initUI();

        plantView = findViewById(R.id.plant);
        plantView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HumanActivity.this, FlowerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        runButton = findViewById(R.id.select1);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HumanActivity.this, RunActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUI(){
        //set transparent status bar
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            //get status bar and action bar's height
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            TypedArray actionbarSizeTypedArray = obtainStyledAttributes(new int[] {
                    android.R.attr.actionBarSize
            });
            int actionBarHeight = (int) actionbarSizeTypedArray.getDimension(0, 0);

            //set tool bar's height
            toolbar = findViewById(R.id.tool);
            toolbar.setMinimumHeight(statusBarHeight + actionBarHeight);
        }

        //init circle
        circle = findViewById(R.id.circle);
        circle.setArcWidth(60);
        circle.setValue(0.3f);
    }
}
