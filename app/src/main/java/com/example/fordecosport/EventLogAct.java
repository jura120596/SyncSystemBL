package com.example.fordecosport;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fordecosport.domain.Event;
import com.example.fordecosport.domain.rest.LibApiVolley;
import com.example.fordecosport.sqlite.DataBaseHelper;
import com.example.fordecosport.adapter.EventListAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventLogAct extends AppCompatActivity {
    public static final int perPage = 50;
    private MenuItem item;
    private static int size = perPage, scrollcount = 0;
    private RecyclerView recyclerView;
    private DataBaseHelper dataBaseHelper;
    private static List<Event> eventList = new ArrayList<>();
    private EventListAdapter customAdapter;
    private boolean hasEvents = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_log);

        init();
        customAdapter = new EventListAdapter(EventLogAct.this, eventList);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(EventLogAct.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (hasEvents) {
                        if (scrollcount++ > 0) {
                            size += perPage;
                            addNextEvents();
                            scrollcount = 0;
                            return;
                        }
                        Toast.makeText(EventLogAct.this, "Потяни ещё раз для добавления", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventLogAct.this, "Больше данных нет", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateEvents() {
        customAdapter.notifyDataSetChanged();
    }

    private void init() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        dataBaseHelper = new DataBaseHelper(EventLogAct.this);
        recyclerView = findViewById(R.id.recycler);

    }


    @Override
    protected void onStart() {
        super.onStart();
        eventList.clear();
        addNextEvents();
        new LibApiVolley(this).fillEvent();
        customAdapter.notifyDataSetChanged();
    }

    private void addNextEvents() {
        int current = eventList.size();
        int all = NoDb.ALL_EVENT_LIST.size();
        for (int i = current; i < Math.min(current + perPage - 1, all - 1); i++) {
            eventList.add(NoDb.ALL_EVENT_LIST.get(all - i - 1));
        }
        updateEvents();
    }


}

