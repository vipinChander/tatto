package com.example.tattomobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tattomobile.R;
import com.example.tattomobile.activity.BookingDetailActivity;
import com.example.tattomobile.model.Response_b;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MyBookListAdapter extends RecyclerView.Adapter<MyBookListAdapter.MyBookListViewHolder> {

    private Context context;
    private List<Response_b> productList;

    public MyBookListAdapter(Context context, List<Response_b> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyBookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_booking_row, parent, false);
        return new MyBookListViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyBookListViewHolder holder, int position) {

        Response_b response_b = productList.get(position);
        try {
            if (!response_b.getProduct().getProductImg().equals("")) {
                Picasso.get()
                        .load(response_b.getProduct().getProductImg())
                        .into(holder.ivThumbnail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            holder.tvTitle.setText(response_b.getProduct().getProductName());
            holder.tvDateTime.setText("Date & Time:" + response_b.getBookingDate() + "," + response_b.getSlot().getSlotTime());

            holder.tvId.setText("ID: " + response_b.getPaymentId());

            if (!(response_b.getSlot().getSlotStatus() == 1)) {
                holder.btn.setBackgroundColor(context.getColor(R.color.yellow_color));
                holder.btn.setTextColor(context.getColor(R.color.white));
                holder.btn.setText("Pending");
            }
            holder.layoutMain.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("response", response_b);
                Intent intent = new Intent(context, BookingDetailActivity.class);
                intent.putExtra("value", bundle);
                context.startActivity(intent);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyBookListViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivThumbnail;
        public LinearLayout layoutMain;
        public TextView tvTitle, tvDateTime, tvId;
        AppCompatButton btn;

        public MyBookListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            layoutMain = itemView.findViewById(R.id.mainlayout);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvId = itemView.findViewById(R.id.tv_id);
            tvDateTime = itemView.findViewById(R.id.tv_date_and_time);
            btn = itemView.findViewById(R.id.btn_booking_status_done);
        }
    }
}
