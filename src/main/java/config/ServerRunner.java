package config;

import tcp.server.TCPServer;
import udp.server.UDPServer;

import java.util.Arrays;

public class ServerRunner {
    public static boolean isWebcam;
    public static boolean isVideo;
    public static boolean isUdp;
    public static boolean isTcp;
    public static boolean fixFPS;
    public static float quality = 0.7f;

    public static void main(String[] args) {
        isWebcam = Arrays.stream(args).anyMatch(arg -> arg.contains("-webcam"));
        isVideo = Arrays.stream(args).anyMatch(arg -> arg.contains("-video"));
        isUdp = Arrays.stream(args).anyMatch(arg -> arg.contains("-udp"));
        isTcp = Arrays.stream(args).anyMatch(arg -> arg.contains("-tcp"));
        fixFPS = Arrays.stream(args).anyMatch(arg -> arg.contains("-fps"));
        if (Arrays.stream(args).anyMatch(arg -> arg.contains("-quality"))) {
            int index = Arrays.asList(args).indexOf("-quality");
            try {
                quality = Float.parseFloat(args[index + 1]);
            } catch (Exception e) {
                System.out.println("Invalid quality parameter. Default value(0.7) is being used instead.");
            }
        }

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
