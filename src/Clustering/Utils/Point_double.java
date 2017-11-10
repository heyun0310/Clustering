package Clustering.Utils;

public class Point_double {

    private double x;
    private double y;

    public Point_double()
    {
        x = 0;
        y = 0;
    }

    public Point_double(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void setX(double x)
    {
        this.x=x;
    }

    public void setY(double y)
    {
        this.y=y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double calDistance(Object obj)
    {
        double distance;
        Point_double point = (Point_double) obj;
        distance = Math.sqrt(Math.abs(point.x - this.x) * Math.abs(point.x - this.x) + Math.abs(point.y - this.y) * Math.abs(point.y - this.y));
        return distance;
    }

    public double calDistanceWithPointAndCluster(Object... objects)
    {
        double distance = 0;
        for (Object obj:objects) {
            distance += this.calDistance(obj);
        }
        distance /= objects.length;
        return distance;
    }

    public String toString(){
        String result=null;
        result= "x:" + x + "y:" + y;
        return result;
    }
}
