package com.penglab.hi5.basic.tracingfunc.gd;

import java.util.Vector;

public class DijkstraClass {
    public static final int ColorWhite = 0;
    public static final int ColorGrey = 1;
    public static final int ColorBlack = 2;
    public static final int ColorRed = 3;
    public static final double very_large_double = 1e+308;

    int nnode;

    Vector<Vector<ConnectionVal>> adjMatrix;

    byte[]  nodeColor; //decide if a node has been visited or not
    double[] nodeDistEst;
    int[] nodeParent;
    int[] nodeDetectTime;
    int[] nodeFinishTime;
    int b_disp;

    public DijkstraClass(int nodenum)
    {
        nnode = nodenum;
        b_disp = 0;
        nodeColor = new byte[nodenum];
        nodeDetectTime = new int[nodenum];
        nodeFinishTime = new int[nodenum];
        nodeDistEst = new double[nodenum];
        nodeParent = new int[nodenum];

        adjMatrix = new Vector<Vector<ConnectionVal>>();
        for(int i=0; i<nodenum; i++){
            adjMatrix.add(new Vector<ConnectionVal>());
        }
    }

    public static int extractWhiteMin(byte[] colorQ, Vector<Vector<ConnectionVal>> adjMatrix, int len)
    {
        double min=very_large_double;
        int idxmin=-1;
        int b_min0 = 0;
        int i,j,jtmp;
        float wei;
        for (i=0;i<len;i++)
        {
            if (colorQ[i]==ColorBlack)
            {
                for (jtmp=0;jtmp<adjMatrix.elementAt(i).size();jtmp++)
                {
                    j = adjMatrix.elementAt(i).elementAt(jtmp).cNode;
                    wei = adjMatrix.elementAt(i).elementAt(jtmp).aVal;
                    if (colorQ[j]==ColorWhite && wei>0)
                    {
                        if (b_min0==0)
                        {
                            b_min0 = 1;
                            min = wei;
                            idxmin = j;
                        }
                        else
                        { //if (b_min0==1)
                            if (min>wei)
                            {
                                min = wei;
                                idxmin = j;
                            }
                        }
                    }
                }
            }
        }
        if (b_min0==0)
        {
            idxmin = -1;
        }
        return idxmin;
    }


    public void dosearch(int r)
    {
        if (nnode<=0 || adjMatrix.isEmpty())
        {
            System.out.println("The input data has not been set yet!");
            return;
        }

        if (r<0 || r>=nnode)
        {
            System.out.println("The root node is invalid. Must between 0 and nnode-1! Do nothing.");
            return;
        }

        float curEdgeVal;
        int i,j,jtmp;
        int[] localQueue_node;
        int time;
        int nleftnode;

        localQueue_node = new int[nnode];
        // if (!localQueue_node)
        // {
        //     System.out.println("Fail to do: int * localQueue_node = new int [nnode];");
        //     goto Label_FreeMemory_Return;
        // }

        // initialization

        for (i=0;i<nnode;i++)
        {
            localQueue_node[i] = i;
            nodeColor[i] = ColorWhite;
            nodeDistEst[i] = very_large_double;//revise a larger num later
            nodeParent[i] = -1;
            nodeDetectTime[i] = -1;
            nodeFinishTime[i] = -1;
        }
        time = 0;
        nodeDistEst[r] = 0;
        nodeParent[r] = -1;

        // begin loop

        nleftnode = nnode;
        while (nleftnode>0)
        {
            if(nleftnode%500==0)
                System.out.println(nleftnode);
            i = extractWhiteMin(nodeColor,adjMatrix,nnode);
            if (i==-1)
            { //for the first node
                i = r;
            }
            nodeDetectTime[i] = ++time;

            if (b_disp==1)
            {
                System.out.printf("time=%i curnode=%i \n",time,i+1);
            }

            for(jtmp=0;jtmp<adjMatrix.elementAt(i).size();jtmp++)
            {
                j = adjMatrix.elementAt(i).elementAt(jtmp).cNode;
                curEdgeVal = adjMatrix.elementAt(i).elementAt(jtmp).aVal; //getAdjMatrixValue(i,j); //this will be faster

                if (curEdgeVal>0 &&
                        nodeColor[j]==ColorWhite &&
                        curEdgeVal<nodeDistEst[j])
                {
                    nodeParent[j] = i+1; //add 1 for the matlab convention
                    nodeDistEst[j] = curEdgeVal;
                }
            }

            nodeColor[i] = ColorBlack;
            nodeFinishTime[i] = ++time;
            nleftnode--;
        }

        return;
    }

    public float getAdjMatrixValue(int parentNode, int childNode){
        if (adjMatrix.isEmpty()) return 0;
        if (parentNode>=nnode || parentNode<0 || childNode>=nnode || childNode<0) return 0;

        boolean b_exist=false;
        for (int j=0;j<adjMatrix.elementAt(parentNode).size();j++)
        {
            if (adjMatrix.elementAt(parentNode).elementAt(j).cNode==childNode) {b_exist=true; return adjMatrix.elementAt(parentNode).elementAt(j).aVal;}
        }
//        if (!b_exist) return 0;
        return 0;
    }
    public void printAdjMatrix(){
        for (int i=0;i<nnode;i++)
        {
            for (int jtmp=0;jtmp<adjMatrix.elementAt(i).size();jtmp++)
            {
                System.out.printf("row [%d] (%d -> %d) = %5.3f\n", i+1, adjMatrix.elementAt(i).elementAt(jtmp).pNode+1,
                        adjMatrix.elementAt(i).elementAt(jtmp).cNode+1, adjMatrix.elementAt(i).elementAt(jtmp).aVal);
            }
        }
    }


}
