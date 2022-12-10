package udp.server;

import config.ServerRunner;
import org.jcodec.api.JCodecException;
import util.FrameUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

    public UDPServer() throws IOException {
        DatagramSocket socket = new DatagramSocket(1234);
        new Thread(() -> {
            try {
                if (ServerRunner.isVideo) {
                    FrameUtil.readVideo();
                } else if (ServerRunner.isWebcam) {
                    FrameUtil.readCamera();
                } else {
                    System.out.println("Wrong inputs! Please specify the input such as -video or -webcam");
                    System.exit(0);
                }

            } catch (Exception e) {
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
