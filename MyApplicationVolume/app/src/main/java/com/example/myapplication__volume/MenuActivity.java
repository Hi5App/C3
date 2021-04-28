package com.example.myapplication__volume;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;

import com.example.myapplication__volume.collaboration.service.CollaborationService;
import com.example.myapplication__volume.collaboration.connector.MsgConnector;
import com.example.myapplication__volume.collaboration.connector.ServerConnector;
import com.example.myapplication__volume.collaboration.basic.ReceiveMsgInterface;

public class MenuActivity extends BaseActivity implements ReceiveMsgInterface {

    private CollaborationService collaborationService;

    private boolean mBound = false;

    private Context menuContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuContext = this;
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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection_msg = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CollaborationService.LocalBinder binder = (CollaborationService.LocalBinder) service;
            collaborationService = (CollaborationService) binder.getService();
            binder.addReceiveMsgInterface((MenuActivity) getActivityFromContext(menuContext));
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void initMsgConnector(String ip){
        MsgConnector msgConnector = MsgConnector.getInstance();

        msgConnector.setIp(ip_TencentCloud);
        msgConnector.setPort(ip);
        msgConnector.initConnection();
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