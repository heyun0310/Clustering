package Clustering.KMeansClustering;

import Clustering.Utils.Point;

import java.util.LinkedList;

public class KMeansClustering {

    //种子点数组
    private LinkedList<Point> seedsList;
    //需要被聚类的坐标数组
    private LinkedList<Point> pointLinkedList;
    //已被分类的坐标数组
    private LinkedList<LinkedList<Point>> pointsClassifiedBySeeds;
    private double[][] distanceMatrix;
    //用于标记种子点是否已经确定了最终位置
    private static boolean seedsFixed = false;

    public KMeansClustering(LinkedList<Point> seedsList, LinkedList<Point> pointLinkedList)
    {
        this.seedsList = seedsList;
        this.pointLinkedList = pointLinkedList;
        pointsClassifiedBySeeds = new LinkedList<LinkedList<Point>>();
        for (int i = 0; i < seedsList.size(); i++) {
            this.pointsClassifiedBySeeds.add(new LinkedList<Point>());
        }
        distanceMatrix = new double[pointLinkedList.size()][seedsList.size()];

    }

    private void calDistanceMatrix()
    {
        for (int i = 0; i < pointLinkedList.size(); i++) {
            for (int j = 0; j < seedsList.size(); j++) {
                distanceMatrix[i][j] = pointLinkedList.get(i).calDistance(seedsList.get(j));
            }

            //找出距离当前坐标最近的种子点并把当前坐标加入到pointsClassifiedBySeeds
            double shortestDistance = distanceMatrix[i][0];
            int seedIndex = 0;
            for (int j = 0; j < seedsList.size(); j++) {
                if (distanceMatrix[i][j] < shortestDistance)
                {
                    shortestDistance = distanceMatrix[i][j];
                    seedIndex = j;
                }
            }
            Point point = pointLinkedList.get(i);
            pointsClassifiedBySeeds.get(seedIndex).add(point);
        }
    }

    private void moveSeeds()
    {
        int averageX = 0;
        int averageY = 0;
        for (int i = 0; i < pointsClassifiedBySeeds.size(); i++) {
            for (Point point: pointsClassifiedBySeeds.get(i)) {
                averageX += point.getX();
                averageY += point.getY();
            }
            averageX /= pointsClassifiedBySeeds.get(i).size();
            averageY /= pointsClassifiedBySeeds.get(i).size();
            if (averageX != seedsList.get(i).getX() && averageY != seedsList.get(i).getY())
            {
                seedsList.get(i).setX(averageX);
                seedsList.get(i).setY(averageY);
            }else
                seedsFixed = true;
        }
    }

    public LinkedList<LinkedList<Point>> cluster()
    {
        while (!seedsFixed)
        {
            calDistanceMatrix();
            moveSeeds();
        }

        return pointsClassifiedBySeeds;
    }

}
