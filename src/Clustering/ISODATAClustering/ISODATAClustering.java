package Clustering.ISODATAClustering;

import Clustering.Utils.Point;
import Clustering.Utils.Point_double;

import java.text.DecimalFormat;
import java.util.*;

public class ISODATAClustering {

    //预期聚类个数
    private int expClstNum;

    //初始聚类中心个数
    private int initClstNum;

    //每一类中允许的最少样本数目（若少于此数，就不能单独成为一类）
    private int lstSampNum;

    //聚类内各特征向量相对标准差上限（大于lstSampNum则分裂）
    private int stdDevitLmt;

    //两聚类中心的最小距离下限（若小于此数，则两类应该合并）
    private double shtstDist;

    //每次迭代中最多可以进行合并的此数
    private int mstMrgNum;

    //最大迭代此数
    private int mstItrtNum;

    private int itrtNum;

    private LinkedList<Point_double> pointLinkedList;
    private LinkedList<LinkedList<Point_double>> clusters;
    private LinkedList<Point_double> clusterCenters;
    private LinkedList<LinkedList<Double>> distanceMatrix;

    //步骤函数产生结果
    private double[] step5_avgDis;
    private double step6_avgDis;
    private LinkedList<Point_double> step8_stdDevitVct;
    private LinkedList<LinkedList<Double>> step9_lrgtCpntList;
    private LinkedList<LinkedList<Double>> step11_disBtwnCtrs;

    //第一步，输入参数
    public ISODATAClustering(LinkedList<Point> pointLinkedList, int[] clusterCenterIndex,
                             int expClstNum, int lstSampNum, int stdDevitLmt, double shtstDist,
                             int mstMrgNum, int mstItrtNum)
    {

        this.pointLinkedList = new LinkedList<>();

        //将pointLinkedList中的int型Point转化为double型的Point
        for (int i = 0; i < pointLinkedList.size(); i++) {
            this.pointLinkedList.add(new Point_double());
            this.pointLinkedList.get(i).setX(pointLinkedList.get(i).getX());
            this.pointLinkedList.get(i).setY(pointLinkedList.get(i).getY());
        }

        this.expClstNum = expClstNum;
        this.initClstNum = clusterCenterIndex.length;
        this.lstSampNum = lstSampNum;
        this.stdDevitLmt = stdDevitLmt;
        this.shtstDist = shtstDist;
        this.mstMrgNum = mstMrgNum;
        this.mstItrtNum = mstItrtNum;

        this.clusters = new LinkedList<>();
        this.clusterCenters = new LinkedList<>();
        this.distanceMatrix = new LinkedList<>();

        this.step8_stdDevitVct = new LinkedList<>();
        this.step9_lrgtCpntList = new LinkedList<>();
        this.step11_disBtwnCtrs = new LinkedList<>();

        //初始化聚类中心集合
        for (int i = 0; i < clusterCenterIndex.length; i++) {
            clusterCenters.add(this.pointLinkedList.get(clusterCenterIndex[i]));
        }
    }

