package com.need.mymall;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class FixerAPI {
    private static final String key = "4287ffc8484e082888a78adc7c603614";

    public String getExchange() {
        String url = "http://data.fixer.io/api/latest?access_key="+key+"&symbols=USD,VND";
        loadEx asyLoadEx = new loadEx();
        asyLoadEx.execute(url);
        try {
            String exchange = asyLoadEx.get();
            if (exchange==null) {
                return "23173.00";
            } else {
                JSONObject jsonObject = new JSONObject(exchange);
                JSONObject rates = jsonObject.getJSONObject("rates");
                String usa = rates.getString("USA");
                String vnd = rates.getString("VND");
                double result = Double.parseDouble(vnd) / Double.parseDouble(usa);
                return String.valueOf(result);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "23173.00";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "23173.00";
        } catch (JSONException e) {
            e.printStackTrace();
            return "23173.00";
        }
    }

    public class loadEx extends AsyncTask<String,Void,String> {

        private String rs = null;
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
                HttpURLConnection uRLConnection = (HttpURLConnection) url.openConnection();
                uRLConnection.addRequestProperty("Content-Type", "application/json");

                InputStream is = uRLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                StringBuffer sb = new StringBuffer();
                String s;
                while((s = reader.readLine())!=null) {
                    sb.append(s);
                }
                rs = sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rs;
        }
    }
}
