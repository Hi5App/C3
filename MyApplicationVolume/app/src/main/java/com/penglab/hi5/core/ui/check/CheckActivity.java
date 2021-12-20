package com.penglab.hi5.core.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.penglab.hi5.R;
import com.penglab.hi5.core.BaseActivity;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.data.Result;

/**
 * Created by Jackiexing on 12/18/21
 */
public class CheckActivity extends BaseActivity {

    private static final String TAG = "CheckActivity";
    private CheckViewModel checkViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_check);
        setSupportActionBar(toolbar);

        checkViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(CheckViewModel.class);
        checkViewModel.getImageDataSource().getResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null){
                    return;
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.file:
                Log.e(TAG,"open file");
                return true;

            case R.id.more:
                Log.e(TAG,"more functions");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public static void start(Context context){
        Intent intent = new Intent(context, CheckActivity.class);
        context.startActivity(intent);
    }
}
