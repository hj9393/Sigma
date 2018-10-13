package com.example.win10.sigma;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.win10.sigma.TabFragment5.Receiver_distance;
import static com.example.win10.sigma.mainpage.Machine_savecode;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * Created by WIN10 on 2017-04-14.
 */

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private DatabaseManager DB_Manager;
    double distance;
    double set_lat;// = 35.851468; //지정위치
    double set_lng;// = 128.492490; //지정위치
    double now_lat;// = 35.864840; //현재위치
    double now_lng;// = 128.458491; //현재위치
    String latlng_address;
    LocationManager locManager; // 위치 정보 프로바이더
    private String locationProvider = null;
    private Location lastKnownLocation = null;
    LocationManager locationManager;
    Location lastLocation;
    String User_email;
    String Video_name;
    //START receive_message
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
            Log.d(TAG, "Form : " + remoteMessage.getFrom());
        Log.i("MyMessagingService", "=======================================================================");
        DB_Manager = new DatabaseManager("");
        //Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload : " + remoteMessage.getData());
        }

        //Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body : " + remoteMessage.getNotification().getBody());
        }
//        Log.i("MyMessagingService", remoteMessage.getData().toString());
 //       Log.i("MyMessagingService", remoteMessage.getData().get("Gps"));
//        Log.i("MyMessagingService", remoteMessage.getData().get("Machine_code"));
        String gps_values[] = remoteMessage.getData().get("Gps").toString().split(",");
        try {
            set_lat = Double.parseDouble(gps_values[0].trim()); //사고위치 경도
            set_lng = Double.parseDouble(gps_values[1].trim()); //사고위치 위도
        }
       catch (Exception e) { Log.d("MyMessagingService", "GPS수신오류"); return; }
        Log.d("MyMessagingService", "사고위치 : set_lat=" + set_lat + ", set_lng=" + set_lng);

        Read_Machine_savecode(); //기계등록번호 읽어오기
        Log.i("MyMessagingService","Machine_savecode = "+(Machine_savecode));

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION); //권한 여부
            if (permissionCheck == PackageManager.PERMISSION_DENIED) { //권한없음
                Log.d("MyMessagingService", "ACCESS_COARSE_LOCATION=권한없음");
            } else { // 권한 있음
                Log.d("MyMessagingService", "ACCESS_COARSE_LOCATION=권한있음");
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //네트워크 프로바이더달기
            }
            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) { //권한없음
                Log.d("MyMessagingService", "ACCESS_FINE_LOCATION=권한없음");
            } else { // 권한 있음
                Log.d("MyMessagingService", "ACCESS_FINE_LOCATION=권한있음");
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //GPS 프로바이더 달기
            }

            if(lastLocation == null)
            {
                lastLocation.getProvider();
            }
            now_lat = lastLocation.getLatitude(); //현재경도
            now_lng = lastLocation.getLongitude(); //현재위도

            Log.i("MyMessagingService", "now_lat=" + now_lat + ", now_lng=" + now_lng);
            distance = (6371 * acos(cos(toRadians(set_lat)) * cos(toRadians(now_lat)) * cos(toRadians(set_lng) - toRadians(now_lng)) + sin(toRadians(set_lat)) * sin(toRadians(now_lat))));

            Log.i("MyMessagingService", "distance = " + (distance));

            Read_Receiver_distance();//수신거리정보 읽어오기
            Read_User_Id();
        }
        catch (Exception e) {
            Log.i("MyMessagingService", "LocationManager 오류 = "+e.getMessage());
        }

        try {
                Geocoder geocoder = new Geocoder(getApplication());
                List<Address> list = null;
                android.location.Address address = null;
                try {
                    list = geocoder.getFromLocation(set_lat/*위도*/ , set_lng /*경도*/ ,10 /* 얻어올 값의 개수*/);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("MyMessagingService", "입출력 오류 - 서버에서 주소변환시 에러발생");
                }
            if (list != null) {
                if (list.size() == 0) {

                } else {
                    address = list.get(0);
                }
            }
            latlng_address = address.getAdminArea() + " " + address.getLocality() + " " + address.getThoroughfare() + " " + address.getFeatureName();
            if(Machine_savecode.equals(remoteMessage.getData().get("Machine_code").toString())) //사고자 판별, 같으면 사고자
            {
                Log.d("MyMessagingService", "문자전송");
                sendSMS("+821085903439","사고위치 : "+ remoteMessage.getData().get("Gps")+"\n"+
                        address.getAdminArea() + " " + address.getLocality() + " " + address.getThoroughfare() + " " + address.getFeatureName()+" URL : http://35.163.148.207/file_stream2.php?MachineNum="+remoteMessage.getData().get("Machine_code")+"&Video_path=./"+User_email+"&Video_name="+remoteMessage.getData().get("Video_name").toString());
                Video_name = remoteMessage.getData().get("Video_name").toString();
            }
            else
            {
                if(distance <Receiver_distance) {
                    sendNotification(remoteMessage.getData().get("message"));
                    Log.d("MyMessagingService", "푸쉬전송");

                }

            }
        }
        catch (Exception e)
        {
            Log.e("MyMessagingService", "오류 ");
        }

    }
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        getApplicationContext().registerReceiver(new BroadcastReceiver(){
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                 //       Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                 //       Toast.makeText(getApplicationContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                //        Toast.makeText(getApplicationContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
               //         Toast.makeText(getApplicationContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
              //          Toast.makeText(getApplicationContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        getApplicationContext().registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));


        SmsManager sms = SmsManager.getDefault();
      //  sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        ArrayList<String> partMessage = sms.divideMessage(message);
        sms.sendMultipartTextMessage(phoneNumber, null, partMessage, null, null);
    }
    private void sendNotification(String messagBody)
    {
        Log.d("MyFirebaseIIDService","received message : " + messagBody);

        Intent intent = new Intent(this,mainpage.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /*Request code */ , intent , PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.car_icon2)
                .setContentTitle("사고정보!!")
                .setContentText(latlng_address+" "+messagBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock= pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,"TAG");
        wakeLock.acquire(5000);

        notificationManager.notify(0 /*ID of notification */, notificationBuilder.build());
    }
    private void Read_Receiver_distance()
    {
        //알림수신 거리 설정정보
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileInputStream fis = openFileInput("Receiver_distance.txt");//파일명
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴
            while (str != null) {
                Receiver_distance = Integer.parseInt(str);
                data.append(str + "\n");
                str = buffer.readLine();
            }

            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void Read_User_Id()
    {
        //기계 등록번호
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileInputStream fis = openFileInput("User_email.txt");//파일명
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴
            while (str != null) {
                User_email = str;
                data.append(str + "\n");
                str = buffer.readLine();
            }
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void Read_Machine_savecode()
    {
        //기계 등록번호
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileInputStream fis = openFileInput("Machine_savecode.txt");//파일명
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴
            while (str != null) {
                Machine_savecode = str;
                data.append(str + "\n");
                str = buffer.readLine();
            }
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
