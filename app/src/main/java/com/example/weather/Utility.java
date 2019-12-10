package com.example.weather;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject object = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    if(object.getInt("pid")==0){

                        province.setId(object.getInt("id"));
                        province.setProvinceCode(object.getString("city_code"));
                        province.setProvinceName(object.getString("city_name"));
                        Log.d("Utility","cityname:"+province.getProvinceName());
                        province.save();
                    }

                    if(object.getInt("pid")!=0){
                        City city = new City();
                        city.setId(object.getInt("id"));
                        city.setCityName(object.getString("city_name"));
                        city.setCityCode(object.getString("city_code"));
                        city.setProvinceId(object.getInt("pid"));
                        Log.d("Utility","cityname1:"+city.getCityName());
                        city.save();}
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }
}
