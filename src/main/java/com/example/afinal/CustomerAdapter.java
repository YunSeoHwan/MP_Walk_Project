package com.example.afinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Adapter를 이용한 RecyclerView Class
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder>
        implements OnCustomerItemClickListener {
    ArrayList<Customer> items = new ArrayList<Customer>();

    OnCustomerItemClickListener listener;

    // custom_item 디자인 불러옴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.custom_item, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Customer item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Customer item) {
        items.add(item);
    }

    public void setItems(ArrayList<Customer> items) {
        this.items = items;
    }

    public Customer getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Customer item) {
        items.set(position, item);
    }

    // 클릭했을 때, 값을 넘겨줌
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    // 입력받은 내용을 ListView로 만들어줌
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView3;
        ImageView imageView;

        public ViewHolder(View itemView, final OnCustomerItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_hello);
            textView3 = itemView.findViewById(R.id.textView3);
            imageView = itemView.findViewById(R.id.imageView);

        }

        public void setItem(Customer item) {
            textView.setText(item.getTip());
            textView3.setText(item.getName());
            imageView.setImageResource(item.getResId());
        }

    }

}
