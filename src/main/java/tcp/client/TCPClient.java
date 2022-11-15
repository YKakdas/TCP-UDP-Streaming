package tcp.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {
    public static void main(String[] args) {
        new TCPClient();
    }

    public TCPClient() {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        for (int i = 0; i < 1; i++) {
            TCPClientWorkerThread thread = new TCPClientWorkerThread("localhost", 4000);
            executorService.submit(thread);
        }
        executorService.shutdown();

        while (!executorService.isTerminated()) {

        }

    }
}
