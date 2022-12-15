package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private FirebaseAuth mFirebaseAuth;     // firebase 인증
    private DatabaseReference mDatabaseRef; // 실시간 DB
    private Sensor stepCountSensor;         // 걸음수 센서와 관리자 선언
    private SensorManager sensorManager;
    Button btn_check, btn_weather;
    TextView t_walk, t_km, t_kcal, tv_city, tv_weather, tv_temp, tv_hello;
    int currentStep, kcal = 0;              // 걸음수, km, kcal 변수
    double km = 0.0;
    static RequestQueue requestQueue;       // request Queue에 저장
    private BroadcastReceiver mReceiver;    // 방송수신자

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t_walk = findViewById(R.id.t_walk);
        t_kcal = findViewById(R.id.t_kcal);
        t_km = findViewById(R.id.t_km);
        tv_hello = findViewById(R.id.tv_hello);
        btn_check = findViewById(R.id.btn_check);
        tv_city = findViewById(R.id.tv_city);
        tv_weather = findViewById(R.id.tv_weather);
        tv_temp = findViewById(R.id.tv_temp);
        btn_weather = findViewById(R.id.btn_weather);
        mFirebaseAuth = FirebaseAuth.getInstance();         // DB 시작 위치
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("project");  // DB project에 접근

        mReceiver = new TimeBroadcast();        // 방송 수신자 객체 생성
        getSupportActionBar().setTitle("메인 메뉴");

        // 사용자 이름 불러오기
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();     // 현재 DB접근
        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                tv_hello.setText(name + "님 안녕하세요");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 위치 서비스 생성
        if (Build.VERSION.SDK_INT >= 23 &&  // 버전 확인후, 사용자에게 권한 요청
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {  // GPS_PROVIDER로 location 생성
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) { // 현재 값 존재 시

                // 해당 DB 위치 접근
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { // 차례로 data값 받아옴
                        String name = snapshot.getValue(String.class);
                        double latitude = location.getLatitude();       // 사용자 현재 위도, 경도 받아옴
                        double longitude = location.getLongitude();

                        UserLocation userLocation = new UserLocation();
                        userLocation.setLatitude(latitude);             // DB에 저장
                        userLocation.setLongitude(longitude);
                        userLocation.setName(name);
                        mDatabaseRef.child("UserLocation").push().setValue(userLocation); // push로 각각을 구별하여 input

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        btn_weather.setOnClickListener(new View.OnClickListener() { // 날씨 불러오기
            @Override
            public void onClick(View view) {
                CurrentCall();
            }
        });

        //volley를 쓸 때 큐가 비어있으면 새로운 큐 생성하기
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        // 활동 퍼미션 체크
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // 걸음 센서 연결
        // * 옵션
        // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
        // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }

        btn_check.setOnClickListener(new View.OnClickListener() {    // 해당 값 저장
            @Override
            public void onClick(View view) {
                if (btn_check.getText().equals("저장하기")) {    // 저장버튼 눌렀을때
                    btn_check.setText("시작하기");
                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                    long now = System.currentTimeMillis();      // 현재 시간 불러옴
                    Date mDate = new Date(now);
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd"); // 다음 형식으로 값 불러옴
                    String getTime = simpleDate.format(mDate);

                    // DB에 저장
                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("WalkDate").setValue(getTime);

                    // 현재 산책관련 정보를 받아오고 DB에 저장
                    UserWalk userWalk = new UserWalk(currentStep, km, kcal);
                    userWalk.setWalk(currentStep);
                    userWalk.setKm(km);
                    userWalk.setKcal(kcal);
                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("UserWalk").setValue(userWalk);
                    currentStep = 0;
                    km = 0;
                    kcal = 0;
                    t_walk.setText(String.valueOf(currentStep));
                    t_km.setText(String.valueOf(km));
                    t_kcal.setText(String.valueOf(kcal));
                    Toast.makeText(getApplicationContext(), "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    btn_check.setText("저장하기");
                    Toast.makeText(getApplicationContext(), "산책을 시작합니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 브로드캐스트 리시버
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);   // 하루가 지났을때 시작
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void CurrentCall() {    // 날씨정보 불러오기
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 &&  // 위치 권한확인
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double latitude = location.getLatitude();       // 사용자 위도, 경도 받아옴
            double longitude = location.getLongitude();

            // openweatherAPI를 이용하여 위도, 경도 저장
            String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=ed86888532d032a872a0c14addf6c12f", latitude, longitude);

            // 요청받은 값 JSON 형태로 받음
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(String response) {

                    try {
                        //api로 받은 파일 jsonobject로 새로운 객체 선언
                        JSONObject jsonObject = new JSONObject(response);

                        //도시 키값 받기
                        String city = jsonObject.getString("name");
                        tv_city.setText("현재위치 : " + city);
                        tv_city.setVisibility(View.VISIBLE);

                        //날씨 키값 받기
                        JSONArray weatherJson = jsonObject.getJSONArray("weather");
                        JSONObject weatherObj = weatherJson.getJSONObject(0);

                        String weather = weatherObj.getString("description");
                        tv_weather.setText("오늘의 날씨 : " + weather);
                        tv_weather.setVisibility(View.VISIBLE);

                        //기온 키값 받기
                        JSONObject tempK = new JSONObject(jsonObject.getString("main"));

                        //기온 받고 켈빈 온도를 섭씨 온도로 변경
                        double tempDo = (Math.round((tempK.getDouble("temp") - 273.15) * 100) / 100.0);
                        tv_temp.setText("온도 : " + tempDo + "°C");
                        tv_temp.setVisibility(View.VISIBLE);

                    } catch (JSONException e) { // 예외처리
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };

            // Queue에 삽입
            request.setShouldCache(false);
            requestQueue.add(request);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (stepCountSensor != null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {    // 산책 시작시에만 정보기록
        if (btn_check.getText().equals("저장하기")) {
            // 걸음 센서 이벤트 발생시
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

                if (event.values[0] == 1.0f) {
                    // 센서 이벤트가 발생할때 마다 걸음수 증가
                    currentStep++;
                    t_walk.setText(String.valueOf(currentStep));
                    if (currentStep % 13 == 0) {
                        km += 0.01;
                        t_km.setText(Double.toString(km));
                    }
                    if (currentStep % 16 == 0) {
                        kcal++;
                        t_kcal.setText(String.valueOf(kcal));
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {  // menu와 intent

        switch (item.getItemId()) {
            case R.id.main:
                Toast.makeText(getApplicationContext(), "현재 화면입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_name:
                Intent intent1 = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent1);
                break;

            case R.id.walk_info:
                Intent intent2 = new Intent(MainActivity.this, WalkInfoActivity.class);
                startActivity(intent2);
                break;

            case R.id.walk_record:
                Intent intent3 = new Intent(MainActivity.this, WalkRecordActivity.class);
                startActivity(intent3);
                break;
        }
        return true;
    }
}
