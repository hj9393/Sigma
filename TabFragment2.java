package com.example.win10.sigma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class TabFragment2 extends ListFragment {
    ListAdapter adapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    ListView list;
    String myJSON;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    private static final String TAG_RESULTS="result";
    private static final String TAG_DATE = "Date";
    private static final String TAG_GPS = "Gps";
    private static final String TAG_ADDRESS = "Address";
    public TabFragment2()    {  }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        personList = new ArrayList<HashMap<String,String>>();
     //   Toast.makeText(getActivity(),mFirebaseUser.getEmail().toString(),Toast.LENGTH_SHORT).show();
        if(mainpage.Shared_registration_state) //기기등록상태
            getData("http://35.163.148.207/read_accident.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString());
        else {
            HashMap<String,String> nodata = new HashMap<String,String>();
            nodata.put("NODATA","NO DATA");
            nodata.put("HINTMSG","기기를 등록해주세요");
            personList.add(nodata);
            adapter = new SimpleAdapter(getActivity(),personList,R.layout.nodata,new String[] {"NODATA","HINTMSG"},new int[] {R.id.nodata,R.id.hintmessage});
            setListAdapter(adapter);;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TabFragment","TabFragment2");
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);
        list = (ListView)view.findViewById(R.id.listview_2);

        return super.onCreateView(inflater,container,savedInstanceState);
    }
    protected void showList(){
        try {
            if(myJSON != null) {
                JSONObject jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray(TAG_RESULTS);

                for (int i = 0; i < peoples.length(); i++) {
                    JSONObject c = peoples.getJSONObject(i);
                    String date = c.getString(TAG_DATE);
                    String gps = c.getString(TAG_GPS);

                    String[] values = gps.split(", ");
                    String lat = values[0]; //위도
                    String lon = values[1]; //경도
                    Geocoder geocoder = new Geocoder(getActivity());
                    List<android.location.Address> list = null;
                    android.location.Address address = null;
                    try {
                        double d1 = Double.parseDouble(lat);
                        double d2 = Double.parseDouble(lon);

                        list = geocoder.getFromLocation(d1, // 위도
                                d2, // 경도
                                10); // 얻어올 값의 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }
                    if (list != null) {
                        if (list.size() == 0) {

                        } else {
                            address = list.get(0);
                        }
                    }

                    HashMap<String, String> persons = new HashMap<String, String>();

                    persons.put(TAG_ADDRESS, address.getAdminArea() + " " + address.getLocality() + " " + address.getThoroughfare() + " " + address.getFeatureName());
                    persons.put(TAG_DATE, date);
                    persons.put(TAG_GPS, gps);

                    personList.add(persons);
                }

                adapter = new SimpleAdapter(getActivity(), personList, R.layout.listview_item1, new String[]{TAG_ADDRESS, TAG_GPS, TAG_DATE},
                        new int[]{R.id.address, R.id.coordinates, R.id.date}
                );

                setListAdapter(adapter);
                //     Toast.makeText(getActivity(),"성공",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
        //    Toast.makeText(getActivity(),"실패",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i("insertToDatabase","onPreExecute");
                loading = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
            }
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
                loading.dismiss();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        //Toast.makeText(getContext(), "좌표 : " + adapter.getCoordinates(position).toString() ,Toast.LENGTH_LONG).show();
        String [] values = adapter.getItem(position).toString().split("=");
        String coordinates = values[1].toString();
        coordinates = coordinates.substring(0,coordinates.length()-6);
        String address = values[3].toString();
        address = address.substring(0,address.length()-1);
        Intent intent = new Intent(getActivity(), Accidents_Around.class);
        //  intent.putExtra("Key",adapter.getItem(position).toString());
        intent.putExtra("Key_address",address);
        intent.putExtra("Key_coordinates",coordinates);
        startActivity(intent);
        //startActivityForResult(intent,position);
    }
}
