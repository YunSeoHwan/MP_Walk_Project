package com.example.afinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TimeBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) { // 날짜가 바뀌면 해당구문 출력
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            Toast.makeText(context, "오늘하루 고생하셨습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}