package com.penglab.hi5.core.ui.QualityInspection;

public class QueryCheckerResult {

    private final String owner;
    private final int checkResult;


    public QueryCheckerResult(String owner, int checkResult) {
        this.owner = owner;
        this.checkResult = checkResult;
    }

    public String getOwner(){
        return owner;
    }
    public int getCheckResult(){
        return checkResult;
    }
}
