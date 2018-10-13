package com.example.win10.sigma;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by WIN10 on 2017-04-14.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Refreshed token : " + token);

        sendRegistrationToServer(token);
    }

    //Add Custom implementation, ass needed.
    private void sendRegistrationToServer(String token)
    {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("Token",token).build();

        Log.d(TAG,"Refreshed token : " + token);
        //Request
        Request request = new Request.Builder().url("http://35.163.148.207/register.php").post(body).build();
        try{
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
