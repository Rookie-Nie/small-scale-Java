package Yang;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/*
此文件为最终代码
 */
public class Yang {
    public static void main(String[] args) {
        JFrame frame = new JFrame();//创建一个窗口（画框）
        JPanel panel = new JPanel();//创建一个面板（画板）

        panel.setLayout(null);//取面板默认布局，但是需要自己指定大小和位置
        //创建一个表示图片的零件（括号中是图片的路径）
        JLabel background = new JLabel(new ImageIcon("img/背景.jpg"));
        background.setSize(480,800);//设置图片大小
        background.setLocation(0,0);//设置图片位置（0,0）表示左上角
        panel.add(background);//将图片添加到画板中

        //循环将144张卡牌添加到面板上
        ArrayList<JButton> cards = createCards();

        Collections.shuffle(cards);//洗牌（将cards中的元素打乱）

        //将cards集合中的卡牌取出，并画到画板上
        //第一次---------发第一层的49张牌
        putCards(cards,panel,49,0,7,30,100);
        //第二次---------发第二层的36张牌
        putCards(cards,panel,36,49,6,60,130);
        //第三次---------发第三层的35张牌
        putCards(cards,panel,35,85,7,30,160);
        //第四次---------发第四层(最上层)的24张牌
        putCards(cards,panel,24,120,6,60,190);

        checkCovered(cards);//发牌后，检查发牌情况

        //添加点击事件
        ArrayList<JButton> belowCards = new ArrayList<>();//准备一个集合，存放卡槽里的卡牌
        addClickAction(cards,belowCards,panel);//为每张卡牌添加点击事件

        frame.add(panel);//将面板装入窗口中
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//点窗口的叉时，退出程序
        frame.setSize(496,840);//根据背景图片大小设置窗口大小
        frame.setLocationRelativeTo(null);//设置窗口相对于桌面居中显示
        frame.setResizable(false);//设置窗口不能调整大小
        frame.setVisible(true);//将窗口显示出来
    }


