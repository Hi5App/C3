package com.penglab.hi5.basic.tracingfunc.gd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

public class Graph {
    ArrayList<Vertex>[] edges;
    float[] dis;
    int[] plist;

    private boolean[] visited;
    public Graph(int nodeNum, Vector<Pair<Integer,Integer>> edge_array, Vector<Float> weights){
        try{
            System.out.println("----------in graph----------");
            dis = new float[nodeNum];
            plist = new int[nodeNum];
            visited = new boolean[nodeNum];
            System.out.println("-----------memory---------------------");
            edges = new ArrayList[nodeNum];
            for(int i=0; i<nodeNum; i++){
                edges[i] = new ArrayList<Vertex>(3);
                dis[i] = Float.MAX_VALUE;
                plist[i] = -2;
                visited[i] = false;
            }
            System.out.println("------------memory2---------------------");
            for(int i=0; i<edge_array.size(); i++){
                int a = edge_array.elementAt(i).getKey();
                int b = edge_array.elementAt(i).getValue();
                Float w = weights.elementAt(i);
                edges[a].add(new Vertex(b,w));
                edges[b].add(new Vertex(a,w));
            }
            System.out.println("----------graph end--------------");
        }catch (OutOfMemoryError e){
            System.out.println("-----------------start to print error info---------------");
            System.out.println(e.getMessage());
//            System.exit(1);
        }
    }

    public void search(int ori){
        Queue<Vertex> q = new PriorityQueue<Vertex>();
        dis[ori] = 0;
        plist[ori] = -1;
        q.add(new Vertex(ori,dis[ori]));

        int count = 0;
        Map<Integer,Vertex> indexVertexMap = new HashMap<Integer, Vertex>();
        while (!q.isEmpty()){
            Vertex x =q.poll();
            visited[x.getIndex()] = true;
            for(int i=0; i<edges[x.getIndex()].size(); i++){
                Vertex y = edges[x.getIndex()].get(i);
                if(!visited[y.getIndex()] && dis[y.getIndex()]>y.getPath()+dis[x.getIndex()] && y.getPath()>0){
                    dis[y.getIndex()] = y.getPath()+dis[x.getIndex()];
                    plist[y.getIndex()] = x.getIndex();
                    if(indexVertexMap.get(y.getIndex())!=null){
                        q.remove(indexVertexMap.get(y.getIndex()));
                        indexVertexMap.remove(y.getIndex());
                    }
                    Vertex v = new Vertex(y.getIndex(),dis[y.getIndex()]);
                    indexVertexMap.put(y.getIndex(),v);
                    q.add(v);
                }
            }

            count++;
            if (count % 500 == 0){
                System.out.println(count);
            }
        }
    }

}
