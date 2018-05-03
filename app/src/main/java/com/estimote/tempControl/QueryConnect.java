package com.estimote.tempControl;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class QueryConnect extends AsyncTask<String, String, String> {
    private static final String IP_REGEX = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$"; //"[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*";
    private static String baseUrl = "http://192.168.1.120/q/";

    @Override
    protected String doInBackground(String... params) {
        String timestampStart = params[0];
        String timestampTo = params[1];

        try {
            String request = URLEncoder.encode(timestampStart + "_" + timestampTo, "UTF-8");
            String url = baseUrl + request;

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(false);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            if(conn.getResponseCode()!=200){
                return "Server Error";
            }

            InputStream in = conn.getInputStream();
            StringBuilder textBuilder = new StringBuilder();
            Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
            String response = textBuilder.toString();

            return response;


        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return ("Error 1");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return ("Error 2");
        } catch (IOException e1) {
            e1.printStackTrace();
            return ("Error 3");
        }

    }

    public static String getBaseUrl() {
        return baseUrl.substring(7, 20);
    }

    public static boolean setBaseUrl(String baseUrl) {
        if (baseUrl.matches(IP_REGEX)) {
            QueryConnect.baseUrl = "http://" + baseUrl + "/q/";
        }
        return baseUrl.matches(IP_REGEX);
    }


}

