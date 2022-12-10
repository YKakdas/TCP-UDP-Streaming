package config;

import tcp.client.TCPClient;
import udp.client.UDPClient;

import java.util.Arrays;

public class ClientRunner {
    public static boolean isUdp;
    public static boolean isTcp;

    public static void main(String[] args) {
        isUdp = Arrays.stream(args).anyMatch(arg -> arg.contains("-udp"));
        isTcp = Arrays.stream(args).anyMatch(arg -> arg.contains("-tcp"));

        if (isTcp) {
            new TCPClient();
        } else if (isUdp) {
            new UDPClient();
        } else {
            System.out.println("Please specify the socket such as -tcp or -udp.");
            System.exit(0);
        }
    }
}
