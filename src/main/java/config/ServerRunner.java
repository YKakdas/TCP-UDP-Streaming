package config;

import tcp.server.TCPServer;
import udp.server.UDPServer;

import java.util.Arrays;

public class ServerRunner {
    public static boolean isWebcam;
    public static boolean isVideo;
    public static boolean isUdp;
    public static boolean isTcp;

    public static void main(String[] args) {
        isWebcam = Arrays.stream(args).anyMatch(arg -> arg.contains("-webcam"));
        isVideo = Arrays.stream(args).anyMatch(arg -> arg.contains("-video"));
        isUdp = Arrays.stream(args).anyMatch(arg -> arg.contains("-udp"));
        isTcp = Arrays.stream(args).anyMatch(arg -> arg.contains("-tcp"));

        if (!isTcp && !isUdp) {
            System.out.println("Please specify the socket such as -tcp or -udp");
            System.exit(0);
        }

        try {
            if (isTcp) {
                new TCPServer();
            } else {
                new UDPServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
