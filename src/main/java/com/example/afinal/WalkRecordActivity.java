package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalkRecordActivity extends AppCompatActivity {

    public CalendarView calendarView;
    public TextView tv_walk, tv_km, tv_kcal, t_record;
    int walk, kcal;
    double km;
    private FirebaseAuth mFirebaseAuth;     // firebase 인증
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_record);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // DB 객체 생성
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("project"); // DB 연결

        getSupportActionBar().setTitle("산책기록");
        calendarView = findViewById(R.id.calendarView);
        tv_walk = findViewById(R.id.tv_walk);
        tv_km = findViewById(R.id.tv_km);
        tv_kcal = findViewById(R.id.tv_kcal);
        t_record = findViewById(R.id.t_record);

        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("UserWalk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserWalk userWalk = snapshot.getValue(UserWalk.class);
                walk = userWalk.getWalk();
                km = userWalk.getKm();
                kcal = userWalk.getKcal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                t_record.setText(name + "님의 산책기록");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 캘린더 날짜 눌렀을때 이벤트처리
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("WalkDate").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String date = snapshot.getValue(String.class);
                        // DB 저장 날짜
                        String y = date.substring(0, 4);
                        String m = date.substring(4, 6);
                        String d = date.substring(6);

                        // 선택한 날짜
                        int yy = Integer.parseInt(y);
                        int mm = Integer.parseInt(m);
                        int dd = Integer.parseInt(d);

                        // 선택 날짜 정수로 변환
                        int select_y = year;
                        int select_m = month + 1;
                        int select_d = dayOfMonth;

                        // 선택날짜에 산책정보 존재하면 값 불러옴
                        if (yy == select_y && mm == select_m && dd == select_d) {
                            tv_walk.setText("걸음수 : " + Integer.toString(walk));
                            tv_km.setText("Km : " + Double.toString(km));
                            tv_kcal.setText("Kcal : " + Integer.toString(kcal));
                        } else {
                            tv_walk.setText("");
                            tv_km.setText("");
                            tv_kcal.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "에러", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
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
                Intent intent1 = new Intent(WalkRecordActivity.this, MainActivity.class);
                startActivity(intent1);
                break;

            case R.id.tv_name:
                Intent intent3 = new Intent(WalkRecordActivity.this, UserActivity.class);
                startActivity(intent3);
                break;

            case R.id.walk_info:
                Intent intent2 = new Intent(WalkRecordActivity.this, WalkInfoActivity.class);
                startActivity(intent2);
                break;

            case R.id.walk_record:
                Toast.makeText(getApplicationContext(), "현재 화면입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}