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
        frame.setTitle("Client");
        frame.add(lbl);
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress ipAddress = InetAddress.getByName("localhost");

            frame.setSize(720, 540);

            DatagramPacket initPacket = new DatagramPacket("init".getBytes(), "init".getBytes().length, ipAddress, 1234);
            socket.send(initPacket);

            ByteArrayInputStream baos;
            ObjectInputStream oos;
            while (true) {
                try {
                    byte[] buffer = new byte[76];
                    DatagramPacket metadataPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(metadataPacket);

                    baos = new ByteArrayInputStream(buffer);
                    oos = new ObjectInputStream(baos);
                    UDPDatagramInfo udpDatagramInfo = (UDPDatagramInfo) oos.readObject();

                    byte[] frameData = new byte[udpDatagramInfo.getSize()];
                    int count = 0;
                    for (int i = 0; i < udpDatagramInfo.getNumberOfFragments(); i++) {
                        byte[] segmentBuffer = new byte[64000];
                        DatagramPacket segmentPacket = new DatagramPacket(segmentBuffer, segmentBuffer.length);
                        socket.receive(segmentPacket);
                        ByteUtil.mergeArrays(ByteUtil.splitArray(segmentPacket.getData(), segmentPacket.getLength()), frameData, count);
                        count += segmentPacket.getLength();
                    }

                    baos = new ByteArrayInputStream(frameData);
                    oos = new ObjectInputStream(baos);
                    FrameInfo frameInfo = (FrameInfo) oos.readObject();

                    if (frameInfo.getFrameNum() == -1) {
                        socket.close();
                        return;
                    }

                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(frameInfo.getData()));

                    if (image != null) {
                        ImageIcon icon = new ImageIcon(image);
                        lbl.setIcon(icon);
                        frame.setVisible(true);
                    }
                } catch (Exception e) {
                    //  e.printStackTrace();
                }

            }
        } catch (Exception e) {
            //   e.printStackTrace();
        }

    }
}
