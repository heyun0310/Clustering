package Clustering.Utils;

public class Point {

    private int x;
    private int y;

    public Point()
    {
        x = 0;
        y = 0;
    }

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setX(int x)
    {
        this.x=x;
    }

    public void setY(int y)
    {
        this.y=y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public double calDistance(Object obj)
    {
        double distance;
        Point point = (Point)obj;
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
