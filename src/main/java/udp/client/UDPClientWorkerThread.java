package udp.client;

import data.FrameInfo;
import data.UDPDatagramInfo;
import util.ByteUtil;
import util.FrameUtil;
import util.ImageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.*;

public class UDPClientWorkerThread extends Thread {

    private int width;
    private int height;

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
            socket.setSoTimeout(3000);
            frame.setSize(720, 540);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DatagramPacket initPacket = new DatagramPacket("init".getBytes(), "init".getBytes().length, ipAddress, 1234);
            socket.send(initPacket);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("Client terminated streaming.");
                    socket.close();
                    e.getWindow().dispose();
                    System.exit(0);
                }
            });

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
                        byte[] segmentBuffer = new byte[1024];
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
                        if (FrameUtil.isCamera) {
                            image = ImageUtil.mirror(image);
                        }
                        ImageIcon icon = new ImageIcon(image);
                        lbl.setIcon(icon);
                        frame.setVisible(true);
                    }
                } catch (Exception e) {
                    if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                        System.out.println("Streaming completed.");
                        socket.close();
                        System.exit(0);
                    }
                }

            }
        } catch (Exception e) {
            if (e instanceof EOFException) {
                System.out.println("Streaming completed.");
             //   System.exit(0);
            }else {
                e.printStackTrace();
            }
        }

    }
}
