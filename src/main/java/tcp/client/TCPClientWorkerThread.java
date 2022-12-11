package tcp.client;

import data.FrameInfo;
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
import java.io.IOException;
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
        frame.setTitle("Client");

        JLabel lbl = new JLabel();
        frame.add(lbl);
        try {

            socket = new Socket(InetAddress.getByName(serverAddress), serverPort);
            socket.setSoTimeout(3000);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            frame.setSize(720, 540);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("Client terminated streaming.");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    e.getWindow().dispose();
                    System.exit(0);
                }
            });

            while (true) {
                FrameInfo frameInfo = (FrameInfo) input.readObject();

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
            }
        } catch (Exception e) {
            if (e instanceof EOFException) {
                System.out.println("Streaming completed.");
                System.exit(0);
            }
        }

    }
}
