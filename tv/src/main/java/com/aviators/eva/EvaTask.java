package com.aviators.eva;

import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by A-7413 on 6/16/17.
 */

public class EvaTask extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... postbody) {
        try {


            String stringArray = postbody[0];
            URL url = new URL(Configuration.api_endpoint);
            String text = "";
            BufferedReader reader = null;

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/vnd.exp-offers.v1+json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(stringArray);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();

            return text;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
