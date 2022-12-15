package com.example.afinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// 근처 사용자 Adapter
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
        implements OnUserItemClickListener{
    ArrayList<User> items = new ArrayList<User>();  // ArrayList 생성

    OnUserItemClickListener listener;       // linstener 변수 생성

    @NonNull
    @Override   // custom_user xml 이용하여 LayoutInflater
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.custom_user, viewGroup, false);

        return new UserAdapter.ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder viewHolder, int position) {
        User item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }   // 아이템 크기 받기

    public void addItem(User item) {
        items.add(item);
    }   // 아이템 더하기

    public void setItems(ArrayList<User> items) {
        this.items = items;
    }

    public User getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, User item) {
        items.set(position, item);
    }

    @Override
    public void onItemClick(UserAdapter.ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {   // ListView에 표시시
       TextView textView, textView3;

        public ViewHolder(View itemView, final OnUserItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_hello);
            textView3 = itemView.findViewById(R.id.textView3);

        }

        public void setItem(User item) {
            textView.setText(item.getName());
            textView3.setText(Double.toString(item.getKm()) + "km");
        }

    }
}
