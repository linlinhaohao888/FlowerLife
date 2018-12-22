package com.masteryoda117.flowerlife;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.masteryoda117.flowerlife.UIelement.CircleView;
import com.masteryoda117.flowerlife.db.RunningData;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RunActivity extends AppCompatActivity {

    private Button confirmBtn = null;
    private TextView kmNumText = null;
    private TextView timeText = null;
    private TextView numRunningText = null;
    private TextView sumTimeText = null;

    public LocationClient locationClient;

    public enum State {RUNNING, STILL, PAUSE}

    public State state = State.STILL;

    private double startLatitude;
    private double startLongitude;
    private double distance = 0;
    private boolean init = false;
    private int time;
    private int beginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        initView();
        initLocation();
        initData();
    }

    private void initData() {
        List<RunningData> dataList = DataSupport.findAll(RunningData.class);
        int sumTime;
        int numRunning;
        int sumKM;

        sumTime = 0;
        sumKM = 0;
        for (RunningData data : dataList) {
            sumTime += data.getTime();
            sumKM += data.getDistance();
        }
        sumKM /= 1000;
        numRunning = dataList.size();
        int sumHour = sumTime / 3600;
        int sumMin = (sumTime - 3600 * sumHour) / 60;
        int sumSec = sumTime - 3600 * sumHour - 60 * sumMin;

        sumTimeText.setText(String.valueOf(sumHour) + ":" + String.valueOf(sumMin) + ":" + String.valueOf(sumSec));
        numRunningText.setText(String.valueOf(numRunning));
        kmNumText.setText(String.valueOf(sumKM));
        timeText.setText("0:0:0");
    }

    private void initView() {
        //set transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            //get status bar and action bar's height
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            TypedArray actionbarSizeTypedArray = obtainStyledAttributes(new int[]{
                    android.R.attr.actionBarSize
            });
            int actionBarHeight = (int) actionbarSizeTypedArray.getDimension(0, 0);

            //set tool bar's height
            Toolbar toolbar = findViewById(R.id.tool);
            toolbar.setMinimumHeight(statusBarHeight + actionBarHeight);
        }

        confirmBtn = findViewById(R.id.btn_confirm);
        kmNumText = findViewById(R.id.num);
        confirmBtn.setOnClickListener(pauseListener);
        confirmBtn.setOnLongClickListener(runningListener);
        timeText = findViewById(R.id.time_count);
        numRunningText = findViewById(R.id.num_running);
        sumTimeText = findViewById(R.id.sumtime);
    }

    private void initLocation() {
        locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(RunActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(RunActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        if (ContextCompat.checkSelfPermission(RunActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(RunActivity.this, permissions, 1);
        } else {
            locationClient.start();
        }
    }

    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public double getDistance(double curLatitude, double curLongitude) {
        double EARTH_RADIUS = 6378137;
        double radCurLatitude = rad(curLatitude);
        double radStartLatitude = rad(startLatitude);
        double a = radCurLatitude - radStartLatitude;
        double b = rad(curLongitude) - rad(startLongitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radCurLatitude) * Math.cos(radStartLatitude) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }


    private View.OnClickListener pauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (state == State.STILL || state == State.PAUSE) {
                state = State.RUNNING;
                beginTime = (int) new Date().getTime() / 1000;
                confirmBtn.setBackgroundResource(R.drawable.btn_main_pause);
                confirmBtn.setText("");
            } else {
                state = State.PAUSE;
                confirmBtn.setText("RE");
                confirmBtn.setBackgroundResource(R.drawable.btn_main);
            }
        }
    };

    private View.OnLongClickListener runningListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (state == State.RUNNING || state == State.PAUSE) {
                state = State.STILL;
                confirmBtn.setText("GO");
                confirmBtn.setBackgroundResource(R.drawable.btn_main);
                try {
                    finishRunning();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    };

    private void finishRunning() throws IOException {
        init = false;

        RunningData data = new RunningData();
        data.setDistance(distance);
        data.setTime(time);
        data.save();

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("username", "username")
                        .add("password", "password")
                        .add("distance", String.valueOf(distance))
                        .add("time", String.valueOf(time))
                        .add("beginTime", String.valueOf(beginTime))
                        .build();

                Request request = new Request.Builder()
                        .url("http://47.106.112.133/flowerLife/update/")
                        .post(requestBody)
                        .build();

                try {
                    Response response = new OkHttpClient().newCall(request).execute();
                    final String res = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!res.equals("OK")) {
                                Toast.makeText(RunActivity.this, "无法更新数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        initData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    locationClient.start();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (state == State.RUNNING) {
                if (!init) {
                    startLatitude = bdLocation.getLatitude();
                    startLongitude = bdLocation.getLongitude();
                    init = true;
                    time = 0;
                }
                time += 1;
                final int hour = time / 3600;
                final int minute = (time - hour * 3600) / 60;
                final int sec = time - hour * 3600 - minute * 60;
                final double curLatitude = bdLocation.getLatitude();
                final double curLongitude = bdLocation.getLongitude();
                distance += getDistance(curLatitude, curLongitude);
                final DecimalFormat format = new DecimalFormat("#.###");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kmNumText.setText(format.format(distance / 1000));
                        timeText.setText(String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(sec));
                    }
                });
            }
            startLatitude = bdLocation.getLatitude();
            startLongitude = bdLocation.getLongitude();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
    }
}
