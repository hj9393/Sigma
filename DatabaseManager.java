package com.example.win10.sigma;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by WIN10 on 2017-05-16.
 */

public class DatabaseManager extends AsyncTask<String, Integer, String> {
    ArrayList<String> list;
    public DatabaseManager(String... params) //file_path , columns
    {
        list =new ArrayList<String>();
        for(int i =0; i<params.length; i++)
            list.add(params[i]); //리스트에 file_path와 DB columns이름 저장

    }
    @Override
    protected String doInBackground(String... params) {
        try{

          //  Log.i("Parmas",String.valueOf(params.length));
            String link="http://35.163.148.207/"+list.get(0);
            String data2 =URLEncoder.encode(list.get(1), "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8"); //첫번째 칼럼
            Log.i("Parmas",String.valueOf(link));
            if(params.length > 1) {
                for(int i=1; i<params.length; i++)
                    data2 += "&" + URLEncoder.encode(list.get(i+1), "UTF-8") + "=" + URLEncoder.encode(params[i], "UTF-8"); //두번쨰 칼럼부터
            }
            Log.i("Parmas",String.valueOf(data2));
        /*    String data  = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");*/
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data2 );
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();
        }
        catch(Exception e){
            Log.i("Exception_ERROR",e.getMessage());
            return new String("Exception: " + e.getMessage());
        }

    }
}
