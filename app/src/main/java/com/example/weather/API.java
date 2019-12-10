package com.example.weather;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class API {
    private URL url;
    private HttpURLConnection urlCon;

    public URL getUrl() {
        return url;
    }

    public API(String cityCode) {
        try {
            String url1 = "http://t.weather.sojson.com/api/weather/city/";
            url = new URL(url1 + cityCode);
            urlCon = null;
        } catch (Exception e) {

        }
    }

    public String getJSON() {
        StringBuilder stringBuilder = new StringBuilder("");
        try {
            urlCon = (HttpURLConnection) url.openConnection();
            InputStreamReader inReader = new InputStreamReader(urlCon.getInputStream(), "utf-8");
            BufferedReader buffer = new BufferedReader(inReader);
            stringBuilder = new StringBuilder("");
            String s;
            while ((s = buffer.readLine()) != null) {
                stringBuilder.append(s);
            }
            urlCon.disconnect();
        } catch (Exception e) {

        }
        return stringBuilder.toString();
    }
}
