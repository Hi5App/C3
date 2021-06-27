package com.penglab.hi5.core.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.penglab.hi5.R;

import java.util.List;

public class RewardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);

        VerticalSeekBar verticalSeekBar = findViewById(R.id.reward_seekbar);
        Score scoreInstance = Score.getInstance();
        int score = scoreInstance.getScore();

        if (score > 300){
            verticalSeekBar.setProgress(300);
        } else {
            verticalSeekBar.setProgress(score);
        }



        ImageButton imageButton2 = findViewById(R.id.reward_button_2);
        ImageButton imageButton1 = findViewById(R.id.reward_button_1);
        ImageView check2 = findViewById(R.id.reward_button2_check);
        ImageView check1 = findViewById(R.id.reward_button1_check);

        RewardLitePalConnector rewardLitePalConnector = RewardLitePalConnector.getInstance();
        List<Integer> rewards = rewardLitePalConnector.getRewards();

        if (rewards.get(0) == 1){
            imageButton1.setEnabled(false);
            check1.setVisibility(View.VISIBLE);
        } else if (score > 100){
            imageButton1.setEnabled(true);
        } else {
            imageButton1.setEnabled(false);
        }

        if (rewards.get(1) == 1){
            imageButton2.setEnabled(false);
            check2.setVisibility(View.VISIBLE);
        } else if (score > 200){
            imageButton2.setEnabled(true);
        } else {
            imageButton2.setEnabled(false);
        }

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton2.setEnabled(false);
                check2.setVisibility(View.VISIBLE);
                RewardLitePalConnector rewardLitePalConnector = RewardLitePalConnector.getInstance();
                rewardLitePalConnector.updateRewards(1, 1);
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton1.setEnabled(false);
                check1.setVisibility(View.VISIBLE);
                RewardLitePalConnector rewardLitePalConnector = RewardLitePalConnector.getInstance();
                rewardLitePalConnector.updateRewards(0, 1);
            }
        });
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