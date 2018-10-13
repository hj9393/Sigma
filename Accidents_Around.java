package com.example.win10.sigma;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by WIN10 on 2017-04-14.
 */

public class Accidents_Around extends FragmentActivity implements OnMapReadyCallback{
    private EditText edt_address;
    private GoogleMap googleMap;
    private Button btn_address;
    private String data_coordinates;
    private String data_address;
    private int KM_position;
    LatLng latlng;
    private static final String TAG = "Accidents_Around";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accidents_around);

        Intent intent = getIntent();
        data_address = intent.getStringExtra("Key_address");
        data_coordinates = intent.getStringExtra("Key_coordinates");
        String data [] = data_coordinates.split(", ");
        Double lat = Double.parseDouble(data[0]);
        Double lng = Double.parseDouble(data[1]);
        latlng = new LatLng(lat, lng);

        btn_address = (Button)findViewById(R.id.btn_address);
        btn_address.setText(data_address);
        
        btn_address.setClickable(false);
        btn_address.setFocusable(false);
    /*    edt_address = (EditText)findViewById(R.id.edt_address);
        edt_address.setText(data);
        edt_address.setFocusable(false);
        edt_address.setClickable(false);
        edt_address.getBackground().setColorFilter(Color.parseColor("#009999"), PorterDuff.Mode.SRC_IN);*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


  //      MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.addMarker(new MarkerOptions().position(latlng).title(data_address+"\n"+data_coordinates));

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,14.0f)); //줌레벨 1 ~ 23

        onAddMarker();
    }
    //마커 , 원추가
    public void onAddMarker(){
        //나의위치 마커
        MarkerOptions mymarker = new MarkerOptions()
                .position(latlng);   //마커위치
        // 반경 원
        CircleOptions circle = new CircleOptions().center(latlng) //원점
                .radius(1000)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#55E96A6A")); //배경색

        //마커추가
        this.googleMap.addMarker(mymarker);

        //원추가
        this.googleMap.addCircle(circle);
    }
}
