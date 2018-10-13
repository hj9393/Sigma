package com.example.win10.sigma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class TabFragment3 extends ListFragment {
    ImageView imageview;
    String myJSON;
    ListView list;
    View view;
    ListViewAdapter listViewAdapter;
    ListAdapter adapter;
    Resources resources;
    Drawable d;
    Context context;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String Video_key;
    private static final String TAG_RESULTS="result";
    private static final String TAG_Date = "Date";
    private static final String TAG_Machine_code = "Machine_code";
    private static final String TAG_Video = "Video";
    private static final String TAG_GPS = "Gps";
    private static final String TAG_ADDRESS = "Address";
    private static final String TAG_Video_Name = "Video_name";
      JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;
    public TabFragment3()    {  }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        personList = new ArrayList<HashMap<String,String>>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        listViewAdapter = new ListViewAdapter();
        if(mainpage.Shared_registration_state)
            getData("http://35.163.148.207/read_video.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString());
        else {
            HashMap<String,String> nodata = new HashMap<String,String>();
            nodata.put("NODATA","NO DATA");
            nodata.put("HINTMSG","기기를 등록해주세요");
            personList.add(nodata);
            adapter = new SimpleAdapter(getActivity(),personList,R.layout.nodata,new String[] {"NODATA","HINTMSG"},new int[] {R.id.nodata,R.id.hintmessage});
            setListAdapter(adapter);;
        }
        Log.i("TabFrag","3 oncreate");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TabFragment","TabFragment3");
        view = inflater.inflate(R.layout.tab_fragment_3, container, false);
        mainpage.load();
    //    imageview = (ImageView)view.findViewById(R.id.imageView);
        imageview = (ImageView) view.findViewById(R.id.imageView);
                list = (ListView)view.findViewById(R.id.listview3);
        Bitmap bmThumbnail;
      //  bmThumbnail = ThumbnailUtils.createVideoThumbnail("/sdcard/fountain_night.mp4", MediaStore.Video.Thumbnails.MICRO_KIND);
     //   imageview.setImageBitmap(bmThumbnail);
        resources = getResources();
      //  Toast.makeText(getActivity(),mainpage.Machine_savecode,Toast.LENGTH_SHORT).show();
     //   gPHP = new GettingPHP();
    //    gPHP.execute(Machine_savecode);
        personList = new ArrayList<HashMap<String,String>>();
        context = getContext();

    //    return view;
        return super.onCreateView(inflater,container,savedInstanceState);
    }
    protected void showList(){
        try {

            if(myJSON != null) {
                JSONObject jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray(TAG_RESULTS);

                for (int i = 0; i < peoples.length(); i++) {
                    JSONObject c = peoples.getJSONObject(i);
                    //       String Machine_code = c.getString(TAG_Machine_code);
                    String Date = c.getString(TAG_Date); //날짜
                    String gps = c.getString(TAG_GPS); //GPS
                    String Video_name = c.getString(TAG_Video_Name); //비디오 이름

                    String[] values = gps.split(", ");
                    String lat = values[0]; //위도
                    String lon = values[1]; //경도
                    Geocoder geocoder = new Geocoder(getActivity());
                    List<Address> address_list = null;
                    android.location.Address address = null;
                    try {
                        double d1 = Double.parseDouble(lat);
                        double d2 = Double.parseDouble(lon);

                        address_list = geocoder.getFromLocation(d1, // 위도
                                d2, // 경도
                                10); // 얻어올 값의 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }
                    if (list != null) {
                        if (address_list.size() == 0) {

                        } else {
                            address = address_list.get(0);
                        }
                    }
            /*        Bitmap thumb = ThumbnailUtils.createVideoThumbnail("file path/url", MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                    imageview.setBackgroundDrawable(bitmapDrawable);*/
            //        Uri uri = Uri.parse("http://35.163.148.207/file_download2.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString()+"&Date="+Date);
                //    String URL = "http://35.163.148.207/file_download2.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString()+"&Date="+Date;
               //     Bitmap bit = getVideoFrame(getContext(),uri);
                  //  Bitmap Img= BitmapFactory.decodeResource(getResources(), R.drawable.warnning);

                //    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail("http://35.163.148.207/file_download2.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString()+"&Date="+Date,
                //            MediaStore.Images.Thumbnails.MICRO_KIND);
                //    imageview.setImageBitmap(bitmap);
                    listViewAdapter.addItem(address.getAdminArea() + " " + address.getLocality() + " " + address.getThoroughfare() + " " + address.getFeatureName(),Date,Video_name);

                } //for
                setListAdapter(listViewAdapter);

            }
        } catch (JSONException e) {
 //           Toast.makeText(getActivity(),"실패",Toast.LENGTH_SHORT).show();
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
    public static Bitmap getVideoFrame(Context context, Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri.toString(),new HashMap<String, String>());
            return retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
        return null;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        //"http://35.163.148.207/file_stream.php"
      //  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://35.163.148.207/file_download.php?MachineNum=RASPI01")));
   /*     android.widget.VideoView vv = (android.widget.VideoView) v.findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(vv);
        // Set video link (mp4 format )
        Uri video = Uri.parse("http://35.163.148.207/file_stream.php");
        vv.setMediaController(mediaController);
        vv.setVideoURI(video);
        vv.requestFocus();
        vv.start();*/
        Video_key = listViewAdapter.getvideoname(position);
      //  Toast.makeText(getContext(), Video_key,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), VideoPlayer.class);

        intent.putExtra("Video_Key",Video_key);
        startActivity(intent);
   //     Toast.makeText(getContext(), "동영상",Toast.LENGTH_LONG).show();

    }
}
