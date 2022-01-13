package com.penglab.hi5.data.model.img;

import com.penglab.hi5.basic.image.XYZ;

/**
 * Created by Jackiexing on 01/12/21
 */
public class PotentialSomaInfo {
    private final int id;
    private final String brainId;
    private final XYZ location;

    public PotentialSomaInfo(int id, String brainId, XYZ location) {
        this.id = id;
        this.brainId = brainId;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getBrainId() {
        return brainId;
    }

    public XYZ getLocation() {
        return location;
    }
}
