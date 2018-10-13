package com.example.win10.sigma;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WIN10 on 2017-04-03.
 */

public class mainpage extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabs;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private BackPressCloseSystem bpcs;
    private boolean LogState;
    private DrawerLayout dlDrawer;
    private ActionBarDrawerToggle dtToggle;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private NavigationView navigationView;
    private static final String STATE_TAG = "STATE_STRING";
    private static final String TAG = "mainpage";
    private static final String MENU_ITEM = "menu_item";
    private int menuItemId;
    private DatabaseManager DB_Manager;
    private static String Shared_Machine_code;
    private TabPagerAdapter pagerAdapter;
    private static String Machine_Info;
    public static String Machine_savecode;
    public static boolean Shared_registration_state;
    private static String Machine_Code;
    private static SharedPreferences appData;
    private static String icon;
    private String email;
    private Context mContext;
    private LayoutInflater inflater;
    private EditText edt_Machine_ID;


    String myJSON;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    private static final String TAG_RESULTS="result";
    private static final String TAG_Machine_code = "Machine_code";
    private static final String TAG_User_id = "User_id";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        email = mFirebaseUser.getEmail();
        Machine_State_Check();
        init();
    }//onCreate

    private void init() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        DB_Manager = new DatabaseManager("insert_machine.php","Machine_code");
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("차량사고시스템"); //툴바 텍스트 변경

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 홈버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_3line); //툴바 홈버튼 이미지 변경

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        bpcs = new BackPressCloseSystem(this);// 뒤로 가기 버튼 이벤트

        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("주변사고 조회"));
        tabs.addTab(tabs.newTab().setText("사고기록 조회"));
        tabs.addTab(tabs.newTab().setText("사고동영상조회"));
     //   tabs.addTab(tabs.newTab().setText("신고정보 조회"));
        tabs.addTab(tabs.newTab().setText("..."));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabs.getTabCount());

        viewPager.setAdapter(pagerAdapter); //각 탭에 대한 리스트뷰

        toolbar.setOnClickListener(new View.OnClickListener() { //툴바 클릭
            @Override
            public void onClick(View v) {
                //           Toast.makeText(mainpage.this,"툴바클릭",Toast.LENGTH_SHORT).show();
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        // Set TabSelectedListener
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i("TabFragment",String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }//init()

    @Override
    public void onBackPressed() {
    /*    dlDrawer.closeDrawers();
        bpcs.onBackPressed();*/
        if (dlDrawer.isDrawerOpen(GravityCompat.START)) {
            dlDrawer.closeDrawer(GravityCompat.START);
        } else {
            bpcs.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //메뉴버튼 클릭
        int id = item.getItemId();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String name = mFirebaseUser.getDisplayName();
        String email = mFirebaseUser.getEmail(); //유저이메일
        switch (id) {
            case android.R.id.home:
                NavigationView navigationView = (NavigationView) dlDrawer.findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);
                View header = navigationView.getHeaderView(0);

                Machine_State_Check();

                Menu menu = navigationView.getMenu();
                MenuItem itemstate =menu.findItem(R.id.nav_registration);
                itemstate.setTitle(Machine_Info);

                TextView user_name = (TextView) header.findViewById(R.id.user_name);
                TextView user_email = (TextView) header.findViewById(R.id.user_email);
                ImageView user_image = (ImageView) header.findViewById(R.id.user_image);

                Uri imgUri = (mFirebaseUser.getPhotoUrl());
                String str = mFirebaseUser.getPhotoUrl().toString();
                user_name.setText(name);
                user_email.setText(email);
                Picasso.with(this).load(imgUri/* url of image */).into(user_image/*your imageview id*/);

                if (dtToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
            case R.id.btn_logout: //로그아웃
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.putExtra("SucessLogOut", true);
                startActivity(intent);
                signOut();
                finish();
                break;
            case R.id.btn_exit: //종료
                Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                signOut();
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Machine_State_Check()
    {
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();
        if(Machine_savecode.equals(""))
        {
            save("기기등록");
        }
    }
    public void signOut() {
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        dtToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Toast.makeText(this,"1",Toast.LENGTH_SHORT).show();

        switch (item.getItemId()) {
            case R.id.nav_home: //홈
                if (!navigationView.getMenu().findItem(R.id.nav_home).isChecked()) {
                    Intent home = new Intent(this, mainpage.class);
                    home.putExtra(STATE_TAG, "home");
                    startActivity(home);
                    finish();
                }
                break;
            case R.id.nav_registration: //기기등록 or 기기정보
            //    item.setTitle(Machine_Info);
                if (item.getTitle().equals("기기등록")) {
                    Log.i("insertToDatabase","기기등록");
                    mContext = getApplicationContext();
                    inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.registration, (ViewGroup) findViewById(R.id.layout_root));
                    AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
                //    final AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
                    aDialog.setTitle("기기등록화면");
                    aDialog.setIcon(R.drawable.car_icon);
                    aDialog.setView(layout);
                    edt_Machine_ID = (EditText) layout.findViewById(R.id.edt_Machine_ID);
                    aDialog.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (edt_Machine_ID.getText().toString().equals("")) {
                                Toast.makeText(mainpage.this, "기기번호를 등록해주세요", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                getData("http://35.163.148.207/read_machine.php?Machine_code="+edt_Machine_ID.getText());

                        //        DB_Manager.execute(edt_Machine_ID.getText().toString());
                   //             insertToDatabase(edt_Machine_ID.getText().toString());
                   //             Toast.makeText(mainpage.this, "기기정보가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                   //             item.setTitle("기기정보");
                         //       item.setIcon(R.drawable.ic_person_black_24dp);
                   //             save("기기정보");
                            }
                        }
                    });
                    aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog ad = aDialog.create();
                    ad.show();
                }
                else //기기정보
                {
            //        gPHP = new GettingPHP();
            //        gPHP.execute(Machine_savecode);

                    AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
                    aDialog.setTitle("기기정보화면");
                    aDialog.setIcon(R.drawable.car_icon);

                    aDialog.setMessage("Machine Code is " + " [" + Machine_savecode + "]");
                    aDialog.setPositiveButton("해제", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mainpage.this, "기기가 해제되었습니다.", Toast.LENGTH_SHORT).show();
                            item.setTitle("기기등록");
                            String data = "null";
          //                  Toast.makeText(mainpage.this,Machine_savecode, Toast.LENGTH_SHORT).show();
                            DB_Manager = new DatabaseManager("update_machine.php","Machine_code");
                            DB_Manager.execute(Machine_savecode);
                         //   item.setIcon(R.drawable.ic_person_add_black_24dp);
                            save("기기등록");
                            load();
                            viewPager.setAdapter(pagerAdapter); //각 탭에 대한 리스트뷰
                        }
                    });
                    aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog ad = aDialog.create();
                    ad.show();
                }
                break;
            case R.id.nav_logout: //로그아웃
                save("기기등록");
                load();

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.putExtra("SucessLogOut", true);
                startActivity(intent);
                signOut();
                viewPager.setAdapter(pagerAdapter); //각 탭에 대한 리스트뷰
                finish();
                break;
            case R.id.nav_version: //버전정보
                AlertDialog.Builder alert_version = new AlertDialog.Builder(this);
                // 메세지
                alert_version.setMessage("Version is " + " [ 1.0 ] ");
                // 확인 버튼 리스너
                alert_version.setPositiveButton("확인", null);
                // 다이얼로그 생성
                AlertDialog version = alert_version.create();
                // 다이얼로그 타이틀
                version.setTitle("Version");
                // 다이얼로그 보기
                version.show();
                break;
            case R.id.nav_developer: //개발자들
                // 다이얼로그 바디
                AlertDialog.Builder alert_developer = new AlertDialog.Builder(this);
                // 메세지
                alert_developer.setMessage("이현건\n" + "배효진\n" + "강혜정\n" + "여아영\n" + "김재명\n");
                // 확인 버튼 리스너
                alert_developer.setPositiveButton("확인", null);
                // 다이얼로그 생성
                AlertDialog developer = alert_developer.create();
                // 아이콘
                developer.setIcon(R.drawable.car_icon);
                // 다이얼로그 타이틀
                developer.setTitle("Developer");
                // 다이얼로그 보기
                developer.show();

                break;
        }
        dlDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            String Machine_code="";
            String User_id="";
//            Toast.makeText(mainpage.this,peoples.length(),Toast.LENGTH_SHORT).show();
            Log.i("insertToDatabase","peoples.length = "+peoples.length());
            for(int i=0;i<peoples.length();i++) {
                JSONObject c = peoples.getJSONObject(i);
                Machine_code = c.getString(TAG_Machine_code);
                User_id = c.getString(TAG_User_id);
                Log.i("insertToDatabase", "Machine_code = " + Machine_code);
                Log.i("insertToDatabase", "User_id = " + User_id);
          /*      HashMap<String,String> persons = new HashMap<String,String>();
                persons.put(TAG_Machine_code,Machine_code);
                personList.add(persons);*/
            }
            Log.i("insertToDatabase", "User_id = " + User_id);
            Log.i("insertToDatabase", "eamil = " + email);
            if (Machine_code.equals("")) {
                Log.i("insertToDatabase", "Machine_code = " + Machine_code);
                Toast.makeText(mainpage.this, "존재하지않는 기기번호 입니다.", Toast.LENGTH_SHORT).show();
            } else if (!User_id.equals("null") && !User_id.equals(email)) { //유저정보가 있고, 그 유저정보가 로그인된유저정보랑 같지 않을때
                Toast.makeText(mainpage.this, "이미등록된 기기번호 입니다.", Toast.LENGTH_SHORT).show();
            } else {
                Shared_Machine_code = Machine_code; //SharedPreferences에 DB Machine_code를 저장
                Menu menu = navigationView.getMenu();
                MenuItem itemstate = menu.findItem(R.id.nav_registration);
                itemstate.setTitle(Machine_Info); //네비게이션 드로어 메뉴이름 변경
                save("기기정보");
                load();

                DB_Manager = new DatabaseManager("update_machine2.php", "user_id", "Machine_code");
                DB_Manager.execute(email, Machine_savecode);
                viewPager.setAdapter(pagerAdapter); //각 탭에 대한 리스트뷰
                Toast.makeText(mainpage.this, "기기정보가 등록되었습니다.", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            Log.i("insertToDatabase","에러");
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
                loading = ProgressDialog.show(mainpage.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
                loading.dismiss();
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
//                    Toast.makeText(mainpage.this,"에러",Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
// 설정값을 저장하는 함수
    public void save(String M_INFO) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        Log.i("Thread호출","2");
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움

        editor.putString("MACHINE_INFO", M_INFO);
        if(M_INFO != "기기등록") {
            editor.putString("MACHINE_CODE", Shared_Machine_code);
            Shared_registration_state = true;
            editor.putBoolean("REGISTRATION_STATE",Shared_registration_state);
            try {
                FileOutputStream fos = this.openFileOutput("Machine_savecode.txt", // 파일명 지정
                        Context.MODE_PRIVATE);// 저장모드
                PrintWriter out = new PrintWriter(fos);
                out.println(Shared_Machine_code);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            editor.putString("MACHINE_CODE", "");
            Shared_registration_state = false;
            editor.putBoolean("REGISTRATION_STATE",Shared_registration_state);
            try {
                FileOutputStream fos = this.openFileOutput("Machine_savecode.txt", // 파일명 지정
                        Context.MODE_PRIVATE);// 저장모드
                PrintWriter out = new PrintWriter(fos);
                out.println("");
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }
    // 설정값을 불러오는 함수
    public static void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        icon = appData.getString("ICON","");
        Machine_Info = appData.getString("MACHINE_INFO", "");
        Machine_savecode = appData.getString("MACHINE_CODE","");
        Shared_registration_state = appData.getBoolean("REGISTRATION_STATE",false);
    }
}
