package com.penglab.hi5.core.game.leaderBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private LeaderBoardAdapter adapter;

    private LeaderBoardViewModel leaderBoardViewModel;

    private TabLayout tabLayout;

    private String [] titles = {"today","universal"};
    private int images[] = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        leaderBoardViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(LeaderBoardViewModel.class);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.leaderboard_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LeaderBoardAdapter(leaderBoardViewModel.getLeaderBoardItemList());
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
}