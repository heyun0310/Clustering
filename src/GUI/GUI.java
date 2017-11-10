package GUI;

import Clustering.HierarchicalClustering.DisjointSets;
import Clustering.HierarchicalClustering.HierarchicalClustering;
import Clustering.ISODATAClustering.ISODATAClustering;
import Clustering.KMeansClustering.KMeansClustering;
import Clustering.NearNeighborClustering.NearNeighborClustering;
import Clustering.Utils.Point;
import Clustering.Utils.Point_double;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class GUI extends JFrame{

    private final JLabel jLabel = new JLabel();
    private final JTextArea textArea = new JTextArea(8,50);
    private final JButton getInputFromFileButton = new JButton();
    private final JButton nearNeighborButton = new JButton();
    private final JButton hierarchicalButton = new JButton();
    private final JButton kmeansButton = new JButton();
    private final JButton isodataButton = new JButton();

    private NearNeighborClustering nearNeighborClustering = new NearNeighborClustering(3);
    private HierarchicalClustering hierarchicalClustering;

    public GUI()
    {
        super("Near Neighbor Clustering");
        //使用GridBagLayout布局管理器
        GridBagLayout layout = new GridBagLayout();
        //用于设置此布局中指定组件的约束条件
        GridBagConstraints constraints = new GridBagConstraints();
        //若组件所在的区域比组件本身要大时则使组件完全填满其显示区域
        constraints.fill = GridBagConstraints.BOTH;
        //设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        constraints.gridwidth = 0;
        //设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        constraints.weightx = 0;
        //设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        constraints.weighty = 0;
        //组件彼此之间的距离，参数分别为上左下右
        constraints.insets = new Insets(20,20,20,20);

        jLabel.setText("请输入点坐标值(如：（1,1))：");
        jLabel.setFont(new Font("宋体",Font.PLAIN, 20));
        add(jLabel);

        textArea.setFont(new Font("宋体",Font.PLAIN, 20));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(textArea);
//        add(new JScrollPane(textArea));

        getInputFromFileButton.setText("填充数值");
        getInputFromFileButton.setFont(new Font("宋体",Font.PLAIN, 20));
        add(getInputFromFileButton);

        nearNeighborButton.setText("近邻聚类");
        nearNeighborButton.setFont(new Font("宋体",Font.PLAIN, 20));
        add(nearNeighborButton);

        hierarchicalButton.setText("层次聚类");
        hierarchicalButton.setFont(new Font("宋体",Font.PLAIN,20));
        add(hierarchicalButton);

        kmeansButton.setText("K-Means聚类");
        kmeansButton.setFont(new Font("宋体",Font.PLAIN,20));
        add(kmeansButton);

        isodataButton.setText("ISODATA聚类");
        isodataButton.setFont(new Font("宋体",Font.PLAIN,20));
        add(isodataButton);

        layout.setConstraints(jLabel, constraints);
        layout.setConstraints(textArea, constraints);
        layout.setConstraints(getInputFromFileButton, constraints);
        layout.setConstraints(nearNeighborButton, constraints);
        layout.setConstraints(hierarchicalButton, constraints);
        layout.setConstraints(kmeansButton, constraints);
        layout.setConstraints(isodataButton, constraints);

        setLayout(layout);

        TextFieldHandler handler = new TextFieldHandler();
        getInputFromFileButton.addActionListener(handler);
        nearNeighborButton.addActionListener(handler);
        hierarchicalButton.addActionListener(handler);
        kmeansButton.addActionListener(handler);
        isodataButton.addActionListener(handler);
    }

    private class TextFieldHandler implements ActionListener
    {
        LinkedList<Point> pointLinkedList = new LinkedList<Point>();
        String output = new String("");

        @Override
        public void actionPerformed(ActionEvent event)
        {
            if (event.getSource() == nearNeighborButton)
            {
                pointLinkedList = getInputFromClient();

                for (int i = 0; i < pointLinkedList.size(); i++)
                {
                    nearNeighborClustering.cluster(pointLinkedList.get(i));
                }

                for (int i = 0; i < nearNeighborClustering.cluster.size(); i++) {

                    output = output + "第" + i + "个聚类中的元素有：\n";

                    for (int j = 0; j < nearNeighborClustering.cluster.get(i).size(); j++) {
                        output = output + "(" + nearNeighborClustering.cluster.get(i).get(j).getX() + "," + nearNeighborClustering.cluster.get(i).get(j).getY() + ")" + ",";
                        output = output.substring(0,output.length()-1);
                        output = output + "\n";
                    }

                }
                // 设置按钮显示效果
                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                // 设置文本显示效果
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                JOptionPane.showMessageDialog(null,output);
            }
            else if (event.getSource() == hierarchicalButton)
            {
                pointLinkedList = getInputFromClient();
                String inputThreshold;
                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                inputThreshold = JOptionPane.showInputDialog("请输入阈值：\n","2");
                double threshold = Double.parseDouble(inputThreshold);

                hierarchicalClustering = new HierarchicalClustering(pointLinkedList, threshold);
                hierarchicalClustering.init();
                hierarchicalClustering.cluster();

                //遍历聚类结果并显示
                DisjointSets disjointSets = hierarchicalClustering.getDisjointSets();
                Map<Integer, ArrayList<Point>> map = hierarchicalClustering.getResult();
                Set<Integer> keys = map.keySet();

                String result = new String("");
                for (int i = 0; i < keys.size(); i++) {
                    result += "第" + i + "个聚类中的元素有：\n";
                    for (Point point: map.get(disjointSets.find(i))) {
                        result += "(" + point.getX() + "," + point.getY() + ")" + ",";
                        result = result.substring(0,result.length()-1);
                        result = result + "\n";
                    }
                }
                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                JOptionPane.showMessageDialog(null, result);
            }
            else if (event.getSource() == getInputFromFileButton)
            {
                String filePath = ".\\rsc\\data.txt";
                File file = new File(filePath);
                FileReader fileReader;
                String lineText;
                String text = "";
                try {
                    fileReader = new FileReader(filePath);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
                    while ((lineText = bufferedReader.readLine()) != null)
                        text += lineText;
                    bufferedReader.close();
                    fileReader.close();
                    textArea.setText(text);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (event.getSource() == kmeansButton)
            {
                LinkedList<Point> seedsList = new LinkedList<Point>();
                String outputMessage = new String("");

                //用户输入种子点坐标
                String inputSeeds;
                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                inputSeeds = JOptionPane.showInputDialog("请输入种子点坐标(如(1,1),(3,4)：\n","(1,1),(3,4)");
                inputSeeds = inputSeeds.replace("(","");
                inputSeeds = inputSeeds.replace(")","");
                String[] seedsArray = inputSeeds.split(",");

                for(int i = 0; i < seedsArray.length; i+= 2) {
                    Point point = new Point();
                    point.setX(Integer.parseInt(seedsArray[i]));
                    point.setY(Integer.parseInt(seedsArray[i + 1]));
                    seedsList.add(point);
                }

                //创建KMeansClustering对象调用函数得到结果
                pointLinkedList = getInputFromClient();
                KMeansClustering kMeansClustering = new KMeansClustering(seedsList, pointLinkedList);
                LinkedList<LinkedList<Point>> pointsClassifiedBySeeds = kMeansClustering.cluster();
                for (int i = 0; i < pointsClassifiedBySeeds.size(); i++) {
                    outputMessage += "第" + i + "个聚类中的元素有：\n";
                    for (Point point:pointsClassifiedBySeeds.get(i)) {
                        outputMessage += "(" + point.getX() + "," + point.getY() + ")" + ",";
                        outputMessage = outputMessage.substring(0,outputMessage.length()-1);
                        outputMessage = outputMessage + "\n";
                    }
                }
                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                JOptionPane.showMessageDialog(null, outputMessage);
            }
            else if (event.getSource() == isodataButton)
            {
                //确定聚类中心
                String inputClusterCenter;
                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                inputClusterCenter = JOptionPane.showInputDialog("请从样本中选取坐标作为聚类中心（输入索引如0,9,18,29)：\n","0,9,18,29");
                String[] clusterCenters = inputClusterCenter.split(",");
                int[] clusterCentersindex = new int[clusterCenters.length];
                for (int i = 0; i < clusterCenters.length; i++) {
                    clusterCentersindex[i] = Integer.parseInt(clusterCenters[i]);
                }
                pointLinkedList = getInputFromClient();

                //调用ISODATAClustering
                ISODATAClustering isodataClustering = new ISODATAClustering(pointLinkedList, clusterCentersindex,
                        4,5, 5, 5, 10,10 );
                LinkedList<LinkedList<Point_double>> result = isodataClustering.isodataCluster();

                String outputMessage = new String("");
                for (int i = 0; i < result.size(); i++) {
                    outputMessage += "第" + i + "个聚类中的元素有：\n";
                    for (Point_double point:result.get(i)) {
                        outputMessage += "(" + point.getX() + "," + point.getY() + ")" + ",";
                    }
                    outputMessage = outputMessage.substring(0,outputMessage.length()-1);
                    outputMessage = outputMessage + "\n";
                }
                UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                JOptionPane.showMessageDialog(null, outputMessage);
            }
        }

        //用于获取用户输入，并将该字符串格式化为Point类型
        private LinkedList<Point> getInputFromClient()
        {
            LinkedList<Point> pointLinkedList = new LinkedList<Point>();

            String getInput = textArea.getText();
            getInput = getInput.replace("(","");
            getInput = getInput.replace(")","");
            String[] strArray = getInput.split(",");

            for(int i = 0; i < strArray.length; i+= 2) {
                Point point = new Point();
                point.setX(Integer.parseInt(strArray[i]));
                point.setY(Integer.parseInt(strArray[i + 1]));
                pointLinkedList.add(point);
            }

            return pointLinkedList;
        }
    }
}