    public LinkedList<LinkedList<Point_double>> isodataCluster()
    {
        step2();

        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.get(i).size(); j++) {
                clusters.get(i).get(j).setX(Double.parseDouble(decimalFormat.format(clusters.get(i).get(j).getX())));
                clusters.get(i).get(j).setY(Double.parseDouble(decimalFormat.format(clusters.get(i).get(j).getY())));
            }
        }

        return clusters;
    }

    //将pointLinkedList中的模式样本分给最近的聚类
    private void step2()
    {
        itrtNum++;

        if (!clusters.isEmpty())
            clusters.clear();
        if (!step8_stdDevitVct.isEmpty())
            step8_stdDevitVct.clear();
        if (!step9_lrgtCpntList.isEmpty())
            step9_lrgtCpntList.clear();
        if (!step11_disBtwnCtrs.isEmpty())
            step11_disBtwnCtrs.clear();
        if (!distanceMatrix.isEmpty())
            distanceMatrix.clear();
        //初始化clusters并将聚类中心放入
        for (int i = 0; i < clusterCenters.size(); i++) {
            clusters.add(new LinkedList<>());
            clusters.get(i).add(clusterCenters.get(i));
        }

        //计算距离矩阵
        for (int i = 0; i < clusterCenters.size(); i++) {
            distanceMatrix.add(new LinkedList<>());
            for (int j = 0; j < pointLinkedList.size(); j++) {
                distanceMatrix.get(i).add(clusterCenters.get(i).calDistance(pointLinkedList.get(j)));
            }
        }

        //将pointLinkedList中的模式样本分给最近的聚类
        for (int j = 0; j < pointLinkedList.size(); j++) {
            double shortestDistance = Double.MAX_VALUE;
            int center = 0;
            for (int i = 0; i < clusterCenters.size(); i++) {
                if (distanceMatrix.get(i).get(j) < shortestDistance)
                {
                    shortestDistance = distanceMatrix.get(i).get(j);
                    center = i;
                }
            }
            clusters.get(center).add(pointLinkedList.get(j));
        }

        step3();
    }

    //若某一聚类中的样本数目小于lstSampNum，则移除该聚类
    private void step3()
    {
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).size() < lstSampNum)
            {
                clusters.remove(i);
                clusterCenters.remove(i);
                distanceMatrix.remove(i);
                initClstNum--;
            }
        }

        step4();
    }

    //更新聚类中心坐标
    private void step4()
    {
        for (int i = 0; i < clusters.size(); i++) {
            int averageX = 0;
            int averageY = 0;
            for (int j = 1; j < clusters.get(i).size(); j++) {
                averageX += clusters.get(i).get(j).getX();
                averageY += clusters.get(i).get(j).getY();
            }
            averageX /= clusters.get(i).size();
            averageY /= clusters.get(i).size();
            clusters.get(i).get(0).setX(averageX);
            clusters.get(i).get(0).setY(averageY);
        }

        step5();
    }

    //计算各聚类中各点到聚类中心的平均距离
    private void step5()
    {
        step5_avgDis = new double[clusterCenters.size()];
        for (int i = 0; i < distanceMatrix.size(); i++) {
            step5_avgDis[i] = 0;
            for (int j = 0; j < distanceMatrix.get(i).size(); j++) {
                step5_avgDis[i] += distanceMatrix.get(i).get(j);
            }
            step5_avgDis[i] /= distanceMatrix.get(i).size();
        }
        step6();
    }

    //计算全部模式样本和其对应聚类中心得总平均距离
    private void step6()
    {
        step6_avgDis = 0;
        for (int i = 0; i < step5_avgDis.length; i++) {
            step6_avgDis += step5_avgDis[i];
        }
        step6_avgDis /= step5_avgDis.length;
        step7();
    }

    private void step7() {
        //若迭代运算次数已达到mstItrtNum次，即最后一次迭代，则置shtstDist为0，转至第十一步
        if (itrtNum > mstItrtNum) {
            shtstDist = 0;
            step11();
        }

        //若迭代运算的次数是偶数次，或初始聚类中心数目多余预期值的两倍，不进行分裂处理，转至第十一步；
        if (itrtNum % 2 == 0 || initClstNum >= 2 * expClstNum) {
            step11();
        }
        //若初始聚类中心的数目小于或等于预期值的一半，则转至第八步，对已有聚类进行分裂处理
        //若既不是偶数次迭代，又不满足上述两倍条件，转至第八步，进行分裂处理。
        if (initClstNum <= expClstNum / 2 || itrtNum % 2 == 1 || initClstNum < 2 * expClstNum) {
            step8();
        }


    }

    //第八步，计算每个聚类中样本距离的标准差向量
    private void step8()
    {
        if (!step8_stdDevitVct.isEmpty())
            step8_stdDevitVct.clear();
        for (int i = 0; i < clusters.size(); i++) {
            double stdDevitX = 0;
            double stdDevitY = 0;
            for (int j = 0; j < clusters.get(i).size(); j++) {
                stdDevitX += Math.abs(clusters.get(i).get(0).getX() - clusters.get(i).get(j).getX())
                            * Math.abs(clusters.get(i).get(0).getX() - clusters.get(i).get(j).getX());
                stdDevitY += Math.abs(clusters.get(i).get(0).getY() - clusters.get(i).get(j).getY())
                            * Math.abs(clusters.get(i).get(0).getY() - clusters.get(i).get(j).getY());;
            }
            stdDevitX /= clusters.get(i).size();
            stdDevitY /= clusters.get(i).size();
            step8_stdDevitVct.add(new Point_double(stdDevitX, stdDevitY));
        }
        step9();
    }

    //第九步，求每一标准差向量中的最大分量
    private void step9()
    {
        //lrgtCpntList中每个元素形式为0/1-lrgtCpnt，0/1用来表示最大分量是x还是y
        double cpntPos = 0;
        double lrgtCpnt = 0;
        double x;
        double y;
        if (!step9_lrgtCpntList.isEmpty())
            step9_lrgtCpntList.clear();
        for (int i = 0; i < step8_stdDevitVct.size(); i++) {
            x = step8_stdDevitVct.get(i).getX();
            y = step8_stdDevitVct.get(i).getY();
            cpntPos = x > y ? 0 : 1;
            lrgtCpnt = x > y ? x : y;
            step9_lrgtCpntList.add(new LinkedList<Double>());
            step9_lrgtCpntList.get(i).add(new Double(cpntPos));
            step9_lrgtCpntList.get(i).add(new Double(lrgtCpnt));
        }
        step10();
    }

