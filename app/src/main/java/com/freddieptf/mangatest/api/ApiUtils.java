package com.freddieptf.mangatest.api;

import android.util.Log;

import com.freddieptf.mangatest.API_KEYS;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fred on 10/9/15.
 */
public class ApiUtils {

    public static String getResultString(URL baseUrl){
        String LOG_TAG = "getResultString";
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            Utilities.Log(LOG_TAG, "Url: " + baseUrl);
            httpURLConnection = (HttpURLConnection)baseUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("X-Mashape-Key", API_KEYS.API_KEY);
            httpURLConnection.connect();

            int statusCode = httpURLConnection.getResponseCode();

            if(statusCode != 200) return "";

            Log.i(LOG_TAG, "Status Code: " + statusCode);

            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            if(stringBuilder.toString() == null || stringBuilder.toString().equals("null")
                    || stringBuilder.length() <= 10) return "";

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
            return "";
        }finally {
            if(httpURLConnection != null) httpURLConnection.disconnect();
            if(bufferedReader != null) try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
