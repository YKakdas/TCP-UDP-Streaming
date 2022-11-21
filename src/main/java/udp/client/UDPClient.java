package udp.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPClient {

    public static void main(String[] args) {
        new UDPClient();
    }

    public UDPClient() {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        for (int i = 0; i < 1; i++) {
            UDPClientWorkerThread thread = new UDPClientWorkerThread("localhost", 4000);
            executorService.submit(thread);
        }
        executorService.shutdown();

        while (!executorService.isTerminated()) {

        }

    }
}
