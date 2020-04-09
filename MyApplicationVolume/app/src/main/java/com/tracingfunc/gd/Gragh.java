package com.tracingfunc.gd;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

public class Gragh {
    Vector<Vector<Vertex>> edges;
    float[] dis;
    int[] plist;

    private boolean[] visited;
    public Gragh(int nodeNum,Vector<Pair<Integer,Integer>> edge_array,Vector<Float> weights){
        dis = new float[nodeNum];
        plist = new int[nodeNum];
        visited = new boolean[nodeNum];
        edges = new Vector<Vector<Vertex>>();
        for(int i=0; i<nodeNum; i++){
            edges.add(new Vector<Vertex>());
            dis[i] = Integer.MAX_VALUE;
            plist[i] = -2;
            visited[i] = false;
        }
        for(int i=0; i<edge_array.size(); i++){
            int a = edge_array.elementAt(i).getKey();
            int b = edge_array.elementAt(i).getValue();
            float w = weights.elementAt(i);
            edges.elementAt(a).add(new Vertex(b,w));
            edges.elementAt(b).add(new Vertex(a,w));
        }
    }

    public void search(int ori){
        Queue<Vertex> q = new PriorityQueue<Vertex>();
        dis[ori] = 0;
        plist[ori] = -1;
        q.add(new Vertex(ori,dis[ori]));

        int count = 0;
        while (!q.isEmpty()){
            Vertex x = q.element();
            q.poll();
            visited[x.getIndex()] = true;
            for(int i=0; i<edges.elementAt(x.getIndex()).size(); i++){
                Vertex y = edges.elementAt(x.getIndex()).elementAt(i);
                if(visited[y.getIndex()])
                    continue;
                if(dis[y.getIndex()]>x.getPath()+y.getPath()){
                    dis[y.getIndex()] = x.getPath()+y.getPath();
                    plist[y.getIndex()] = x.getIndex();
                    q.add(new Vertex(y.getIndex(),dis[y.getIndex()]));
                }
            }

            count++;
            if (count % 500 == 0){
                System.out.println(count);
            }
        }
    }

}
