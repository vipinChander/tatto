package com.example.tattomobile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tattomobile.R;
import com.example.tattomobile.model.Slot;
import com.example.tattomobile.model.TimeSave;
import com.example.tattomobile.model.TimesDataModel;

import java.util.List;

public class TimeshowAdapter extends RecyclerView.Adapter<TimeshowAdapter.ViewHolderTimes> {
    private Context context;
    private List<Slot> slotList;
    private int row_index=-1;
    private TimeSave timeSave;


    public TimeshowAdapter(Context context, List<Slot> slotList) {
        this.context = context;
        this.slotList = slotList;
    }

    @NonNull
    @Override
    public ViewHolderTimes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.times_show_design, parent, false);
        return new ViewHolderTimes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTimes holder, @SuppressLint("RecyclerView") int position) {
       holder.Dateshowtext.setText(slotList.get(position).getSlotTime());
        if(row_index==position){
            holder.Dateshowtext.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.roundyellowshape) );;
            holder.Dateshowtext.setTextColor(Color.parseColor("#000000"));
        }
        else
        {
            holder.Dateshowtext.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_bg_edittext) );;
            holder.Dateshowtext.setTextColor(Color.parseColor("#fad02c"));
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=position;
                notifyDataSetChanged();
                timeSave=TimeSave.getInstance();
                timeSave.setSlot_time(slotList.get(position).getSlotTime());
                timeSave.setPositios(String.valueOf(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    class ViewHolderTimes extends RecyclerView.ViewHolder {
        private TextView Dateshowtext;
        RelativeLayout relativeLayout;

        public ViewHolderTimes(@NonNull View itemView) {
            super(itemView);
            Dateshowtext = itemView.findViewById(R.id.times_text);
            relativeLayout = itemView.findViewById(R.id.mainLayoutTimes);
        }
    }
}
