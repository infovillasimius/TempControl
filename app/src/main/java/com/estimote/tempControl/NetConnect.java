package com.estimote.tempControl;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class NetConnect extends AsyncTask<String,String,String> {
    private static final String IP_REGEX = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$"; //"[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*";
    private static String baseUrl = "http://192.168.1.120/s/";

    @Override
    protected String doInBackground(String ... params) {
        String key=params[0];
        String data=params[1];
        try {
            String request = URLEncoder.encode(key+"___"+data, "UTF-8");
            String url = baseUrl + request;


            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(false);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response= conn.getResponseCode();
            return ("Data send -> "+ response);


        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return ("not send 1");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return ("not send 2");
        } catch (IOException e1) {
            e1.printStackTrace();
            return ("not send 3");
        }

    }

    public static String getBaseUrl() {
        return baseUrl.substring(7, 20);
    }

    public static boolean setBaseUrl(String baseUrl) {
        if (baseUrl.matches(IP_REGEX)) {
            NetConnect.baseUrl = "http://" + baseUrl + "/s/";
        }
        return baseUrl.matches(IP_REGEX);
    }



}

