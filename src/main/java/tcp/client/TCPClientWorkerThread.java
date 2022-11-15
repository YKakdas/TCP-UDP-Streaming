package tcp.client;

import data.FrameInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClientWorkerThread extends Thread {

    private String serverAddress;
    private int serverPort;

    private int width;
    private int height;

    public TCPClientWorkerThread(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    // Simple TCP Client, sends and receives a string
    @Override
    public void run() {
        Socket socket;
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        JLabel lbl = new JLabel();
        frame.add(lbl);
        try {

            socket = new Socket(InetAddress.getByName(serverAddress), serverPort);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            frame.setSize(1024, 720);

            while (true) {
                FrameInfo frameInfo = (FrameInfo) input.readObject();

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
