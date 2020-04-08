package com.tracingfunc.gd;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

public class Gragh {
    private List<Vertex> vertexs;
    Vector<Vector<Vertex>> edges;
    float[] dis;
    int[] plist;

    private Queue<Vertex> unVisited;
    public Gragh(List<Vertex> vertexs,Vector<Pair<Integer,Integer>> edge_array,Vector<Float> weights){
        this.vertexs = vertexs;
        dis = new float[vertexs.size()];
        plist = new int[vertexs.size()];
        for(int i=0; i<vertexs.size(); i++){
            edges.add(new Vector<Vertex>());
            dis[i] = Integer.MAX_VALUE;
            plist[i] = -2;
        }
        for(int i=0; i<edge_array.size(); i++){
            int a = edge_array.elementAt(i).getKey();
            int b = edge_array.elementAt(i).getValue();
            float w = weights.elementAt(i);
            edges.elementAt(a).add(new Vertex(b,w));
            edges.elementAt(b).add(new Vertex(a,w));
        }
        initUnVisited();
    }

    public List<Vertex> getVertexs() {
        return vertexs;
    }

    public void search(int ori){
        Queue<Vertex> q = new PriorityQueue<Vertex>();
        dis[ori] = 0;
        plist[ori] = -1;
        q.add(new Vertex(ori,dis[ori]));
        while (!q.isEmpty()){
            Vertex x = q.element();
            q.poll();
            for(int i=0; i<edges.elementAt(x.getIndex()).size(); i++){
                Vertex y = edges.elementAt(x.getIndex()).elementAt(i);
                if(dis[y.getIndex()]>x.getPath()+y.getPath()){
                    dis[y.getIndex()] = x.getPath()+y.getPath();
                    plist[y.getIndex()] = x.getIndex();
                    q.add(new Vertex(y.getIndex(),dis[y.getIndex()]));
                }
            }
        }
    }

//    public void search(){
//        int count = 0;
//        while(!unVisited.isEmpty()){
//            Vertex vertex = unVisited.element();
//            vertex.setMarkered(true);
//            List<Vertex> neighbors = getNeighbors(vertex);
//            updatesDistance(vertex, neighbors);
//            pop();
//            count++;
//            if(count%10==0){
//                System.out.println(unVisited.size());
//            }
//        }
//        System.out.println("search over");
//    }

    /*
     * 更新所有邻居的最短路径
     */
//    private void updatesDistance(Vertex vertex, List<Vertex> neighbors){
//        for(Vertex neighbor: neighbors){
//            updateDistance(vertex, neighbor);
//        }
//    }

    /*
     * 更新邻居的最短路径
     */
//    private void updateDistance(Vertex vertex, Vertex neighbor){
//        float distance = getDistance(vertex, neighbor) + vertex.getPath();
//        if(distance < neighbor.getPath()){
//            neighbor.setParent(vertex.getIndex());
//            neighbor.setPath(distance);
//        }
//    }

    /*
     * 初始化未访问顶点集合
     */
    private void initUnVisited() {
        unVisited = new PriorityQueue<Vertex>();
        for (Vertex v : vertexs) {
            unVisited.add(v);
        }
    }

    /*
     * 从未访问顶点集合中删除已找到最短路径的节点
     */
    private void pop() {
        unVisited.poll();
    }

//    /*
//     * 获取顶点到目标顶点的距离
//     */
//    private float getDistance(Vertex source, Vertex destination) {
//        return edge_array.indexOf(new Pair<Integer,Integer>(source.getIndex(),destination.getIndex()));
//    }
//
//    /*
//     * 获取顶点所有(未访问的)邻居
//     */
//    private List<Vertex> getNeighbors(Vertex v) {
//        List<Vertex> neighbors = new ArrayList<Vertex>();
//        int index = v.getIndex();
//        Vertex neighbor = null;
//        double distance;
//        for(int i=0; i<edge_array.size(); i++){
//            if(edge_array.elementAt(i).getKey()==i){
//                neighbor = getVertex(edge_array.elementAt(i).getValue());
//                if(!neighbor.isMarkered()){
//                    neighbors.add(neighbor);
//                }
//            }
//        }
//        return neighbors;
//    }

    /*
     * 根据顶点位置获取顶点
     */
    private Vertex getVertex(int index) {
        return vertexs.get(index);
    }


}
