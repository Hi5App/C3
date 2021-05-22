package com.penglab.hi5.chat.nim.session.extension;

import com.alibaba.fastjson.JSONObject;


public class InviteAttachment extends CustomAttachment {
//    String port;
//    String brainNum;
//    String res;
    String invitor;
    String path;
//    String roomName;
    String soma;

//    String KEY_PORT = "port";
//    String KEY_BRAINNUM = "brainNum";
//    String KEY_RES = "res";
    String KEY_INVITOR = "invitor";
    String KEY_PATH = "path";
//    String KEY_ROOMNAME = "roomName";
    String KEY_SOMA = "soma";

    public InviteAttachment(){
        super(CustomAttachmentType.Invite);
    }

    public InviteAttachment(String invitor, String path, String soma){
        super(CustomAttachmentType.Invite);
        this.invitor = invitor;
        this.path = path;
        this.soma = soma;
    }


    @Override
    protected void parseData(JSONObject data) {
        invitor = data.getString(KEY_INVITOR);
        path = data.getString(KEY_PATH);
        soma = data.getString(KEY_SOMA);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_INVITOR, invitor);
        data.put(KEY_PATH, path);
        data.put(KEY_SOMA, soma);
        return data;
    }

    public String getInvitor() {
        return invitor;
    }

    public String getPath() {
        return path;
    }

    public String getSoma() {
        return soma;
    }
}