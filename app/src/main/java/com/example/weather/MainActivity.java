package com.example.weather;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.litepal.crud.DataSupport;

import java.util.LinkedList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler handler;
    public  EditText editText;
    private LinkedList<Weather> weatherList;
    public static LinkedList<Weather> myMarkList;
    private char Case;
    private String pref1="weather0";
    private String pref2="myMark";
    public static boolean ishis=false;
    public static boolean ismark=false;
    String citycode;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            switch (Case) {
                case '1':
                    Toast.makeText(MainActivity.this,"id城市不存在",Toast.LENGTH_SHORT).show();

                    Case = '0';
                    break;
                case '2':
                    Toast.makeText(MainActivity.this,"城市不存在",Toast.LENGTH_SHORT).show();
                    Case = '0';
                    break;
                case '3':
                    WeatherAdapter adapter = new WeatherAdapter(MainActivity.this, R.layout.weather_item, weatherList);
                    ListView listView = findViewById(R.id.list_view);
                    listView.setAdapter(adapter);
                    Case = '0';
                    break;
                default:
                    break;
            }
        }
    };

    private void sendRequestWithOkHttp() {
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String strurl= "http://10.3.148.110:8080/city.json";
                    //String strurl= "http://10.3.130.165:8080/city.json";
                    /*
                    Request request=new Request.Builder().url(strurl).build();
                    Response response=okHttpClient.newCall(request).execute();
                    String responseData=response.body().string();
                    Utility.handleProvinceResponse (responseData);*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ChooseAreaFragment.isOk){
            String temp=getIntent().getStringExtra("id");
            editText.setText(temp);
        }
        //请求一次，把数据存到数据库里就可以了，后续就不需要了
        Button sendRequest = (Button) findViewById(R.id.button);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestWithOkHttp();
                Intent intent =new Intent(MainActivity.this,ChooseAreaFragment.class);
               // intent.putExtra("page",String.valueOf(position));//String.valueOf()把position的数值转换成字符串类型
                startActivity(intent);
            }
        });

        //显示“查询历史”的listview里的信息
        weatherList = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            //判断是否已在sharedpreference里存在
            if (exist(i,pref1)) {
                weatherList.addLast(get(i,pref1));
            } else {
                break;
            }
        }
        //显示“我的关注”的listview里的信息
        myMarkList=new LinkedList<>();
        for(int i=0;i<3;i++){
            if(exist(i,pref2))
            myMarkList.addLast(get(i,pref2));
            else
                break;
        }

        //“查询历史”的适配器和listview的设置
        WeatherAdapter adapter = new WeatherAdapter(MainActivity.this, R.layout.weather_item, weatherList);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        //点击listview里的子项，会触发ContentActivity事件，也就是点击一个地区的条目，会显示其具体信息
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position是所点击item在适配器中的位置，id是所点击的item在listview的第几行
                /*
                List<City> cityList= DataSupport.findAll(City.class);

                if(cityList.size()>0){
                    for(City cc:cityList){
                        if(position==cc.getId()){
                            citycode=cc.getCityCode();
                            break;
                        }
                    }
                }*/
                ishis=true;
                Intent intent =new Intent(
                        MainActivity.this,InfoActivity.class);
                intent.putExtra("page",String.valueOf(position));//String.valueOf()把position的数值转换成字符串类型
                startActivity(intent);
            }
        });
        //我的关注的适配器和Listview的设置
        WeatherAdapter adapter1 = new WeatherAdapter(MainActivity.this, R.layout.citymark, myMarkList);
        ListView listView1 = findViewById(R.id.list_view2);
        listView1.setAdapter(adapter1);
        //点击listview2里的子项，会触发ContentActivity事件，也就是点击一个地区的条目，会显示其具体信息
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ismark=true;
                Intent intent =new Intent(MainActivity.this,InfoActivity.class);
                intent.putExtra("page1",String.valueOf(position));
                startActivity(intent);
            }
        });

        handler = new Handler();
        //显示搜索框
        editText=findViewById(R.id.search);
        //为搜索按钮注册点击事件
        Button search_bt = (Button) findViewById(R.id.search_button);
        search_bt.setOnClickListener(this);
    }

//点击事件的设置
    public void onClick(View v) {
        switch (v.getId()) {
            //点击的是“查询按钮”
            case R.id.search_button:
                //Toast.makeText(MainActivity.this,"你点击了search按钮",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //你输入的城市id
                        String cityid = editText.getText().toString();
                        //Log.d("MainActivity","cityid: "+cityid);
                        //如果你输入的城市id长度不是9，是第一种情况
                        if (cityid.length() != 9) {
                            Case = '1';
                        } else {
                            API api = new API(cityid);
                            //Log.d("MainActivity","api: "+api.getUrl());
                            JsonObj jsono = new JsonObj();
                            //api.getJSON（）先获得JSON文件
                            //jsonobj.getData()，获得JSON文件里的各项信息，并把信息传给 weather
                            String data = api.getJSON();
                            //Log.d("MainActivity","data: "+data);
                            Weather weather = jsono.getData(data);
                            //如果cityId为空或者cityId不存在，这里的cityId是从Json文件里获得到的
                            if (weather.getId() == null || weather.getId().length() < 1) {
                                Case = '2';//城市不存在的情况
                            } else {
                                //要求是至少缓存3次的查询结果，所以weatherlist里存3个城市的信息即可
                                if (weatherList.size() >= 3) {
                                    //Log.d("MainActivity","weather: "+weather.getCity()+weather.getDate());
                                    weatherList.removeLast();
                                }
                                //先写到sharedpreference里
                                add(weatherList,weather,pref1);
                                //添加到weatherlist里，便于输出
                                weatherList.addFirst(weather);
                                Case = '3';
                            }
                        }
                        handler.post(runnable);
                    }
                }).start();
                break;
            default:
                break;
        }
    }
    //把最新的查询结果放到SharedPreferences最前面
    public void add(LinkedList<Weather>list,Weather w,String s) {

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
    //把查询结果写入SharedPreferences，s是sharedpreferences对象名称
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
    //从SharedPreferences里获得结果
    public Weather get(int num,String s) {
        Weather w = new Weather();
        SharedPreferences sharedPreferences = getSharedPreferences(s + num, MODE_PRIVATE);
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
    //先判断查询的信息在不在SharedPreferences里，如果在，就直接获取信息
    public boolean exist(int num,String s) {
        Weather w = new Weather();
        SharedPreferences sharedPreferences = getSharedPreferences(s + num, MODE_PRIVATE);
        return sharedPreferences.getString("id", "").length() > 0;
    }
}