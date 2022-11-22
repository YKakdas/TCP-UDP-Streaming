package udp.client;

import data.FrameInfo;
import data.UDPDatagramInfo;
import util.ByteUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientWorkerThread extends Thread {

    private int width;
    private int height;


    // Simple UDP Client, sends and receives a string
    @Override
    public void run() {

        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        JLabel lbl = new JLabel();
        frame.add(lbl);
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress ipAddress = InetAddress.getByName("localhost");
            //ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            frame.setSize(1024, 720);

            DatagramPacket packet2 = new DatagramPacket("Dummy".getBytes(), "Dummy".getBytes().length, ipAddress, 1234);
            socket.send(packet2);

            while (true) {
                try {
                    byte[] buffer = new byte[76];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    System.out.println("Received info " + packet.getLength() );
                    ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
                    ObjectInputStream oos = new ObjectInputStream(baos);
                    UDPDatagramInfo udpDatagramInfo = (UDPDatagramInfo) oos.readObject();

                    byte[] total = new byte[udpDatagramInfo.getSize()];
                    int count = 0;
                    for (int i = 0; i < udpDatagramInfo.getNumberOfFragments(); i++) {
                        buffer = new byte[64000];
                        packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        ByteUtil.mergeArrays(ByteUtil.splitArray(packet.getData(), packet.getLength()), total, count);
                        count += packet.getLength();
                        System.out.println("Received bytes");
                    }

                    ByteArrayInputStream baos2 = new ByteArrayInputStream(total);
                    ObjectInputStream oos2 = new ObjectInputStream(baos2);
                    FrameInfo frameInfo = (FrameInfo) oos2.readObject();

                    if (frameInfo.getSize() == -1) {
                        socket.close();
                        return;
                    }

                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(frameInfo.getData()));

                    if (image != null) {
                        ImageIcon icon = new ImageIcon(image);
                        lbl.setIcon(icon);
                        frame.setVisible(true);
                    }
                }catch (Exception e){
                  //  e.printStackTrace();
                }

            }
        } catch (Exception e) {
         //   e.printStackTrace();
        }

    }
}
