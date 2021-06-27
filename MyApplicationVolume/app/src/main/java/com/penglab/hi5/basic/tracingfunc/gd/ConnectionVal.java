package com.penglab.hi5.basic.tracingfunc.gd;

public class ConnectionVal {
    int pNode; //parent node index
    int cNode; //children node index
    float aVal; //adjacent matrix value (weight on an edge) //note that I changed the edge type from unsigned char to float
    ConnectionVal() {pNode=-1; cNode=-1; aVal=0;}
}
