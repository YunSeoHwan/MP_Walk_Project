package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {


    RecyclerView recyclerView;              // RecyclerView와 adapter생성
    UserAdapter adapter;
    private FirebaseAuth mFirebaseAuth;     // firebase 인증
    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.recyclerView);     // recyclerView 할당
        getSupportActionBar().setTitle("근처 사용자");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("project"); // DB 연결

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);      // layout 생성
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserAdapter();

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {

            mDatabaseRef.child("UserLocation").addChildEventListener(new ChildEventListener() {
                @Override
                // DB 값 받아오기
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    UserLocation userLocation = snapshot.getValue(UserLocation.class);
                    double latitude = location.getLatitude();       // 사용자 위도, 경도
                    double longitude = location.getLongitude();
                    double dectLatitude = userLocation.getLatitude();   // DB저장된 상대방 위도, 경도
                    double dectLongitude = userLocation.getLongitude();
                    String name = userLocation.getName();       // 사용자 이름

                    Location myLocation = new Location("");     // 비교할 Location 생성
                    Location dectLocation = new Location("");

                    myLocation.setLatitude(latitude);
                    myLocation.setLongitude(longitude);
                    dectLocation.setLatitude(dectLatitude);
                    dectLocation.setLongitude(dectLongitude);

                    double dist = myLocation.distanceTo(dectLocation);  // 상대방과 나의 거리 구함
                    int nDist = (int) (dist);
                    double d = nDist / 1000;

                    // 자기 자신과의 위치는 제외
                    if (d > 0) {
                        recyclerView.setAdapter(adapter);
                        adapter.addItem(new User(name, d));
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main:
                Intent intent1 = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent1);
                break;

            case R.id.tv_name:
                Toast.makeText(getApplicationContext(), "현재 화면입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.walk_info:
                Intent intent2 = new Intent(UserActivity.this, WalkInfoActivity.class);
                startActivity(intent2);
                break;

            case R.id.walk_record:
                Intent intent3 = new Intent(UserActivity.this, WalkRecordActivity.class);
                startActivity(intent3);
                break;
        }
        return true;
    }
}