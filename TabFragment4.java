package com.example.win10.sigma;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class TabFragment4 extends ListFragment {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private static final String TAG_RESULTS="result";
    private static final String TAG_DATE = "Date";
    String myJSON;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    ListAdapter adapter;
    View view;


    public TabFragment4() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TabFragment", "TabFragment4");
        view = inflater.inflate(R.layout.tab_fragment_4, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        personList = new ArrayList<HashMap<String,String>>();
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


                    HashMap<String, String> persons = new HashMap<String, String>();

                    persons.put(TAG_DATE, date);

                    personList.add(persons);
                }

                adapter = new SimpleAdapter(getActivity(), personList, R.layout.listview_item4, new String[]{TAG_DATE},
                        new int[]{R.id.sms_check}
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
}
