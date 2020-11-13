package com.need.mymall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;

class NotificationAdater extends RecyclerView.Adapter<NotificationAdater.ViewHolder> {

    private ArrayList<NotificationModel> notificationModelArrayList;

    public NotificationAdater(ArrayList<NotificationModel> notificationModelArrayList) {
        this.notificationModelArrayList = notificationModelArrayList;
    }

    @NonNull
    @Override
    public NotificationAdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdater.ViewHolder holder, int position) {
        String title = notificationModelArrayList.get(position).getTitle();
        String body = notificationModelArrayList.get(position).getBody();
        Date date = notificationModelArrayList.get(position).getDate();
        boolean readed = notificationModelArrayList.get(position).isReaded();

        ((ViewHolder)holder).setData(title,body,date.toString(),readed);
    }

    @Override
    public int getItemCount() {
        return notificationModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView body;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notification_title);
            body = itemView.findViewById(R.id.notification_body);
            date = itemView.findViewById(R.id.notification_date);
        }

        public void setData (String title,String body,String date,boolean readed) {
            this.title.setText(title);
            this.body.setText(body);
            this.date.setText(date);
        }
    }
}
