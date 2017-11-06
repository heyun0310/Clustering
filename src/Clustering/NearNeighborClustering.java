package Clustering;

import java.util.LinkedList;

public class NearNeighborClustering {

    public LinkedList<LinkedList<Clustering.Point>> cluster = new LinkedList<LinkedList<Clustering.Point>>();
    double threshold;

    public NearNeighborClustering(double threshold)
    {
        this.threshold = threshold;
    }

    public void cluster(Point point)
    {
        boolean classified = false;

        if (cluster.isEmpty())
        {
            LinkedList<Point> subCluster = new LinkedList<Point>();
            subCluster.add(point);
            cluster.add(subCluster);
        }else {
            for (int i = 0; i < cluster.size() && !classified; i++) {
                for (int j = 0; j < cluster.get(i).size() && !classified; j++) {
                    if (point.calDistance(cluster.get(i).get(j)) <= threshold) {
                        cluster.get(i).add(point);
                        classified = true;
                        break;
                    }
                }
            }
            if (!classified)
            {
                LinkedList<Point> subCluster = new LinkedList<Point>();
                subCluster.add(point);
                cluster.add(subCluster);
            }
        }

    }
}
