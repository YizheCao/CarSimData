import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Copyright (C), 2019, CaoYizhe
 * FileName: Receive.java
 * 服务器端，接收UDP通信数据并显示
 *
 * @author 曹奕哲
 * @Date   2019年7月10日
 * @version 1.0
 */
public class Receive extends Thread{
    public int port = 8081;
    JPanel panel = null;
    JTextField textField = null;
    JLabel label_ = null;
    JScrollPane scrollPane = null;
    JTextArea textArea = null;

    /**
     * 构造函数，构造服务器端图形化界面，监听端口、接收数据并在图形化界面显示
     */
    public Receive(){
        JFrame frame = new JFrame("服务器");

        textField = new JTextField("8081");
        label_ = new JLabel("端口号：");
        textArea = new JTextArea();

        scrollPane = new JScrollPane(textArea);
        panel = new JPanel();
        panel.add(label_);
        panel.add(textField);

        frame.setSize(400, 300);
        frame.add(panel,BorderLayout.NORTH);
        frame.add(scrollPane,BorderLayout.CENTER);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int)(toolkit.getScreenSize().getWidth() - frame.getWidth())/2;
        int y = (int)(toolkit.getScreenSize().getHeight() - frame.getHeight())/2;
        frame.setLocation(x, y);
        frame.setVisible(true);

        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(port);
            byte[] buf = new byte[1024];

            while(true) {
                DatagramPacket datagramPacket = new DatagramPacket(buf, 0, buf.length);
                datagramSocket.receive(datagramPacket);

                String data = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                String str = null;
                if (textArea.getText() == null || "".equals(textArea.getText())) {
                    str = data;
                } else {
                    str = textArea.getText() + "\r\n" + data;
                }
                textArea.setText(str);
                textArea.setCaretPosition(textArea.getDocument().getLength());
                //System.out.println(data);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            datagramSocket.close();
        }
    }

    /**
     * 主函数
     *
     * @param args 接收命令行参数
     */
    public static void main(String[] args) {
        new Receive();
    }
}
