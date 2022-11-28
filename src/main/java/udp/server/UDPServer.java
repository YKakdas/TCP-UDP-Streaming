package udp.server;

import org.jcodec.api.JCodecException;
import util.FrameUtil;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer {

    public static void main(String[] args) throws IOException, JCodecException, InterruptedException {
        new UDPServer();
    }

    public UDPServer() throws IOException, JCodecException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        for (int i = 0; i < 1; i++) {
            UDPServerWorkerThread thread =
                    new UDPServerWorkerThread(1234);
            executorService.submit(thread);
        }
        executorService.shutdown();

    //    FrameUtil.readVideo();
        FrameUtil.readCamera();

        while (!executorService.isTerminated()) {

        }
    }

}
