package tcp.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

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
        Socket socket = null;
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(320, 240);
        JLabel lbl = new JLabel();
        try {

            socket = new Socket(InetAddress.getByName(serverAddress), serverPort);

            DataInputStream input = new DataInputStream(socket.getInputStream());

            while (true) {
                byte[] receiveBuffer;
                int size = input.readInt();

                if (size == -1) {
                    socket.close();
                    return;
                }

                receiveBuffer = new byte[size];

                int numberOfReadChars = input.read(receiveBuffer);
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(receiveBuffer));

                Instant start = Instant.now();
                Instant end = Instant.now();
                if (image != null) {
                    Duration timeElapsed = Duration.between(start, end);
                    if(timeElapsed.toMillis() < 30){
                        Thread.sleep(30 - Duration.between(start, end).toMillis());
                    }
                    start = Instant.now();

                    ImageIcon icon = new ImageIcon(image);
                    lbl.setIcon(icon);
                    frame.add(lbl);
                    frame.setVisible(true);
                    end = Instant.now();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