//    第十步，在任一最大分量集{σjmax, j = 1, 2, …, Nc}中，若有σjmax>θS ，同时又满足如下两个条件之一：
//    和Nj > 2(θN + 1)，即Sj中样本总数超过规定值一倍以上，
//            2.
//    则将zj 分裂为两个新的聚类中心 和 ，且Nc加1。  中对应于σjmax的分量加上kσjmax，其中 ； 中对应于σjmax的分量减去kσjmax。
//    如果本步骤完成了分裂运算，则转至第二步，否则继续。
    private void step10()
    {

        for (int i = 0; i < step9_lrgtCpntList.size(); i++)
        {
            boolean flag = false;
            if (step9_lrgtCpntList.get(i).get(1) > stdDevitLmt)
            {
                if ((step5_avgDis[i] > step6_avgDis && clusters.get(i).size() > 2 * (lstSampNum + 1))
                        || (clusterCenters.size() <= expClstNum / 2))
                {
                    //分裂，并修改原聚类中心坐标、增加新的聚类坐标
                    //若最大分量在x坐标，则修改x，y保持不动
                    if (step9_lrgtCpntList.get(i).get(0) == 0)
                    {
//                        clusters.get(i).get(0).setX(clusters.get(i).get(0).getX() + lrgtCpntList.get(i).get(1));
                        clusterCenters.get(i).setX(clusterCenters.get(i).getX() + step9_lrgtCpntList.get(i).get(1));
//                        clusters.add(new LinkedList<>());
//                        clusters.get(clusters.size() - 1).add(new Point_double
//                                ((clusters.get(i).get(0).getX() - lrgtCpntList.get(i).get(1)), clusters.get(i).get(0).getY()));
                        clusterCenters.add(new Point_double
                                ((clusterCenters.get(i).getX() - step9_lrgtCpntList.get(i).get(1)), clusterCenters.get(i).getY()));
                        flag = true;
//                        for (Point_double point: clusters.get(i)) {
//                            double disFromOld = Math.abs(point.getX() - clusters.get(i).get(0).getX());
//                            double disFromNew = Math.abs(point.getX() - clusters.get(clusters.size() - 1).get(0).getX());
//                            if (disFromNew < disFromOld)
//                            {
//                                clusters.get(clusters.size() - 1).add(point);
//                                clusters.get(i).remove(point);
//                            }
//                        }
                    }
                    //若最大分量在y坐标，则修改y，x保持不动
                    if (step9_lrgtCpntList.get(i).get(0) == 1)
                    {
//                        clusters.get(i).get(0).setY(clusters.get(i).get(0).getY() + lrgtCpntList.get(i).get(1));
                        clusterCenters.get(i).setY(clusterCenters.get(i).getY() + step9_lrgtCpntList.get(i).get(1));
//                        clusters.add(new LinkedList<>());
//                        clusters.get(clusters.size() - 1).add(new Point_double
//                                (clusters.get(i).get(0).getX(), clusters.get(i).get(0).getY() - lrgtCpntList.get(i).get(1)));
                        clusterCenters.add(new Point_double
                                (clusterCenters.get(i).getX(), clusterCenters.get(i).getY() - step9_lrgtCpntList.get(i).get(1)));
                        flag = true;
//                        for (Point_double point: clusters.get(i)) {
//                            double disFromOld = Math.abs(point.getY() - clusters.get(i).get(0).getY());
//                            double disFromNew = Math.abs(point.getY() - clusters.get(clusters.size() - 1).get(0).getY());
//                            if (disFromNew < disFromOld)
//                            {
//                                clusters.get(clusters.size() - 1).add(point);
//                                clusters.get(i).remove(point);
//                            }
//                        }
                    }
                }

            }
            if (flag == true)
                step2();
        }
        step11();
    }

    //计算全部聚类中心之间的距离
    private void step11()
    {
        for (int i = 0; i < clusterCenters.size(); i++) {
            step11_disBtwnCtrs.add(new LinkedList<>());
            for (int j = 0; j < clusterCenters.size(); j++) {
                step11_disBtwnCtrs.get(i).add(clusterCenters.get(i).calDistance(clusterCenters.get(j)));
            }
        }
        step12and13();
    }

    //第十二步，比较disBtwnCtrs和shtstDist，将disBtwnCtrs < shtstDist的值按最小距离次序递增排列
    //第十三步，将彼此之间距离小于shtstDist的两个聚类中心合并
    private void step12and13()
    {
        Point_double newClstrCntr = new Point_double();
        double x = 0;
        double y = 0;

        for (int i = 0; i < step11_disBtwnCtrs.size(); i++) {
            for (int j = 0; j < step11_disBtwnCtrs.get(i).size(); j++) {
                if (i > j && step11_disBtwnCtrs.get(i).get(j) < shtstDist)
                {
                    x = clusters.get(i).size() * clusters.get(i).get(0).getX() + clusters.get(j).size() * clusters.get(j).get(0).getX();
                    x /= (clusters.get(i).size() + clusters.get(j).size());
                    y = clusters.get(i).size() * clusters.get(i).get(0).getY() + clusters.get(j).size() * clusters.get(j).get(0).getY();
                    y /= (clusters.get(i).size() + clusters.get(j).size());
                    newClstrCntr.setX(x);
                    newClstrCntr.setY(y);
//                    clusters.get(i).get(0).setX(x);
//                    clusters.get(i).get(0).setY(y);
//                    clusters.remove(j);
                    clusterCenters.get(i).setX(x);
                    clusterCenters.get(i).setY(y);
                    clusterCenters.remove(j);
                    initClstNum--;
                }
            }
        }
        step14();
    }

    private void step14()
    {
        if (itrtNum >= mstItrtNum)
            return;
        step2();
    }
}
