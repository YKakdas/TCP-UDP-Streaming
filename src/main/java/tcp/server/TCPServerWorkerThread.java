package tcp.server;

import config.ServerRunner;
import util.FrameUtil;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class TCPServerWorkerThread extends Thread {

    private ServerSocket serverSocket;

    public TCPServerWorkerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream output;

            Socket socket = serverSocket.accept();
            InetSocketAddress clientSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            System.out.println("A client with ip: " + clientSocketAddress.getAddress().getHostAddress()
                    + " and port : " + clientSocketAddress.getPort() + " has been connected");


            output = new ObjectOutputStream(socket.getOutputStream());

            while (FrameUtil.currentFrame == null) ;

            loopForStreaming(output, socket);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loopForStreaming(ObjectOutputStream output, Socket socket) throws IOException, InterruptedException {

        int previousNum = 0;
        Instant before = Instant.now();
        Instant after = Instant.now();

        while (true) {
            long delta = Math.abs(Duration.between(before, after).toMillis());
            if (ServerRunner.fixFPS && delta < 30) {
                Thread.sleep(30 - delta);
            }
            before = Instant.now();
            if (previousNum == FrameUtil.currentFrame.getFrameNum()) {
                after = Instant.now();
                continue;
            }
            int currentNum = FrameUtil.currentFrame.getFrameNum();
            output.writeObject(FrameUtil.currentFrame);
            output.flush();
            after = Instant.now();
            previousNum = currentNum;

            if (FrameUtil.readingFramesOver) {
                System.out.println("Streaming completed.");
                socket.close();
                System.exit(0);
            }
        }
    }
}
