package udp.server;

import org.jcodec.api.JCodecException;
import util.FrameUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

    public static void main(String[] args) throws IOException {
        new UDPServer();
    }

    public UDPServer() throws IOException {
        DatagramSocket socket = new DatagramSocket(1234);
        new Thread(() -> {
            try {
                FrameUtil.readVideo();
            } catch (IOException | JCodecException e) {
                throw new RuntimeException(e);
            }
        }).start();

        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            new UDPServerWorkerThread(socket, packet).start();
        }

        //   FrameUtil.readCamera();

    }

}
