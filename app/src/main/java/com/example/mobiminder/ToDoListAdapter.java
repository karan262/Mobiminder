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

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {

    private Context context;
    private String t_title[],t_description[],t_date[];

    public ToDoListAdapter( Context context,String t_title[],String t_description[],String t_date[]) {

        this.context = context;
        this.t_title=t_title;
        this.t_description=t_description;
        this.t_date=t_date;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_mytodo_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ToDoListAdapter.ViewHolder holder, final int position) {
        holder.title.setText(holder.title.getText()+t_title[position]);
        holder.desc.setText(holder.desc.getText()+t_description[position]);
        holder.date.setText(holder.date.getText()+t_date[position]);
        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyToDoActivity)context).EditToDoList(position);
            }
        });
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyToDoActivity)context).DeleteToDoList(position);


            }
        });
//        holder.textViewHead.setText(listItem.getHead());
//        holder.textViewDesc.setText(listItem.getDesc());

    }

    @Override
    public int getItemCount() {
//        return listItems.size();
        return  t_title.length;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView desc;
        public TextView date;
        public Button Edit;
        public Button Delete;


        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.Title_List);
            desc = (TextView) itemView.findViewById(R.id.Desc_List);
            date = (TextView) itemView.findViewById(R.id.Date_List);
            Edit = (Button) itemView.findViewById(R.id.Edit_Todo);
            Delete = (Button) itemView.findViewById(R.id.Delete_Todo);
        }
    }
}