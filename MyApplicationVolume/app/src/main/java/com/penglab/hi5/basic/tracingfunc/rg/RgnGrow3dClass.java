package com.penglab.hi5.basic.tracingfunc.rg;

public class RgnGrow3dClass {
    public int imgWid, imgHei, imgDep;
//    public byte[] quantImg1d;
    public byte[][][] quantImg;

//    public byte[] PHCDONEIMG1d;
    public byte[][][] phcDoneImg;//PHCDONEIMG3d;

    public int stackCount, maxStackSize, ifIncreaseLabel, phcCurLabel;

//    public int[] PHCLABELSTACK1d;
    public int[][][] phcLabelStack;//PHCLABELSTACK3d;
    public  int phcLabelStackPos;//PHCLABELSTACKPOS;

    public POS phcRgnPos;//PHCURGNPOS;
    public POS phcRgnPosHead;//PHCURGNPOS_head;
    public RGN phcRgn;//PHCURGN;
    public RGN phcRgnHead;//PHCURGN_head;

    public int totalPosNum, totalRgnNum;

    public RgnGrow3dClass(){
        imgWid = 0; imgHei = 0; imgDep = 0;
        quantImg = null;
        phcDoneImg = null;

        stackCount = -1; maxStackSize = 16; ifIncreaseLabel = -1; phcCurLabel = -1;
        phcLabelStack = null;
        phcLabelStackPos = 0;

        phcRgnPos = null; phcRgnPosHead = null;
        phcRgn = null; phcRgnHead = null;
        totalPosNum = 0; totalRgnNum = 0;
    }


    public void rgnfindsub(int rowi, int colj, int depk, int direction, int stackinc){

        if(this.stackCount>= this.maxStackSize){
            if(this.ifIncreaseLabel != 0){
                this.ifIncreaseLabel = 0;
            }
            return;
        }

        byte[][][] flagImg = this.phcDoneImg;
        int imgWid = this.imgWid;
        int imgHei = this.imgHei;
        int imgDep = this.imgDep;

        if(stackinc == 1){
            this.phcLabelStack[0][0][this.phcLabelStackPos] = depk;
            this.phcLabelStack[0][1][this.phcLabelStackPos] = colj;
            this.phcLabelStack[0][2][this.phcLabelStackPos] = rowi;

            this.stackCount++;
            this.phcLabelStackPos++;

            flagImg[depk][colj][rowi] = -1;

            if(this.phcRgnPos != null){
                this.phcRgnPos.pos = depk*(this.imgHei * this.imgWid) + colj*(this.imgWid) + rowi; //
                this.phcRgnPos.next = new POS();
//                if (this.PHCURGNPOS.next==0)
//                {printf("Fail to do: this.PHCURGNPOS.next = new POS; -.current phcDebugPosNum=%i.\n",phcDebugPosNum);}
                this.phcRgnPos = this.phcRgnPos.next;
                this.totalPosNum++;
            }else {
                System.out.println("PHCURGNPOS is null!!");
            }
        }else {   //%if stackinc==0
            flagImg[depk][colj][rowi] = -2;
        }

        // % search 26 direction orders

        // 1
        if (rowi>0 && flagImg[depk][colj][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj,depk,1,1);
        // 2
        if (rowi<imgWid-1 && flagImg[depk][colj][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj,depk,1,1);
        // 3
        if (colj>0 && flagImg[depk][colj-1][rowi]==1)
            this.rgnfindsub(rowi,colj-1,depk,1,1);
        // 4
        if (colj<imgHei-1 && flagImg[depk][colj+1][rowi]==1)
            this.rgnfindsub(rowi,colj+1,depk,1,1);
        // 5
        if (depk>0 && flagImg[depk-1][colj][rowi]==1)
            this.rgnfindsub(rowi,colj,depk-1,1,1);
        // 6
        if (depk<imgDep-1 && flagImg[depk+1][colj][rowi]==1)
            this.rgnfindsub(rowi,colj,depk+1,1,1);
        // 7
        if (rowi>0 && colj>0 && flagImg[depk][colj-1][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj-1,depk,1,1);
        // 8
        if (rowi<imgWid-1 && colj>0 && flagImg[depk][colj-1][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj-1,depk,1,1);
        // 9
        if (rowi>0 && colj<imgHei-1 && flagImg[depk][colj+1][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj+1,depk,1,1);
        // 10
        if (rowi>imgWid && colj<imgHei-1 && flagImg[depk][colj+1][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj+1,depk,1,1);
        // 11
        if (rowi>0 && depk>0 && flagImg[depk-1][colj][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj,depk-1,1,1);
        // 12
        if (rowi<imgWid-1 && depk>0 && flagImg[depk-1][colj][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj,depk-1,1,1);
        // 13
        if (rowi>0 && depk<imgDep-1 && flagImg[depk+1][colj][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj,depk+1,1,1);
        // 14
        if (rowi<imgWid-1 && depk<imgDep-1 && flagImg[depk+1][colj][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj,depk+1,1,1);
        // 15
        if (colj>0 && depk>0 && flagImg[depk-1][colj-1][rowi]==1)
            this.rgnfindsub(rowi,colj-1,depk-1,1,1);
        // 16
        if (colj<imgHei-1 && depk>0 && flagImg[depk-1][colj+1][rowi]==1)
            this.rgnfindsub(rowi,colj+1,depk-1,1,1);
        // 17
        if (colj>0 && depk<imgDep-1 && flagImg[depk+1][colj-1][rowi]==1)
            this.rgnfindsub(rowi,colj-1,depk+1,1,1);
        // 18
        if (colj<imgHei-1 && depk<imgDep-1 && flagImg[depk+1][colj+1][rowi]==1)
            this.rgnfindsub(rowi,colj+1,depk+1,1,1);
        // 19
        if (rowi>0 && colj>0 && depk>0 && flagImg[depk-1][colj-1][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj-1,depk-1,1,1);
        // 20
        if (rowi<imgWid-1 && colj>0 && depk>0 && flagImg[depk-1][colj-1][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj-1,depk-1,1,1);
        // 21
        if (rowi>0 && colj<imgHei-1 && depk>0 && flagImg[depk-1][colj+1][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj+1,depk-1,1,1);
        // 22
        if (rowi>0 && colj>0 && depk<imgDep-1 && flagImg[depk+1][colj-1][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj-1,depk+1,1,1);
        // 23
        if (rowi<imgWid-1 && colj<imgHei-1 && depk>0 && flagImg[depk-1][colj+1][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj+1,depk-1,1,1);
        // 24
        if (rowi<imgWid-1 && colj>0 && depk<imgDep-1 && flagImg[depk+1][colj-1][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj-1,depk+1,1,1);
        // 25
        if (rowi>0 && colj<imgHei-1 && depk<imgDep-1 && flagImg[depk+1][colj+1][rowi-1]==1)
            this.rgnfindsub(rowi-1,colj+1,depk+1,1,1);
        // 26
        if (rowi<imgWid-1 && colj<imgHei-1 && depk<imgDep-1 && flagImg[depk+1][colj+1][rowi+1]==1)
            this.rgnfindsub(rowi+1,colj+1,depk+1,1,1);


        return;

    }


}
