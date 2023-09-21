package com.penglab.hi5.data.model.img;

import com.penglab.hi5.basic.image.XYZ;

public class CollaborateNeuronInfo {
    private String imageId;
    private String name;
    private XYZ loc;
    private String[] resolutionList;
    private String currentRes;


    public CollaborateNeuronInfo(String image, String name, XYZ loc) {
        this.imageId = image;
        this.name = name;
        this.loc = loc;
        this.resolutionList = null;
        this.currentRes = "";
    }

    public CollaborateNeuronInfo(){
        this.imageId = "";
        this.name = "";
        this.loc = new XYZ();
    }




    public String getBrainName() {
        return imageId;
    }

    public String getNeuronName() {
        return name;
    }

    public XYZ getLocation() {
        return loc;
    }

    public void setBrainNumber (String brainNumber){
        this.imageId = brainNumber;
    }

    public void setNeuronNumber (String neuronNumber){
        this.name = neuronNumber;
    }

    public void setLocation(XYZ xyz){
        this.loc = xyz;
    }

    public String[] getResolutionList() {
        return resolutionList;
    }

    public void setResolutionList(String[] resolutionList) {
        this.resolutionList = resolutionList;
    }
}
