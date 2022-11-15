package tcp.server;

import java.io.ObjectOutputStream;
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

            output = new ObjectOutputStream(socket.getOutputStream());

            int count = 0;

            while (true) {
                if (count < TCPServer.frames.size()) {
                    output.writeObject(TCPServer.frames.get(count));
                    output.flush();
                    count++;
                }
                if (count >= TCPServer.frames.size() && TCPServer.readingFramesOver) {
                    break;
                }
            }

            output.writeInt(-1);
            output.flush();
            socket.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
