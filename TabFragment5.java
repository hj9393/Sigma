package com.example.win10.sigma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class TabFragment5 extends Fragment {
    TextView txt_spinner;
    public static Spinner spinner_km;
    public static int Receiver_distance;
    int KM_position;
    public TabFragment5()    {  }
  //  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TabFragment", "TabFragment5");
        View view = inflater.inflate(R.layout.tab_fragment_5, container, false);
        spinner_km = (Spinner) view.findViewById(R.id.spinner_km);
        txt_spinner = (TextView) view.findViewById(R.id.txt_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.txt_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_km.setAdapter(adapter);

        spinner_km.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = getActivity().getSharedPreferences("KM",0); //
                SharedPreferences.Editor editor = settings.edit();
                KM_position = spinner_km.getSelectedItemPosition(); //현재 선택된 인덱스값 가져오기
                Receiver_distance = Integer.parseInt(spinner_km.getSelectedItem().toString());
           //     Toast.makeText(getActivity(),Recevier_distance,Toast.LENGTH_SHORT).show();
                editor.putInt("KM",KM_position); //선택된 값을 저장
                editor.putInt("Receiver",Receiver_distance);
                editor.apply();
                try {
                    FileOutputStream fos = getActivity().openFileOutput("Receiver_distance.txt", // 파일명 지정
                                    Context.MODE_PRIVATE);// 저장모드
                    PrintWriter out = new PrintWriter(fos);
                    out.println(Receiver_distance);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }); //setOnItemSelectedListener

        SharedPreferences settings = this.getActivity().getSharedPreferences("KM",0);
        KM_position = settings.getInt("KM",KM_position); //마지막으로 선택된 인덱스값 가져오기.
        spinner_km.setSelection(KM_position); //인덱스로 값 설정
        Log.i("프래퍼런스세팅",Integer.toString(KM_position));
        return view;
    }//onCreateView

    public static String getSpinner_KM()
    {
        return spinner_km.getSelectedItem().toString();
    }
}//TabFragment5
