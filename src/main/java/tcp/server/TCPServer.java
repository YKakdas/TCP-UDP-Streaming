package tcp.server;

import config.ServerRunner;
import org.jcodec.api.JCodecException;
import util.FrameUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {

    public TCPServer() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        ServerSocket serverSocket = new ServerSocket(4000);

        new Thread(() -> {
            try {
                if (ServerRunner.isVideo) {
                    System.out.println("TCP server is running for streaming a video...");
                    FrameUtil.readVideo();
                } else if (ServerRunner.isWebcam) {
                    System.out.println("TCP server is running for streaming webcam...");
                    FrameUtil.readCamera();
                } else {
                    System.out.println("Wrong inputs! Please specify the input such as -video or -webcam");
                    System.exit(0);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        for (int i = 0; i < 8; i++) {
            TCPServerWorkerThread thread =
                    new TCPServerWorkerThread(serverSocket);
            executorService.submit(thread);
        }
        executorService.shutdown();

        while (!executorService.isTerminated()) {

        }
    }

}
