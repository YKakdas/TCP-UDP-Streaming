package udp.client;

import data.FrameInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class UDPClientWorkerThread extends Thread {

    private String serverAddress;
    private int serverPort;

    private int width;
    private int height;

    public UDPClientWorkerThread(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    // Simple UDP Client, sends and receives a string
    @Override
    public void run() {

        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        JLabel lbl = new JLabel();
        frame.add(lbl);
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(this.serverAddress);
            //ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            frame.setSize(1024, 720);

            while (true) {
                FrameInfo frameInfo = (FrameInfo) input.readObject();

                if (frameInfo.getSize() == -1) {
                    socket.close();
                    return;
                }

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(frameInfo.getData()));

                if (image != null) {
                    Thread.sleep(50);
                    ImageIcon icon = new ImageIcon(image);
                    lbl.setIcon(icon);
                    frame.setVisible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
