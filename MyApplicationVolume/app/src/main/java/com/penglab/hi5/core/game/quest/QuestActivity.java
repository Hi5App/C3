package com.penglab.hi5.core.game.quest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.ViewModelFactory;

import java.util.ArrayList;

public class QuestActivity extends AppCompatActivity {

    private String TAG = "QuestActivity";

//    private Quest[] quests = {
//            new Quest("Login", 1, 1, 1, 20),
//            new Quest("Draw a line", 0, 1, 20),
//            new Quest("Draw 100 lines", 0, 100, 100),
//            new Quest("Draw a marker", 0, 1, 20),
//            new Quest("Draw 100 markers", 0, 100, 100),
//    };

    private QuestBindingAdapter questBindingAdapter;
    private QuestViewModel questViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(QuestViewModel.class);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.quest_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initAdapter();
        recyclerView.setAdapter(questBindingAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));
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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    private void initAdapter() {
        questBindingAdapter = new QuestBindingAdapter(this, questViewModel);
        ArrayList<Quest> dailyQuests = questViewModel.getDailyQuestsModel().getDailyQuests();
        for (int i = 0; i < dailyQuests.size(); i++) {
            questBindingAdapter.getItems().add(dailyQuests.get(i));
        }
    }
}