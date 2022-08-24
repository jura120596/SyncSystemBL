package com.example.fordecosport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fordecosport.R;
import com.example.fordecosport.domain.Event;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {
    private Context context;
    private List<Event> list;
    public EventListAdapter(Context context, List<Event> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_log_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Event event = list.get(position);
        holder.id.setText(String.valueOf(position+1));
        holder.time.setText(String.valueOf(event.getTime()));
        switch (event.getEvent()) {
            case "0":
                holder.event.setText("Send A");
                break;
            case "1":
                holder.event.setText("Send B");
                break;
            case "2":
                holder.event.setText("Send C");
                break;
            case "3":
                holder.event.setText("Send D");
                break;
            case "4":
                holder.event.setText("Successful login");
                break;
            case "5":
                holder.event.setText("Unsuccessful login");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, time, event;

        public MyViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.idTv);
            event = view.findViewById(R.id.eventTV);
            time = view.findViewById(R.id.timeTV);
        }
    }
}
