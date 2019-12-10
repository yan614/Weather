package com.example.weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseAreaFragment extends AppCompatActivity {
    final int LEVEL_PROVINCE = 0;
    final int LEVEL_CITY = 1;
    //final int LEVEL_COUNTY = 2;

    private TextView txtTitle;
    private Button btnBack;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    public static boolean isOk=false;

    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        queryProvinces();
        isOk=false;
        txtTitle = (TextView) findViewById(R.id.txt_title);
        btnBack = (Button)findViewById(R.id.btn_back);
        listView = (ListView)findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(ChooseAreaFragment.this, R.layout.simple_list_item, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentLevel) {
                    case LEVEL_PROVINCE:
                        selectedProvince = provinceList.get(position);
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        selectedCity = cityList.get(position);
                        String city_code=selectedCity.getCityCode();
                        API api = new API(city_code);
                        JsonObj jsono = new JsonObj();
                        //api.getJSON（）先获得JSON文件
                        //jsonobj.getData()，获得JSON文件里的各项信息，并把信息传给 weather
                        String data = api.getJSON();
                        Weather weather = jsono.getData(data);
                        String cityid=weather.getId();
                        Log.d("choosefragment:","cityid"+cityid);
                        isOk=true;
                        Intent intent =new Intent(ChooseAreaFragment.this,MainActivity.class);
                        intent.putExtra("id",cityid);
                        startActivity(intent);
                        break;

                    default:
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    queryProvinces();
            }
        });
    }

    private void queryProvinces() {
        txtTitle.setText("中国");
        btnBack.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            //
        }
    }

    private void queryCities() {
        txtTitle.setText(selectedProvince.getProvinceName());
        btnBack.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if (cityList.size() > 0) {

                dataList.clear();
                for (City c : cityList) {
                    dataList.add(c.getCityName());
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel = LEVEL_CITY;

        } else {
            //
        }
    }
/*
    private void queryCounties() {
        txtTitle.setText(selectedCity.getCityName());
        btnBack.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0) {
            try {
                dataList.clear();
                for (County c : countyList) {
                    dataList.add(c.getCountyName());
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel = LEVEL_COUNTY;
            } catch (NullPointerException e) {
                int provinceCode = selectedProvince.getProvinceCode();
                String url = getResources().getString(R.string.url_query_province) + provinceCode;
                queryFromServer(url, "city");
                url = getResources().getString(R.string.url_query_province)
                        + selectedProvince.getProvinceCode() + "/"
                        + selectedCity.getCityCode();
                queryFromServer(url, "county");
            }
        } else {
            String url = getResources().getString(R.string.url_query_province)
                    + selectedProvince.getProvinceCode() + "/"
                    + selectedCity.getCityCode();
            queryFromServer(url, "county");
        }
    }*/

}
