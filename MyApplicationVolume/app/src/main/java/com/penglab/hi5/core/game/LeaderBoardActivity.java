package com.penglab.hi5.core.game;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.penglab.hi5.R;
import com.penglab.hi5.core.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private LeaderBoardItem[] leaderBoardItems = {
            new LeaderBoardItem("xfffff", "邢飞", 1000),
            new LeaderBoardItem("zyh", "朱一行", 800),
            new LeaderBoardItem("zx", "赵轩", 600)
    };

    private List<LeaderBoardItem> leaderBoardItemList = new ArrayList<>();

    private LeaderBoardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initLeaderBoardItems();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.leaderboard_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LeaderBoardAdapter(leaderBoardItemList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back:
                finish();
                return true;
            default:
                return true;
        }
    }

    private void initLeaderBoardItems(){
        LeaderBoardContainer leaderBoardContainer = LeaderBoardContainer.getInstance();
        leaderBoardItemList = leaderBoardContainer.getLeaderBoardItems();
//        for (int i = 0; i < leaderBoardItems.length; i++){
//            leaderBoardItemList.add(leaderBoardItems[i]);
//        }
    }
}