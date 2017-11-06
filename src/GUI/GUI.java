package GUI;

import Clustering.NearNeighborClustering;
import Clustering.Point;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class GUI extends JFrame{

    private final JLabel jLabel = new JLabel();
    private final JTextField textField = new JTextField();
    private final JButton button = new JButton();
    private NearNeighborClustering nearNeighborClustering = new NearNeighborClustering(3);

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

        textField.setFont(new Font("宋体",Font.PLAIN, 20));
        add(textField);

        button.setText("聚类");
        button.setFont(new Font("宋体",Font.PLAIN, 20));
        add(button);

        layout.setConstraints(jLabel, constraints);
        layout.setConstraints(textField, constraints);
        layout.setConstraints(button, constraints);

        setLayout(layout);

        TextFieldHandler handler = new TextFieldHandler();
        button.addActionListener(handler);
    }

    private class TextFieldHandler implements ActionListener
    {
        LinkedList<Point> pointLinkedList = new LinkedList<Point>();
        String output = new String("");

        @Override
        public void actionPerformed(ActionEvent event)
        {
            if (event.getSource() == button)
            {
                String getInput = textField.getText();
                getInput = getInput.replace("(","");
                getInput = getInput.replace(")","");
                String[] strArray = getInput.split(",");

                for(int i = 0; i < strArray.length; i+= 2) {
                    Point point = new Point();
                    point.setX(Integer.parseInt(strArray[i]));
                    point.setY(Integer.parseInt(strArray[i + 1]));
                    pointLinkedList.add(point);
                }

                for (int i = 0; i < pointLinkedList.size(); i++)
                {
                    nearNeighborClustering.cluster(pointLinkedList.get(i));
                }

                for (int i = 0; i < nearNeighborClustering.cluster.size(); i++) {

                    output = output + "第" + i + "个聚类中的元素有：";

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
        }
    }
}
