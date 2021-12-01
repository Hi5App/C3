package com.penglab.hi5.core;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.penglab.hi5.R;
import com.penglab.hi5.chat.ChatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * on top bar menu created, link res/menu/main.xml
     * @param menu menu layout
     * @return if create Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    /**
     * call the corresponding function when button in top bar clicked
     * @param item item clicked
     * @return if handle the event
     */
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back:
                finish();
            default:
                return true;
        }
    }

    public static void start(Context context){
        Intent intent = new Intent(context, HelpActivity.class);
        context.startActivity(intent);
    }
}