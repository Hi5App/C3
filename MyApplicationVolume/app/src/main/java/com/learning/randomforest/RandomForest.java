package com.learning.randomforest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RandomForest {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();//可用的线程数量
    public int C;//目标类别的数量
    public int M;//属性的数量
    public int Ms;//属性扰动，每次从M个属性随机选取Ms个属性，Ms = log2（M）

    private ArrayList<DecisionTree> trees;

    private long startTime;
    private int numTrees;

    private double update;//实时显示进度，每建立一棵树的更新量
    private double progress;

    private int[] importance;

    private HashMap<int[], int[]> estimateOOB;//key:数据记录，value:RF的分类结果

    private ArrayList<ArrayList<Integer>> Prediction;

    private double error;//RF的错误率

    private ExecutorService treePool;//控制树生长的进程池

    private ArrayList<int[]> trainData;
    private ArrayList<int[]> testData;

    public RandomForest(int numTrees, ArrayList<int[]> trainData, ArrayList<int[]> testData){
        this.numTrees = numTrees;
        this.trainData = trainData;
        this.testData = testData;
        trees = new ArrayList<DecisionTree>(numTrees);
        update = 100.0/((double)numTrees);
        progress = 0;
        StartTimer();
        System.out.println("creating "+numTrees+" trees in a random Forest. . .");
        System.out.println("total data size is "+trainData.size());
        System.out.println("number of attributes " + (trainData.get(0).length-1));
        System.out.println("number of selected attributes " + ((int)Math.round(Math.log(trainData.get(0).length-1)/Math.log(2) + 1)));
        estimateOOB = new HashMap<int[],int[]>(trainData.size());
        Prediction = new ArrayList<ArrayList<Integer>>();
    }

    public void start(){
        System.out.println("Num of threads started : " + NUM_THREADS);
        System.out.println("Running...");
        treePool = Executors.newFixedThreadPool(NUM_THREADS);
        for (int t=0; t < numTrees; t++){
            System.out.println("constructing " + t + " Tree");
            treePool.execute(new CreateTree(trainData,this,t+1));
            //System.out.print(".");
        }
        treePool.shutdown();
        try {
            treePool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); //effectively infinity
        } catch (InterruptedException ignored){
            System.out.println("interrupted exception in Random Forests");
        }
        System.out.println("");
        System.out.println("Finished tree construction");
        TestForest(trees, testData);
        CalcImportance();
        System.out.println("Done in "+TimeElapsed(startTime));
    }

    /**
     * @param collec_tree   the collection of the forest's decision trees
     * @param test_data	    测试数据集
     */
    private void TestForest(ArrayList<DecisionTree> collec_tree, ArrayList<int[]> test_data ) {
        int correstness = 0 ;
        int k = 0;
        ArrayList<Integer> actualLabel = new ArrayList<Integer>();
        for(int[] rec:test_data){
            actualLabel.add(rec[rec.length-1]);
        }
        int treeNumber = 1;
        for(DecisionTree dt:collec_tree){
            dt.CalculateClasses(test_data, treeNumber);
            Prediction.add(dt.predictions);
            treeNumber++;
        }
        for(int i = 0; i<test_data.size(); i++){
            ArrayList<Integer> Val = new ArrayList<Integer>();
            for(int j =0; j<collec_tree.size(); j++){
                Val.add(Prediction.get(j).get(i));//The collection of each Tree's prediction in i-th record
            }
            int pred = labelVote(Val);//Voting algorithm
            if(pred == actualLabel.get(i)){
                correstness++;
            }
        }
        System.out.println("Accuracy of Forest is : " + (100 * correstness / test_data.size()) + "%");
    }

    /**
     * Voting algorithm
     * @param treePredict   The collection of each Tree's prediction in i-th record
     */
    private int labelVote(ArrayList<Integer> treePredict){
        // TODO Auto-generated method stub
        int max=0, maxclass=-1;
        for(int i=0; i<treePredict.size(); i++){
            int count = 0;
            for(int j=0; j<treePredict.size(); j++){
                if(treePredict.get(j) == treePredict.get(i)){
                    count++;
                }
                if(count > max){
                    maxclass = treePredict.get(i);
                    max = count;
                }
            }
        }
        return maxclass;
    }
    /**
     * 计算RF的分类错误率
     */
    private void CalcErrorRate(){
        double N=0;
        int correct=0;
        for (int[] record:estimateOOB.keySet()){
            N++;
            int[] map=estimateOOB.get(record);
            int Class=FindMaxIndex(map);
            if (Class == DecisionTree.GetClass(record))
                correct++;
        }
        error=1-correct/N;
        System.out.println("correctly mapped "+correct);
        System.out.println("Forest error rate % is: "+(error*100));
    }
    /**
     * 更新  OOBEstimate
     * @param record	        a record from data matrix
     * @param Class
     */
    public void UpdateOOBEstimate(int[] record, int Class){
        if (estimateOOB.get(record) == null){
            int[] map = new int[C];
            //System.out.println("class of record : "+Class);map[Class-1]++;
            estimateOOB.put(record,map);
        }
        else {
            int[] map = estimateOOB.get(record);
            map[Class-1]++;
        }
    }
    /**
     * calculates the importance levels for all attributes.
     */
    private void CalcImportance() {
        importance = new int[M];
        for (DecisionTree tree:trees){
            for (int i=0; i<M; i++)
                importance[i] += tree.getImportanceLevel(i);
        }
        for (int i=0;i<M;i++)
            importance[i] /= numTrees;
        System.out.println("The forest-wide importance as follows:");
        for (int j=0; j<importance.length; j++){
            System.out.println("Attr" + j + ":" + importance[j]);
        }
    }
    /**
     * 创建一棵决策树
     */
    private class CreateTree implements Runnable{
        /** 训练数据 */
        private ArrayList<int[]> train_data;
        /** 随机森林 */
        private RandomForest forest;
        /** the numb of RF */
        private int treeNum;

        public CreateTree(ArrayList<int[]> train_data, RandomForest forest, int num){
            this.train_data = train_data;
            this.forest = forest;
            this.treeNum = num;
        }
        /**
         * Create the decision tree
         */
        public void run() {
            System.out.println("Creating a DecisionTree num : " + treeNum + " ");
            trees.add(new DecisionTree(train_data, forest, treeNum));
            //System.out.println("tree added in RandomForest.AdDecisionTree.run()");
            progress += update;
            System.out.println("---progress:" + progress);
        }
    }

    /**
     * Evaluates testdata
     * @param record	a record to be evaluated
     */
    public int Evaluate(int[] record){
        int[] counts=new int[C];
        for (int t=0;t<numTrees;t++){
            int Class=(trees.get(t)).Evaluate(record);
            counts[Class]++;
        }
        return FindMaxIndex(counts);
    }

    public static int FindMaxIndex(int[] arr){
        int index=0;
        int max = Integer.MIN_VALUE;
        for (int i=0;i<arr.length;i++){
            if (arr[i] > max){
                max=arr[i];
                index=i;
            }
        }
        return index;
    }


    /**
     * @param startTime	        开始时间
     * @return			the hr,min,s
     */
    private static String TimeElapsed(long startTime){
        int s=(int)(System.currentTimeMillis()-startTime)/1000;
        int h=(int)Math.floor(s/((double)3600));
        s-=(h*3600);
        int m=(int)Math.floor(s/((double)60));
        s-=(m*60);
        return ""+h+"hr "+m+"m "+s+"s";
    }

    private void StartTimer(){
        startTime = System.currentTimeMillis();
    }

}
