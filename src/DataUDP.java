import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Copyright (C), 2019, CaoYizhe
 * FileName: DataUDP.java
 * 客户端，通过UDP通信发送数据
 *
 * @author 曹奕哲
 * @Date   2019年7月10日
 * @version 1.0
 */
public class DataUDP extends JFrame implements ActionListener {
    /** 速度，转角信息 */
    public float speed = 0;
    public float steer = 0;
    /** 发送的数据包 */
    public String data = null;
    /** 套接字，端口，IP地址 */
    public DatagramSocket dataSocket = null;
    public int port = 8080;
    public String ipAddress = "localhost";
    /** 判断是否停止发送数据标志 */
    public boolean isStart = false;
    /** 线程 */
    public Thread thread = null;
    /** IP地址，端口号文本框 */
    JTextField textField1 = null;
    JTextField textField2 = null;
    JTextField textField3 = null;
    JTextField textField4 = null;
    JTextField textField = null;
    /** 静态文本 */
    JLabel label = null;
    JLabel label1 = null;
    JLabel label2 = null;
    JLabel label3 = null;
    JLabel label_ = null;
    /** 面板容器 */
    JPanel panel = null;
    /** “开始发送”，“结束发送”按钮 */
    JButton start = null;
    JButton end = null;
    /** 读数据TXT地址 */
    String filePath = "D:\\data.txt";

    /**
     * 构造函数，构造客户端图形化界面
     */
    public DataUDP(){
        start = new JButton("开始发送");
        start.addActionListener(this);
        end = new JButton("结束发送");
        end.addActionListener(this);

        textField1 = new JTextField("192");
        textField2 = new JTextField("168");
        textField3 = new JTextField("1");
        textField4 = new JTextField("165");
        textField = new JTextField("8080");

        label = new JLabel("IP地址：");
        label1 = new JLabel(".");
        label2 = new JLabel(".");
        label3 = new JLabel(".");
        label_ = new JLabel("端口号：");

        panel = new JPanel();
        panel.setLayout(null);
        start.setBounds(80,180,90,30);
        end.setBounds(220,180,90,30);
        label.setBounds(40,40,80,20);
        label1.setBounds(150,40,80,20);
        label2.setBounds(215,40,80,20);
        label3.setBounds(280,40,80,20);
        label_.setBounds(40,100,80,20);
        textField1.setBounds(100,40,40,20);
        textField2.setBounds(165,40,40,20);
        textField3.setBounds(230,40,40,20);
        textField4.setBounds(295,40,40,20);
        textField.setBounds(100,100,40,20);
        panel.add(textField1);
        panel.add(textField2);
        panel.add(textField3);
        panel.add(textField4);
        panel.add(textField);
        panel.add(label);
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(label_);
        panel.add(start);
        panel.add(end);

        this.setTitle("客户端");
        this.setSize(400, 300);
        this.add(panel,BorderLayout.CENTER);
        /* 图形化界面居中 */
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int)(toolkit.getScreenSize().getWidth() - this.getWidth()) / 2;
        int y = (int)(toolkit.getScreenSize().getHeight() - this.getHeight()) / 2;
        this.setLocation(x, y);
        this.setVisible(true);
    }

    /**
     * 监听按钮事件
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == start) {
            if(thread == null){
                thread = new Thread(new sendData());
                thread.start();
            }else{
                synchronized(thread) {
                    thread.notify();
                }
            }
            isStart = true;
        }
        else if(e.getSource() == end){
            isStart = false;
        }
    }

    /**
     * 内部类，发送数据线程，监听端口、读取TXT数据并发送
     */
    private class sendData implements Runnable {
        public void run(){
            /** 字符输入流 */
            BufferedReader br = null;
            String line = "";
            String str = "";

            ipAddress = textField1.getText() + "." + textField2.getText() + "." + textField3.getText() + "." + textField4.getText();
            //System.out.println(ipAddress);
            port = Integer.parseInt(textField.getText());
            //System.out.println(port);

            try {
                br = new BufferedReader(new FileReader(filePath));

                dataSocket = new DatagramSocket(port);
                InetAddress destination = InetAddress.getByName(ipAddress);

                while((line = br.readLine()) != null) {
                    //line = br.readLine();
                    str = " "+ line;
                    String[] dictionary = str.split(" ");
                    speed = Float.parseFloat(dictionary[1]);
                    steer = Float.parseFloat(dictionary[2]);
                    //System.out.println(speed);
                    //System.out.println(steer);
                    data = speed + "," + steer;

                    DatagramPacket datagramPacket = new DatagramPacket(data.getBytes(), data.getBytes().length, destination, 8081);
                    dataSocket.send(datagramPacket);

                    synchronized (thread) {
                        try { Thread.sleep(1000);
                            if(!isStart) {
                                thread.wait();
                            }
                        }
                        catch(InterruptedException e) {
                            System.out.println("当前线程被中断");
                            break;
                        }
                    }
                }
                br.close();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                dataSocket.close();
            }
        }
    }

    /**
     * 主函数
     *
     * @param args 接收命令行参数
     */
    public static void main(String[] args) {
        new DataUDP();
    }
}
