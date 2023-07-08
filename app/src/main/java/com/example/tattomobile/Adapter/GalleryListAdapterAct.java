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
import com.example.tattomobile.model.OurWorkListMode;
import com.example.tattomobile.model.Response_Gallery;
import com.example.tattomobile.model.Response_list;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GalleryListAdapterAct extends RecyclerView.Adapter<GalleryListAdapterAct.TestimonialsViewHolder> {
    private Context context;
    private OurWorkListMode serviceModelList;
    int showDataCount;

    public GalleryListAdapterAct(Context context, OurWorkListMode serviceModelList, int showDataCount) {
        this.context = context;
        this.serviceModelList = serviceModelList;
        this.showDataCount = showDataCount;
    }

    @NonNull
    @Override
    public GalleryListAdapterAct.TestimonialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testimonials_row, parent, false);
        return new GalleryListAdapterAct.TestimonialsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryListAdapterAct.TestimonialsViewHolder holder, int position) {
        Response_list response_list = serviceModelList.getResponse().get(position);
        Picasso.get()
                .load(response_list.getImgSrc())
                .into(holder.ivThumbnail);
//        holder.itemView.setOnClickListener(view -> {
//            if (videoModel!=null && videoModel.getVideoLink()!=null)
//            watchYoutubeVideo(getYouTubeId(videoModel.getVideoLink()));
//        });
        holder.tvplaybutton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return serviceModelList.getResponse().size();

    }

    public class TestimonialsViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivThumbnail, tvplaybutton;

        public TestimonialsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvplaybutton = itemView.findViewById(R.id.playbutton);
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

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "error";
        }
    }
}
