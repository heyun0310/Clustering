package Clustering.HierarchicalClustering;

import Clustering.Utils.Point;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HierarchicalClustering {

    private ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<ArrayList<Double>>();
    private DisjointSets disjointSets;
    private LinkedList<Point> pointLinkedList;
    private double thresholdDistance;

    DecimalFormat df = new DecimalFormat("######0.00");

    public HierarchicalClustering(LinkedList<Point> pointLinkedList, double threshold)
    {
        this.pointLinkedList = pointLinkedList;
        this.thresholdDistance = threshold;
    }

    public void init()
    {
        for (int i = 0; i < pointLinkedList.size(); i++) {

            distanceMatrix.add(new ArrayList<Double>());

            //建立并查集
            disjointSets = new DisjointSets(pointLinkedList.size());

            //计算距离矩阵
            for (int j = 0; j < pointLinkedList.size(); j++) {
                //距离矩阵右上半部分数值为0.00
                if (i <= j)
                {
                    distanceMatrix.get(i).add(new Double(df.format(0)));
                }else
                {
                    //计算两点之间的欧式距离并加入距离矩阵
                    double distance = pointLinkedList.get(i).calDistance(pointLinkedList.get(j));
                    distanceMatrix.get(i).add(Double.parseDouble(df.format(distance)));
                }
            }
        }
    }

    public void cluster()
    {
        //寻找当前矩阵元素中的最小值，并且将这两个坐标点聚为一类
        //记录最短距离对应的聚类/点坐标
        double shortestDistance = distanceMatrix.get(0).get(0);
        int number1 = 0, number2 = 0;

        for (int i = 0; i < distanceMatrix.size(); i++) {
            for (int j = 0; j < distanceMatrix.get(i).size(); j++) {
                if (i > j)
                {
                    if (distanceMatrix.get(i).get(j) < shortestDistance)
                    {
                        shortestDistance = distanceMatrix.get(i).get(j);
                        number1 = i;
                        number2 = j;
                    }
                }

            }
        }

        if (shortestDistance > thresholdDistance)
            return;

        //在此聚类
        if (disjointSets.find(number1) != disjointSets.find(number2))
            disjointSets.union(disjointSets.find(number1), disjointSets.find(number2));

        //更新距离矩阵（第number1行、第number1列）
        ArrayList<Double> rowNum1OfDistanceMatrix = distanceMatrix.get(number1);
        ArrayList<Double> rowNum2OfDistanceMatrix = distanceMatrix.get(number2);

        ArrayList<Double> updateRowAndColumnNum1OfDistanceMatrix = new ArrayList<Double>();
        ArrayList<Double> updateRowAndColumnNum2OfDistanceMatrix = new ArrayList<Double>();

        for (int i = 0; i < rowNum1OfDistanceMatrix.size(); i++) {
                Double shorterDistance = Math.min(rowNum1OfDistanceMatrix.get(i), rowNum2OfDistanceMatrix.get(i));
                if (shorterDistance.doubleValue() != 0)
                    updateRowAndColumnNum1OfDistanceMatrix.add(shorterDistance);
                else
                    updateRowAndColumnNum1OfDistanceMatrix.add(Double.MAX_VALUE);
                updateRowAndColumnNum2OfDistanceMatrix.add(Double.MAX_VALUE);
        }

        distanceMatrix.set(number1, updateRowAndColumnNum1OfDistanceMatrix);
        distanceMatrix.set(number2, updateRowAndColumnNum2OfDistanceMatrix);

        for (int i = 0; i < distanceMatrix.size(); i++) {
            distanceMatrix.get(i).set(number1, updateRowAndColumnNum1OfDistanceMatrix.get(i));
            distanceMatrix.get(i).set(number2, updateRowAndColumnNum2OfDistanceMatrix.get(i));
        }

        cluster();

    }

    public Map<Integer, ArrayList<Point>> getResult()
    {
        Map<Integer, ArrayList<Point>> map = new HashMap<>();

        for (int i = 0; i < pointLinkedList.size(); i++) {
            if (disjointSets.find(i) == i)
                map.put(i, new ArrayList<Point>());
        }

        for (int i = 0; i < pointLinkedList.size(); i++) {
            if (disjointSets.find(i) == i)
                map.get(i).add(pointLinkedList.get(i));
            else
                map.get(disjointSets.find(i)).add(pointLinkedList.get(i));
        }

        return map;
    }

    public DisjointSets getDisjointSets()
    {
        return disjointSets;
    }

}
