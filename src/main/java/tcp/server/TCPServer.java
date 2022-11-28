package tcp.server;

import data.FrameInfo;
import org.jcodec.api.JCodecException;
import util.FrameUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {

    public static void main(String[] args) throws IOException, JCodecException, InterruptedException {
        new TCPServer();
    }

    public TCPServer() throws IOException, JCodecException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        ServerSocket serverSocket = new ServerSocket(4000);

        for (int i = 0; i < 8; i++) {
            TCPServerWorkerThread thread =
                    new TCPServerWorkerThread(serverSocket);
            executorService.submit(thread);
        }
        executorService.shutdown();

        FrameUtil.readVideo();
     //   FrameUtil.readCamera();
        while (!executorService.isTerminated()) {

        }
    }

}
