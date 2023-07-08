package com.example.tattomobile.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tattomobile.R;
import com.example.tattomobile.model.Datum;
import com.example.tattomobile.model.GalleryDataModel;
import com.example.tattomobile.model.Response_Gallery;
import com.example.tattomobile.model.VideoModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestimonialsListAdapter extends RecyclerView.Adapter<TestimonialsListAdapter.TestimonialsViewHolder> {

    private Context context;
    private List<VideoModel> serviceModelList;
    int showDAta=0;

    public TestimonialsListAdapter(Context context, List<VideoModel> serviceModelList,int showDAta) {
        this.context = context;
        this.serviceModelList = serviceModelList;
        this.showDAta=showDAta;
    }

    @NonNull
    @Override
    public TestimonialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testimonials_row, parent, false);
        return new TestimonialsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestimonialsViewHolder holder, int position) {
        Picasso.get()
                .load(serviceModelList.get(position).getVideoImg())
                .placeholder(R.drawable.app_logo)
                .into(holder.ivThumbnail);
        holder.itemView.setOnClickListener(view -> {
            if (serviceModelList.get(position).getVideoLink()!=null && serviceModelList.get(position).getVideoLink()!=null)
                watchYoutubeVideo(getYouTubeId(serviceModelList.get(position).getVideoLink()));
        });


    }

    @Override
    public int getItemCount() {
        return  serviceModelList.size();
    }

    public class TestimonialsViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivThumbnail,tvplaybutton;

        public TestimonialsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);

        }
    }

    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    private String getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }}
