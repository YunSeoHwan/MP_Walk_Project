package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WalkInfoActivity extends AppCompatActivity {

    Button btn_write;

    RecyclerView recyclerView;
    CustomerAdapter adapter;
    private FirebaseAuth mFirebaseAuth;     // firebase 인증
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_info);

        // 변수 초기화
        btn_write = findViewById(R.id.btn_write);
        recyclerView = findViewById(R.id.recyclerView);
        getSupportActionBar().setTitle("산책 Tip");
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // DB 객체 생성
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("project"); // DB 연결

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomerAdapter();

        // 산책 tip DB접근
        mDatabaseRef.child("tip").addChildEventListener(new ChildEventListener() {
            @Override

            // DB 값 받아오기
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Customer customer = snapshot.getValue(Customer.class);
                String name = customer.getName();
                String tip = customer.getTip();
                adapter.addItem(new Customer(tip, name, R.drawable.user));
                recyclerView.setAdapter(adapter);
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

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editText = new EditText(WalkInfoActivity.this);
                AlertDialog.Builder dig = new AlertDialog.Builder(WalkInfoActivity.this);
                dig.setTitle("산책 Tip");
                dig.setView(editText);
                dig.setPositiveButton("확인", new DialogInterface.OnClickListener() { // 대화상자 생성
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {   // DB접근하여 값 저장
                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.getValue(String.class);
                                Customer customer = new Customer(editText.getText().toString(), name);
                                customer.setName(name);
                                customer.setTip(editText.getText().toString());
                                mDatabaseRef.child("tip").push().setValue(customer);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                dig.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                dig.show();
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
                Intent intent1 = new Intent(WalkInfoActivity.this, MainActivity.class);
                startActivity(intent1);
                break;

            case R.id.tv_name:
                Intent intent3 = new Intent(WalkInfoActivity.this, UserActivity.class);
                startActivity(intent3);
                break;

            case R.id.walk_info:
                Toast.makeText(getApplicationContext(), "현재 화면입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.walk_record:
                Intent intent2 = new Intent(WalkInfoActivity.this, WalkRecordActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }
}