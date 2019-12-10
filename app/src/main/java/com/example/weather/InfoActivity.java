package com.example.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

public class InfoActivity extends AppCompatActivity implements  View.OnClickListener{
    private int pos;
    private Weather weather;
    private Handler handler;
    private String pref1="weather0";
    private String pref2="myMark";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        handler = new Handler();
        //获得page里存的position 也就是位置
        //如果从查询历史传来的
        if(MainActivity.ishis){
            String temp=getIntent().getStringExtra("page");
            pos=Integer.parseInt(temp);
            weather = get(pos,pref1);
            MainActivity.ishis=false;
        }
        //如果是从我的关注传来的
        if(MainActivity.ismark){
            String temp=getIntent().getStringExtra("page1");
            pos=Integer.parseInt(temp);
            weather = get(pos,pref2);
            MainActivity.ismark=false;
        }/*
        String temp=getIntent().getStringExtra("page");
        pos=Integer.parseInt(temp);//pos就是被点击的item的位置*/
        Log.d("infoActivity","page: "+pos);
        //获得所有的天气信息
        refresh();
        Button return_bt=(Button)findViewById(R.id.return_button);
        return_bt.setOnClickListener(this);
        Button refresh_bt=(Button)findViewById(R.id.refresh_button);
        refresh_bt.setOnClickListener(this);
        Button guanzhu_bt=(Button)findViewById(R.id.guanzhu);
        guanzhu_bt.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_button:
                startActivity(new Intent(InfoActivity.this, MainActivity.class));
                break;
            case R.id.refresh_button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String cityid = weather.getId() + "";
                        API api = new API(cityid);
                        JsonObj jsonobj = new JsonObj();
                        Weather w = jsonobj.getData(api.getJSON());
                        //pos就是mainactivity里传来的点击子项的位置对应也是sharedpreferences里的位置weather00 01 02
                        set(pos, w,pref1);
                        weather = w;
                        refresh();
                    }
                }).start();
                break;

            case R.id.guanzhu:
                int cishu=0;
                for(int i=0;i<3;i++){
                    //如果当前pref2里存在该城市，则关注失败
                    if(exist(i,pref2,weather)){
                        cishu=cishu+1;
                        Toast.makeText(InfoActivity.this,"您已关注该城市！",Toast.LENGTH_SHORT).show();
                    }
                }
                //遍历完当前的pref2  weather都没有和pref2相等，则成功
                if(cishu==0){
                    add(MainActivity.myMarkList,weather,pref2);
                    Toast.makeText(InfoActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }
    //把最新的查询结果放到SharedPreferences最前面
    public void add(LinkedList<Weather> list, Weather w, String s) {

        if (list.size() == 0) {
            set(0, w,s);
        } else if (list.size() == 1) {
            set(1, get(0,s),s);
            set(0, w,s);
        } else if (list.size() == 2 || list.size() == 3) {
            set(2, get(1,s),s);
            set(1, get(0,s),s);
            set(0, w,s);
        }
    }
    public void set(int num, Weather w,String s) {
        SharedPreferences.Editor editor = getSharedPreferences(s + num, MODE_PRIVATE).edit();
        editor.putString("id", w.getId());
        editor.putString("province", w.getProvince());
        editor.putString("city", w.getCity());
        editor.putString("updateTime", w.getUpdateTime());
        editor.putString("date", w.getDate());
        editor.putString("temperature", w.getTemperature());
        editor.putString("humidity", w.getHumidity());
        editor.putString("PM25", w.getPM25());
        editor.putString("ganmao",w.getGanmao());
        editor.apply();
    }
    public Weather get(int num,String s) {
        Weather w = new Weather();
        SharedPreferences sharedPreferences = getSharedPreferences(s+ num, MODE_PRIVATE);
        w.setId(sharedPreferences.getString("id", ""));
        w.setProvince(sharedPreferences.getString("province", ""));
        w.setCity(sharedPreferences.getString("city", ""));
        w.setUpdateTime(sharedPreferences.getString("updateTime",""));
        w.setDate(sharedPreferences.getString("date", ""));
        w.setTemperature(sharedPreferences.getString("temperature", ""));
        w.setHumidity(sharedPreferences.getString("humidity", ""));
        w.setPM25(sharedPreferences.getString("PM25", ""));
        w.setGanmao(sharedPreferences.getString("ganmao",""));
        return w;
    }
    public void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView city = findViewById(R.id.cityText);
                city.setText(weather.getProvince() + weather.getCity());
                TextView date = findViewById(R.id.dateText);
                date.setText(weather.getDate());
                TextView temperature = findViewById(R.id.temperatureText);
                temperature.setText(weather.getTemperature());
                TextView humidity = findViewById(R.id.humidityText);
                humidity.setText(weather.getHumidity());
                TextView PM25 = findViewById(R.id.PM25Text);
                PM25.setText(weather.getPM25());
                TextView updateTime = findViewById(R.id.updateTimeText);
                updateTime.setText(weather.getUpdateTime());
                TextView ganmao = findViewById(R.id.ganmaoText);
                ganmao.setText(weather.getGanmao());
            }
        });

    }
    public boolean exist(int num,String s,Weather w) {
        //Weather w = new Weather();
        SharedPreferences sharedPreferences = getSharedPreferences(s + num, MODE_PRIVATE);
        if (w.getCity().equals(sharedPreferences.getString("city",""))){
            return true;
        }
        return false;
    }
}
