package config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import tcp.client.TCPClient;
import udp.client.UDPClient;
import util.CustomHelpPageFormatter;

public class ClientRunner {
    @Parameter(names = {"-h", "-help"}, help = true, order = 0)
    private static boolean help;
    @Parameter(names = {"-u", "-udp"}, description = "Utilizes UDP Sockets while streaming", order = 3)
    public static boolean isUdp = false;
    @Parameter(names = {"-t", "-tcp"}, description = "Utilizes TCP Sockets while streaming", order = 4)
    public static boolean isTcp = false;
    @Parameter(names = "-ip",
            description = "IP Address of the server", order = 1)
    public static String serverIP = "127.0.0.1";
    @Parameter(names = "-port",
            description = "Port Address of the server", order = 2)
    public static int serverPort = 1234;

    public static void main(String[] args) {
        JCommander commander = JCommander.newBuilder()
                .addObject(new ClientRunner())
                .build();

        commander.parse(args);
        commander.setColumnSize(200);
        commander.setUsageFormatter(new CustomHelpPageFormatter(commander));

        if (help) {
            commander.usage();
        }

        if (!isTcp && !isUdp) {
            isTcp = true;
        }

        if (isUdp) {
            new UDPClient();
        } else {
            new TCPClient();
        }

    }
}
