package udp.server;

import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class UDPServerWorkerThread extends Thread{
    private int serverPort;

    public UDPServerWorkerThread(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(serverPort);
            //ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            int count = 0;

            while (true) {
                if (count < UDPServer.frames.size()) {
                    output.writeObject(UDPServer.frames.get(count));
                    output.flush();
                    count++;
                }
                if (count > UDPServer.frames.size() && UDPServer.readingFramesOver) {
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
