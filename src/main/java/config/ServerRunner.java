package config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import tcp.server.TCPServer;
import udp.server.UDPServer;
import util.CustomHelpPageFormatter;

import java.io.IOException;

public class ServerRunner {
    @Parameter(names = {"-h", "-help"}, help = true, order = 0)
    private static boolean help;
    @Parameter(names = {"-w", "-webcam"}, description = "Streams live camera feed of the server", order = 4)
    public static boolean isWebcam = false;
    @Parameter(names = {"-v", "-video"}, description = "Streams a video", order = 3)
    public static boolean isVideo = false;
    @Parameter(names = {"-u", "-udp"}, description = "Utilizes UDP Sockets while streaming", order = 6)
    public static boolean isUdp = false;
    @Parameter(names = {"-t", "-tcp"}, description = "Utilizes TCP Sockets while streaming", order = 5)
    public static boolean isTcp = false;
    @Parameter(names = {"-fps"}, description = "Does not try to keep fps at 30. Transmits frames as soon as possible", order = 8)
    public static boolean fixFPS = true;
    @Parameter(names = {"-q", "-quality"},
            description = "Determines the amount of the compression. Range: [0-1]. 1 means no compression", order = 7)
    public static float quality = 0.7f;
    @Parameter(names = {"-f", "-filepath"},
            description = "Specified file will be streamed", order = 9)
    public static String filepath = "src/main/java/video_samples/2min.mp4";
    @Parameter(names = {"-short"},
            description = "Sample 2 min video is streamed", order = 10)
    public static boolean isShort = false;
    @Parameter(names = {"-long"},
            description = "Sample 10 min video is streamed", order = 11)
    public static boolean isLong = false;
    @Parameter(names = "-ip",
            description = "IP Address for the server", order = 1)
    public static String serverIP = "127.0.0.1";
    @Parameter(names = "-port",
            description = "Port Address for the server", order = 2)
    public static int serverPort = 1234;


    public static void main(String[] args) throws IOException {
        JCommander commander = JCommander.newBuilder()
                .addObject(new ServerRunner())
                .build();

        commander.parse(args);
        commander.setColumnSize(200);
        commander.setUsageFormatter(new CustomHelpPageFormatter(commander));

        if (help) {
            commander.usage();
        }

        if (!isVideo && !isWebcam) {
            isVideo = true;
        }

        if (!isUdp && !isTcp) {
            isTcp = true;
        }

        if (isShort) {
            filepath = "src/main/java/video_samples/2min.mp4";
        } else if (isLong) {
            filepath = "src/main/java/video_samples/10min.mp4";
        }

        if (isUdp) {
            new UDPServer();
        } else {
            new TCPServer();
        }

    }

}

