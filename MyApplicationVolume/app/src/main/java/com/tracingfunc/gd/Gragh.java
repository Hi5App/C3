package com.tracingfunc.gd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

public class Gragh {
    ArrayList<Vertex>[] edges;
    float[] dis;
    int[] plist;

    private boolean[] visited;
    public Gragh(int nodeNum,Vector<Pair<Integer,Integer>> edge_array,Vector<Float> weights){
        try{
            System.out.println("----------in gragh----------");
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
            System.out.println("edge size: "+edge_array.size()+" w size: "+weights.size());
            for(int i=0; i<edge_array.size(); i++){
                int a = edge_array.elementAt(i).getKey();
                int b = edge_array.elementAt(i).getValue();
                Float w = weights.elementAt(i);
                if(i<500){
                    System.out.println("a: "+a+" b: "+b+" w: "+w);
                }
                edges[a].add(new Vertex(b,w));
                edges[b].add(new Vertex(a,w));
            }
            System.out.println("----------gragh end--------------");
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
//            if(count<500){
//                System.out.println(count+":");
//                for (Vertex i : q ){
//                    System.out.println("index: "+i.getIndex()+" weight: "+i.getPath()+" childweight: "+i.getChildpath());
//                }
//                System.out.println("x index: "+x.getIndex()+" x weight: "+x.getPath()+" childweight: "+x.getChildpath());
//            }
//            q.poll();
            visited[x.getIndex()] = true;
            for(int i=0; i<edges[x.getIndex()].size(); i++){
                Vertex y = edges[x.getIndex()].get(i);
//                if(visited[y.getIndex()])
//                    continue;
                if(!visited[y.getIndex()] && dis[y.getIndex()]>y.getPath()+dis[x.getIndex()] && y.getPath()>0){
                    dis[y.getIndex()] = y.getPath()+dis[x.getIndex()];
                    plist[y.getIndex()] = x.getIndex();
                    if(indexVertexMap.get(y.getIndex())!=null){
                        q.remove(indexVertexMap.get(y.getIndex()));
                        indexVertexMap.remove(y.getIndex());
//                        if(count<500){
//                            System.out.println("before: "+y.getIndex());
//                            System.out.println(q.size());
//                        }
                    }
//                    float childpath = 0;
//                    for(int j=0; j<edges[y.getIndex()].size(); j++){
//                        Vertex z = edges[y.getIndex()].get(j);
//                        if(visited[z.getIndex()])
//                            continue;
//                        if(childpath<z.getPath()){
//                            childpath += z.getPath();
//                        }
//                    }
//                    childpath /= edges[y.getIndex()].size();
                    Vertex v = new Vertex(y.getIndex(),dis[y.getIndex()]);
                    indexVertexMap.put(y.getIndex(),v);
                    q.add(v);
//                    if(count<500){
//                        System.out.println("after: "+dis[y.getIndex()]);
//                        System.out.println(q.size());
//                    }
                }
            }

            count++;
            if (count % 500 == 0){
                System.out.println(count);
            }
        }
    }

}
