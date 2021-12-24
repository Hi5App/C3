package com.penglab.hi5.data.model.img;

public class AnoInfo {
    private String anoName;
    private String neuronId;
    private String anoUrl;
    private String apoUrl;
    private String swcUrl;
    private String owner;

    public AnoInfo(String anoName, String neuronId, String anoUrl, String apoUrl, String swcUrl, String owner) {
        this.anoName = anoName;
        this.neuronId = neuronId;
        this.anoUrl = anoUrl;
        this.apoUrl = apoUrl;
        this.swcUrl = swcUrl;
        this.owner = owner;
    }

    public String getAnoName() {
        return anoName;
    }

    public void setAnoName(String anoName) {
        this.anoName = anoName;
    }

    public String getNeuronId() {
        return neuronId;
    }

    public void setNeuronId(String neuronId) {
        this.neuronId = neuronId;
    }

    public String getAnoUrl() {
        return anoUrl;
    }

    public void setAnoUrl(String anoUrl) {
        this.anoUrl = anoUrl;
    }

    public String getApoUrl() {
        return apoUrl;
    }

    public void setApoUrl(String apoUrl) {
        this.apoUrl = apoUrl;
    }

    public String getSwcUrl() {
        return swcUrl;
    }

    public void setSwcUrl(String swcUrl) {
        this.swcUrl = swcUrl;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
