package com.tracingfunc.gd;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

public class Gragh {
    private List<Vertex> vertexs;
    Vector<Pair<Integer,Integer>> edge_array;
    Vector<Float> weights;

    private Queue<Vertex> unVisited;
    public Gragh(List<Vertex> vertexs,Vector<Pair<Integer,Integer>> edge_array,Vector<Float> weights){
        this.vertexs = vertexs;
        this.edge_array = edge_array;
        this.weights = weights;
    }

    public List<Vertex> getVertexs() {
        return vertexs;
    }

    public void search(){
        while(!unVisited.isEmpty()){
            Vertex vertex = unVisited.element();
            vertex.setMarkered(true);
            List<Vertex> neighbors = getNeighbors(vertex);
            updatesDistance(vertex, neighbors);
            pop();
        }
        System.out.println("search over");
    }

    /*
     * 更新所有邻居的最短路径
     */
    private void updatesDistance(Vertex vertex, List<Vertex> neighbors){
        for(Vertex neighbor: neighbors){
            updateDistance(vertex, neighbor);
        }
    }

    /*
     * 更新邻居的最短路径
     */
    private void updateDistance(Vertex vertex, Vertex neighbor){
        float distance = getDistance(vertex, neighbor) + vertex.getPath();
        if(distance < neighbor.getPath()){
            neighbor.setParent(vertex.getIndex());
            neighbor.setPath(distance);
        }
    }

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

    /*
     * 获取顶点到目标顶点的距离
     */
    private float getDistance(Vertex source, Vertex destination) {
        return edge_array.indexOf(new Pair<Integer,Integer>(source.getIndex(),destination.getIndex()));
    }

    /*
     * 获取顶点所有(未访问的)邻居
     */
    private List<Vertex> getNeighbors(Vertex v) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        int index = v.getIndex();
        Vertex neighbor = null;
        double distance;
        for(int i=0; i<edge_array.size(); i++){
            if(edge_array.elementAt(i).getKey()==i){
                neighbor = getVertex(edge_array.elementAt(i).getValue());
                if(!neighbor.isMarkered()){
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    /*
     * 根据顶点位置获取顶点
     */
    private Vertex getVertex(int index) {
        return vertexs.get(index);
    }


}
