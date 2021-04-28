package com.main.core.collaboration.basic;

public interface ReceiveMsgInterface {

    /**
     process msg when the service receive msg from server
     */
    public void onRecMessage(String msg);
}
