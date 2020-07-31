package com.example.myapplication__volume;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    /**
     * call the corresponding function when button in top bar clicked
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back:
//                try {
//                    Intent intent = new Intent(HelpActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                finish();
            default:
                return true;
//                return super.onOptionsItemSelected(item);
        }
    }
}