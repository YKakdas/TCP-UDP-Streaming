package tcp.server;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class TCPServerWorkerThread extends Thread {

    private List<byte[]> frames;
    private ServerSocket serverSocket;

    public TCPServerWorkerThread(List<byte[]> frames, ServerSocket serverSocket) {
        this.frames = frames;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {

            DataOutputStream output;

            Socket socket = serverSocket.accept();

            output = new DataOutputStream(socket.getOutputStream());

            for (byte[] frame : frames) {
                output.writeInt(frame.length);
                output.flush();
                output.write(frame);
                output.flush();
            }

            output.writeInt(-1);
            output.flush();
            socket.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
