package com.tracingfunc.rg;

public class RgnGrow3dClass {
    public int imgWid, imgHei, imgDep;
    public byte[] quantImg1d;
    public byte[][][] quantImg3d;

    public byte[] PHCDONEIMG1d;
    public byte[][][] PHCDONEIMG3d;

    public int STACKCNT, MAXSTACKSIZE, IFINCREASELABEL, PHCURLABEL;

    public int[] PHCLABELSTACK1d;
    public int[][][] PHCLABELSTACK3d;
    public  int PHCLABELSTACKPOS;

    public POS PHCURGNPOS;
    public POS PHCURGNPOS_head;
    public RGN PHCURGN;
    public RGN PHCURGN_head;

    public int TOTALPOSnum, TOTALRGNnum;

    public RgnGrow3dClass(){
        imgWid = 0; imgHei = 0; imgDep = 0;
        quantImg1d = null; quantImg3d = null;
        PHCDONEIMG3d = null; PHCDONEIMG1d = null;

        STACKCNT = -1; MAXSTACKSIZE = 16; IFINCREASELABEL=-1; PHCURLABEL=-1;
        PHCLABELSTACK3d = null; PHCLABELSTACK1d = null;
        PHCLABELSTACKPOS = 0;

        PHCURGNPOS = null; PHCURGNPOS_head = null;
        PHCURGN = null; PHCURGN_head = null;
        TOTALPOSnum = 0; TOTALRGNnum = 0;
    }


    public void rgnfindsub(int rowi, int colj, int depk, int direction, int stackinc){

        if(this.STACKCNT >= this.MAXSTACKSIZE){
            if(this.IFINCREASELABEL != 0){
                this.IFINCREASELABEL = 0;
            }
            return;
        }

        byte[][][] flagImg = this.PHCDONEIMG3d;
        int imgWid = this.imgWid;
        int imgHei = this.imgHei;
        int imgDep = this.imgDep;

        if(stackinc == 1){
            this.PHCLABELSTACK3d[0][0][this.PHCLABELSTACKPOS] = depk;
            this.PHCLABELSTACK3d[0][1][this.PHCLABELSTACKPOS] = colj;
            this.PHCLABELSTACK3d[0][2][this.PHCLABELSTACKPOS] = rowi;

            this.STACKCNT++;
            this.PHCLABELSTACKPOS++;

            flagImg[depk][colj][rowi] = -1;

            if(this.PHCURGNPOS != null){
                this.PHCURGNPOS.pos = (int) depk*(this.imgHei * this.imgWid) + colj*(this.imgWid) + rowi; //
                this.PHCURGNPOS.next = new POS();
//                if (this.PHCURGNPOS.next==0)
//                {printf("Fail to do: this.PHCURGNPOS.next = new POS; -.current phcDebugPosNum=%i.\n",phcDebugPosNum);}
                this.PHCURGNPOS = this.PHCURGNPOS.next;
                this.TOTALPOSnum++;
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
