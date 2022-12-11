package udp.server;

import config.ServerRunner;
import util.FrameUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {

    public UDPServer() throws IOException {
        DatagramSocket socket = new DatagramSocket(ServerRunner.serverPort, InetAddress.getByName(ServerRunner.serverIP));
        new Thread(() -> {
            try {
                if (ServerRunner.isWebcam) {
                    System.out.println("UDP server is running for streaming webcam...");
                    FrameUtil.readCamera();
                } else if (ServerRunner.isVideo) {
                    System.out.println("UDP server is running for streaming a video...");
                    FrameUtil.readVideo();
                } else {
                    System.out.println("Wrong inputs! Please specify the input such as -video or -webcam");
                    System.exit(0);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        while (true) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                new UDPServerWorkerThread(socket, packet).start();
            } catch (Exception e) {
                if (FrameUtil.serverDown) {
                    System.out.println("Server shutdown");
                    System.exit(0);
                }
            }

        }

    }

}