    //该方法用于给每张卡牌添加点击事件
    public static void addClickAction(ArrayList<JButton> cards,ArrayList<JButton> belowCards,JPanel panel){
        for(int i = 0;i< cards.size();i++){//遍历所有卡牌
            JButton card = cards.get(i);//获取下标120的卡牌
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {//点击卡牌时自动执行
                    if(belowCards.size()<7) {
                        JButton current = (JButton) e.getSource();//获取被点击的按钮
                        String name = current.getName();//获取被点击的按钮的名字
                        //System.out.println(name+"被点击了");

                /*
                1、将这张卡牌从cards中删除
                2、将这张卡牌存到下面belowCards卡槽中
                3、将belowCards的元素显示在卡槽中
                 */
                        cards.remove(current);//将当前卡牌从cards中删除

                        int num = belowCards.size();//想插入卡牌的位置
                        for(int index = 0;index< belowCards.size();index++){//遍历卡槽中所有的卡牌
                            if(belowCards.get(index).getName().equals(name)){//判断卡槽中的卡牌是否与点击的卡牌相同
                                num = index;//设置要插入的卡牌位置
                                break;
                            }
                        }

                        belowCards.add(num,current);//将当前卡牌存到下面belowCards卡槽中

                        //若num位置后面的第二个位置的卡牌的名字与当前卡牌的名字相同，则说明有三张相同的卡牌，需要将他们从卡槽消除
                        if(num+2< belowCards.size()) {
                            if (belowCards.get(num + 2).getName().equals(name)) {
                                //删除num下标的卡牌，同时将被删除的卡牌存到c1中
                                for(int i = 0; i<3 ; i++) {
                                    JButton c1 = belowCards.remove(num);
                                    panel.remove(c1);
                                }

                            }
                        }

                        panel.updateUI();//刷新窗口，若不刷新可能删除的卡牌还在卡槽中（窗口自身问题）

                        for (int i = 0; i < belowCards.size(); i++) {//遍历所有卡槽中的卡牌
                            belowCards.get(i).setLocation(25 + i * 62, 640);//设置卡槽中卡牌的位置
                        }
                        //将当前按钮的点击事件删除（卡槽中的按钮不能点击了）
                        current.removeActionListener(current.getActionListeners()[0]);
                        checkCovered(cards);//重新检查卡牌是否被压住
                    }
                    else {//卡槽满，游戏结束
                        JOptionPane.showMessageDialog(panel,"卡槽已满，游戏结束");
                        //将来可以加入看广告，或者存钱解锁功能
                    }


                /*
                待解决的问题：
                1.下面卡槽中的按钮还能点击，怎么设计为不能点击的？（已解决）
                2.移动卡牌后，当原本被压住的卡牌不再被压住时，应该被点亮（已解决）
                3.设计卡槽中最多只能放7个卡牌（已解决）
                4.卡槽中有三个一样的卡牌时，并没有消除
                 */


                }
            });
        }}

    //该方法用于144张卡牌的压牌情况
    public static void checkCovered(ArrayList<JButton> cards){
        for(int n=0;n< cards.size();n++){//遍历所有卡牌
            JButton bottom = cards.get(n);//获取下标n这张卡牌
            for(int i=n+1;i< cards.size();i++){//遍历bottom后面的卡牌
                JButton top = cards.get(i);//获取bottom后面的卡牌
                boolean covered = covered(top,bottom);//检测是否被压住
                if(covered){
                    bottom.setEnabled(false);
                    break;//检测到被压住后，后面就不走了，若不break则后面的卡牌又会将它点亮
                }
                else
                    bottom.setEnabled(true);
            }
        }

    }

    //该方法用于检测上面的卡牌top是否压住了下面的卡牌bottom
    public static boolean covered(JButton top,JButton bottom){
        int x1 = bottom.getX()-59;
        int x2 = bottom.getX()+59;
        int y1 = bottom.getY()-60;
        int y2 = bottom.getY()+60;
        int x = top.getX();
        int y = top.getY();

        return x>x1 && x<x2 && y>y1 && y<y2;//若不满足此处条件，则视为压住了
    }

    //用于将144张卡牌存储到集合中
    public static ArrayList<JButton> createCards(){
        ArrayList<JButton> cards = new ArrayList<>();//卡牌集合
        String[] names = {"刷子","剪刀","南瓜","奶瓶","干草","手套","树","树桩",
                "毛线","水桶","火堆","白菜","苹果","草","萝卜","铃铛"};
        for(int j = 0;j<9;j++) {        //循环9次，生成144张卡牌
            for (int i = 0; i < names.length; i++) {
                String name = "img/" + names[i] + ".png";
                JButton card = new JButton(new ImageIcon(name));
                card.setName(names[i]);//设置卡牌名称

                //设置按钮不可用时显示的图片（用于显示被压住的卡牌）
                String name2 = "img/"+names[i]+"2.png";//获取不可用图片卡牌路径
                card.setDisabledIcon(new ImageIcon(name2));//设置不可用图片
                //card.setEnabled(false);//设置按钮不可用（仅用于实验）

                cards.add(card);//将卡牌添加到cards中备用
            }
        }
        return cards;
    }

    //该方法用于摆放一层牌（发牌操作）
    /*
    ArrayList<JButton> cards----卡牌集合
    JPanel panel----------------要装入的画板
    count-----------------------这一层要装入的卡牌数
    offset----------------------这一层的卡牌从集合的第几张开始摆
    cols------------------------这一层一行有几张卡牌
    x---------------------------第一张卡牌的x坐标
    y---------------------------第一张卡牌的y坐标
     */
    public static void putCards(ArrayList<JButton> cards,JPanel panel,int count,int offset,int cols,int x,int y){
        for(int i = 0;i< count;i++){
            JButton card = cards.get(i+offset);
            card.setSize(59,66);//设置卡牌大小
            card.setLocation(x+(i%cols)*59,y+(i/cols)*60);//设置卡牌位置
            card.setBorderPainted(false);//设置是否绘制按钮边框，false（不绘制）
            card.setContentAreaFilled(false);//设置按钮区域是否填充颜色，false（不填充）
            panel.add(card,0);//将卡牌添加到面板上
        }
    }


}
