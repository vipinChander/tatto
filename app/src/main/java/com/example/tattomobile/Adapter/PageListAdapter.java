package com.example.tattomobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tattomobile.Home.HomeActivity;
import com.example.tattomobile.R;
import com.example.tattomobile.activity.CalenderActivity;
import com.example.tattomobile.activity.MyBookingActivity;
import com.example.tattomobile.model.PageDataModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.PageViewHolder> {

    private Context context;
    private List<PageDataModel> pageDataModelList;
    private RecyclerView recyclerView;
    private boolean result;

    public PageListAdapter(Context context, List<PageDataModel> pageDataModelList,boolean result) {
        this.context = context;
        this.pageDataModelList = pageDataModelList;
        this.result=result;
    }

    public PageListAdapter(Context context, List<PageDataModel> pageDataModelList, RecyclerView recyclerView,boolean result) {
        this.context = context;
        this.pageDataModelList = pageDataModelList;
        this.recyclerView = recyclerView;
        this.result=result;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_row, parent, false);
        if (pageDataModelList.size() > 1 && recyclerView != null) {
            int width = recyclerView.getWidth();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (width * 0.9);
            view.setLayoutParams(params);
        }
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        PageDataModel pageDataModel = pageDataModelList.get(position);
        holder.tvDesignName.setText(pageDataModel.getProductName());
        Picasso.get()
                .load(pageDataModel.getProductImg())
                .into(holder.ivThumbnail);
        holder.tvOfferPrice.setText("Offer Price: " + pageDataModel.getOfferAmount() +"/-");
        holder.tvPrice.setText("Price: " + pageDataModel.getProductAmount() +"/-");
        if (result) {
            holder.tvdescription.setText(pageDataModel.getProductDescription());
        }else {
            holder.tvdescription.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            Bundle bundle=new Bundle();
            bundle.putSerializable("response",pageDataModel);
            Intent intent=new Intent(context, CalenderActivity.class);
            intent.putExtra("data",bundle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pageDataModelList.size();
    }

    public class PageViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivThumbnail;
        public LinearLayout llBook;
        public TextView tvDesignName, tvOfferPrice, tvPrice,tvdescription;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvDesignName = itemView.findViewById(R.id.tv_design_name);
            tvOfferPrice = itemView.findViewById(R.id.tv_offer_price);
            tvPrice = itemView.findViewById(R.id.tv_price);
            llBook = itemView.findViewById(R.id.ll_book);
            tvdescription=itemView.findViewById(R.id.tv_description_name);
        }
    }
}
