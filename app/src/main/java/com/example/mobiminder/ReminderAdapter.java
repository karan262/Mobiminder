package com.example.mobiminder;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private Context context;
    private String t_place[],t_description[],lat[],log[];

    public ReminderAdapter( Context context,String t_place[],String t_description[],String lat[],String log[]) {

        this.context = context;
        this.t_place=t_place;
        this.t_description=t_description;
        this.lat=lat;
        this.log=log;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_reminder, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ReminderAdapter.ViewHolder holder, final int position) {
        holder.place.setText(holder.place.getText()+t_place[position]);
        holder.desc.setText(holder.desc.getText()+t_description[position]);
        holder.lat.setText(holder.lat.getText()+lat[position]);
        holder.log.setText(holder.log.getText()+log[position]);
//        holder.textViewHead.setText(listItem.getHead());
//        holder.textViewDesc.setText(listItem.getDesc());

    }

    @Override
    public int getItemCount() {
//        return listItems.size();
        return  t_place.length;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView place;
        public TextView desc;
        public TextView lat;
        public TextView log;



        public ViewHolder(View itemView) {
            super(itemView);
            place = (TextView) itemView.findViewById(R.id.Place_List);
            desc = (TextView) itemView.findViewById(R.id.DescReminder_List);
            lat = (TextView) itemView.findViewById(R.id.lat_List);
            log = (TextView) itemView.findViewById(R.id.long_List);
        }
    }
}