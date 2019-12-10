package com.example.weather;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
public class JsonObj {
    public JsonObj() {

    }
    //解析天气Json数据的方法
    public Weather getData(String data) {
        Weather weather = new Weather();
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject cityInfo = jsonObject.getJSONObject("cityInfo");
            //获得市
            String city = cityInfo.getString("city");
            //省id
            String cityId = cityInfo.getString("citykey");
            //获得省
            String parent = cityInfo.getString("parent");
            //获得数据更新时间
            String updateTime=cityInfo.getString("updateTime");
            //当天时间
            String date = jsonObject.getString("date");
            //data里的信息
            JSONObject data1 = jsonObject.getJSONObject("data");
            //温度
            String wendu = data1.getString("wendu");
            //湿度
            String shidu = data1.getString("shidu");
            //pm2.5
            String pm25 = data1.getString("pm25");
            //感冒
            String ganmao=data1.getString("ganmao");
            //向weather对象里设置各个参数
            weather.setProvince(parent);//省
            weather.setCity(city);//市
            weather.setId(cityId);//城市id
            weather.setUpdateTime(updateTime);//更新时间
            weather.setDate(date);//当天日期
            weather.setTemperature(wendu+"℃");//温度
            weather.setPM25(pm25);//pm2.5
            weather.setHumidity(shidu);//湿度
            weather.setGanmao(ganmao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weather;
    }
}
