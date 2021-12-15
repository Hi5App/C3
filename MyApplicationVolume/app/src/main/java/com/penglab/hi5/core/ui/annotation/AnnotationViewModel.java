package com.penglab.hi5.core.ui.annotation;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jackiexing on 12/09/21
 */
public class AnnotationViewModel {

    public enum EditMode {
        NONE, ZOOM, PINPOINT, PAINTCURVE, DELETEMARKER, DELETECURVE, SPLIT, CHANGEMARKERTYPE, CHANGECURVETYPE, DELETEMULTIMARKER, ZOOMINROI
    }

    public enum OpenFileMode {
        NONE, IMPORTSWC, ANALYZESWC, LOADLOCALIMAGE
    }

    public enum PenColor {
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }

    private final MutableLiveData<UserInfoView> userInfo = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>();

    private MutableLiveData<EditMode> editMode = new MutableLiveData<>(EditMode.NONE);

    private MutableLiveData<OpenFileMode> openFileMode = new MutableLiveData<>(OpenFileMode.NONE);

    private FileInfoState fileInfoState = new FileInfoState();

    private boolean ifAnimation = false;

    private boolean isBigData_Remote;

    private int rotation_speed = 36;

    private boolean mBoundManagement = false;
    private boolean mBoundCollaboration = false;

    private boolean copyFile = false;
    private boolean firstLoad = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onRecMessage(String msg) {

        if (msg.startsWith("TestSocketConnection")){
            ServerConnector.getInstance().sendMsg("HeartBeat");
        }

        /*
        select file
         */
        if (msg.startsWith("GETFILELIST:")){
            processReceivedFileList(msg.split(":")[1]);
        }

        /*
        After msg:  "LOADFILES:0 /17301/17301_00019/17301_00019_x20874.000_y23540.000_z7388.000.ano /17301/17301_00019/test_01_fx_lh_test.ano"

        when the file is selected, room will be created, and collaborationService will be init, port is room number
         */
        if (msg.startsWith("Port:")){

        }

        /*
        After msg:  "/login:xf"

        server will send user list when the users in current room are changed
         */
        if (msg.startsWith("/users:")){
            if (firstLoad || copyFile){
                /*
                when first join the room, try to get the image
                 */
                MsgConnector.getInstance().sendMsg("/ImageRes:" + Communicator.BrainNum);
                firstLoad = false;
                copyFile   = false;
            }
            /*
            update the user list
             */
            String[] users = msg.split(":")[1].split(";");
            List<String> newUserList = Arrays.asList(users);
            fileInfoState.roomInfoState.setUserList(newUserList);
        }

        /*
        After msg:  "/ImageRes:18454"

        process the img resolution info; msg format: "Imgblock:18454;2;mid_x;mid_y;mid_z;size;"
         */
        if (msg.startsWith("ImgRes")){
            int resDefault = Math.min(2, Integer.parseInt(msg.split(";")[1]));
            Communicator.getInstance().initImgInfo(null, Integer.parseInt(msg.split(";")[1]), resDefault, msg.split(";"));
            MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + Communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");
        }

        /*
        After msg:  "/Imgblock:"

        process the img block & swc apo file; msg format: "GetBBSwc:18454;2;mid_x;mid_y;mid_z;size;"
         */
        if (msg.startsWith("Block:")){

        }

        if (msg.startsWith("File:")){

        }

        /*
        for collaboration -------------------------------------------------------------------
         */
        if (msg.startsWith("/drawline_norm:")){

        }

        if (msg.startsWith("/delline_norm:")){

        }

        if (msg.startsWith("/addmarker_norm:")){

        }

        if (msg.startsWith("/delmarker_norm:")){

        }

        if (msg.startsWith("/retypeline_norm:")){

        }

        /*
        for score things -------------------------------------------------------------------
         */
        if (msg.startsWith("Score:")){

        }

        if (msg.startsWith("GETFIRSTK:")){

        }
    }

    private void processReceivedFileList(String fileList) {
        List<String> list_array = new ArrayList<>();
        String[] list = fileList.split(";;");

        fileInfoState.isAFile = false;


        for (int i = 0; i < list.length; i++){
            if (list[i].split(" ")[0].endsWith(".apo") || list[i].split(" ")[0].endsWith(".eswc")
                    || list[i].split(" ")[0].endsWith(".swc") || list[i].split(" ")[0].endsWith("log") )
                continue;

            if(Communicator.getInstance().initSoma(list[i].split(" ")[0])){
                fileInfoState.fileName = list[i].split(" ")[0];
                fileInfoState.isAFile = true;
                continue;
            }

            list_array.add(list[i].split(" ")[0]);
        }

        if (fileInfoState.isAFile){
            list_array.add(0, "Create A New Room");
        }

        fileInfoState.sonFileList= new String[list_array.size()];
        for (int i = 0; i < list_array.size(); i++){
            fileInfoState.sonFileList[i] = list_array.get(i);
        }
    }

    public void createFile(String roomName) {
        ServerConnector serverConnector = ServerConnector.getInstance();
        Communicator.getInstance().setPath(fileInfoState.conPath + "/" + roomName);
        serverConnector.sendMsg("LOADFILES:0 " + fileInfoState.conPath + "/" + fileInfoState.fileName + " " + fileInfoState.conPath + "/" + roomName);
        serverConnector.setRoomName(roomName);
        fileInfoState.roomInfoState.setRoomName(roomName);
        copyFile = true;
    }

    public void loadFile(String fileName) {
        if (fileInfoState.isAFile) {
            ServerConnector serverConnector = ServerConnector.getInstance();
            serverConnector.sendMsg("LOADFILES:2 " + fileInfoState.conPath + "/" + fileName);
            serverConnector.setRoomName(fileName);
            fileInfoState.roomInfoState.setRoomName(fileName);

            Communicator.getInstance().setPath(fileInfoState.conPath + "/" + fileName);
            firstLoad = true;
        } else {
            fileInfoState.conPath.setValue(fileInfoState.conPath.getValue() + "/" + fileName);
            ServerConnector.getInstance().sendMsg("GETFILELIST:" + fileInfoState.conPath);
        }
    }

    public FileInfoState getFileInfoState() {
        return fileInfoState;
    }
}
