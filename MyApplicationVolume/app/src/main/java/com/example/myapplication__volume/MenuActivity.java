package com.example.myapplication__volume;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.myapplication__volume.collaboration.MsgConnector;
import com.example.myapplication__volume.collaboration.ServerConnector;
import com.example.myapplication__volume.collaboration.basic.ReceiveMsgInterface;

public class MenuActivity extends BaseActivity implements ReceiveMsgInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    /**
     * on top bar menu created, link res/menu/main.xml
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return true;
    }

    @Override
    public void onRecMessage(String msg) {

    }

    public void singlePlayer(View view) {
        ServerConnector serverConnector = ServerConnector.getInstance();
        MsgConnector msgConnector = MsgConnector.getInstance();
//        serverConnector.sendMsg("hello world !");

        serverConnector.sendMsg("GETFILELIST:" + "/");
    }


}