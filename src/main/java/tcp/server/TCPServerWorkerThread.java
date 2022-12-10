package tcp.server;

import util.FrameUtil;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

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

    private void loopForStreaming(ObjectOutputStream output, Socket socket) throws IOException {

        int previousNum = 0;
        while (true) {

            if (previousNum == FrameUtil.currentFrame.getFrameNum()) {
                continue;
            }
            int currentNum = FrameUtil.currentFrame.getFrameNum();
            output.writeObject(FrameUtil.currentFrame);
            output.flush();
            previousNum = currentNum;

            if (FrameUtil.readingFramesOver) {
                break;
            }
        }

        output.writeInt(-1);
        output.flush();
        socket.close();
    }
}
