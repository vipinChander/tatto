package com.example.tattomobile.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tattomobile.R;
import com.example.tattomobile.activity.DetailActivity;
import com.example.tattomobile.model.ServiceModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ServiceViewHolder> {

    private Context context;
    private List<ServiceModel> serviceModelList;
    private Activity activity;

    public ServiceListAdapter(Context context, List<ServiceModel> serviceModelList) {
        this.context = context;
        this.serviceModelList = serviceModelList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_row_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceModel serviceModel = serviceModelList.get(position);
        Picasso.get()
                .load(serviceModel.getServiceIcon())
                .into(holder.ivThumbnail);
        holder.tvTitle.setText(serviceModel.getServiceName());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("service_id", serviceModel.getServiceId());
            intent.putExtra("product_name", serviceModel.getServiceName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public ImageView ivThumbnail;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }
    }
}
