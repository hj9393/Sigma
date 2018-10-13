package com.example.win10.sigma;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by WIN10 on 2017-05-20.
 */

public class VideoPlayer extends AppCompatActivity{
    String myJSON;
    JSONArray peoples = null;
    VideoView video;
    Toolbar videotoolbar;
    String ServerUrl;
    WebView webview;
    private String Video_key;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private static final String TAG_RESULTS="result";
    private static final String TAG_Video_path = "Video_path";
    private static final String TAG_Video_name = "Video_name";
    private String VIDEO_PATH="http://35.163.148.207/home/ubuntu";


    private BackPressCloseSystem bpcs;

    public static final int progress_bar_type = 0;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer);
        bpcs = new BackPressCloseSystem(this);// 뒤로 가기 버튼 이벤트
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        webview = (WebView)findViewById(R.id.webview);
        Intent intent = getIntent();
        Video_key = intent.getStringExtra("Video_Key");
        videotoolbar = (Toolbar)findViewById(R.id.videotoolbar);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#000000"));
        setSupportActionBar(videotoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 홈버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_file_download_black_24dp); //툴바 홈버튼 이미지 변경



        video = (VideoView)findViewById(R.id.video);
        final MediaController mediaController =  new MediaController(this);
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Uri videourl = Uri.parse("http://35.163.148.207/file_download4.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString()+"&Video_name="+Video_key);
        video.setVideoURI(videourl);
        video.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //메뉴버튼 클릭
      //  Uri download = Uri.parse("http://35.163.148.207/file_download2.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString()+"&Date="+Video_key);
        ServerUrl = "http://35.163.148.207/file_download4.php?MachineNum="+mainpage.Machine_savecode+"&Video_path=./"+mFirebaseUser.getEmail().toString()+"&Video_name="+Video_key;

        Log.i("download",mainpage.Machine_savecode+" "+mFirebaseUser.getEmail().toString()+" " +Video_key);
        StringBuilder output = new StringBuilder();
        try {

            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri u = Uri.parse(ServerUrl);

            i.setData(u);
            startActivity(i);
        }
        catch(Exception ex){
            Log.i("download","fail");
            Toast.makeText(this,"다운로드실패", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this,"다운로드합니다...",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //you have the permission now.
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ServerUrl));
            request.setTitle("Vertretungsplan");
            request.setDescription("wird heruntergeladen");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String filename = URLUtil.guessFileName(ServerUrl, null, MimeTypeMap.getFileExtensionFromUrl(ServerUrl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            DownloadManager manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }
    @Override
    public void onBackPressed() {
        video.stopPlayback();
        finish();
    }
    protected void showList(){
        try {
            if(myJSON != null) {
                JSONObject jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray(TAG_RESULTS);

                for (int i = 0; i < peoples.length(); i++) {
                    JSONObject c = peoples.getJSONObject(i);
                    String vpath = c.getString(TAG_Video_path);
                    vpath = vpath.substring(1,vpath.length());
                    String vname = c.getString(TAG_Video_name);
                    VIDEO_PATH += vpath+"/"+vname;
                    Log.i("VIDEO",VIDEO_PATH);
                }
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
                loading = ProgressDialog.show(VideoPlayer.this, "Please Wait", null, true, true);
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
