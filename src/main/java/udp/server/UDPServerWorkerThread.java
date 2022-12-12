package udp.server;

import config.ServerRunner;
import data.FrameInfo;
import data.UDPDatagramInfo;
import util.ByteUtil;
import util.FrameUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

public class UDPServerWorkerThread extends Thread {
    private int serverPort;
    private DatagramSocket socket;
    private DatagramPacket packet;

    public UDPServerWorkerThread(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    public void run() {
        try {
            int count = 0;

            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();

            System.out.println("A client with ip: " + clientAddress + " and port : " + clientPort + " has been connected");

            while (FrameUtil.currentFrame == null) ;

            loopForStreaming(socket, clientAddress, clientPort);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loopForStreaming(DatagramSocket socket,
                                  InetAddress clientAddress, int clientPort) throws IOException, InterruptedException {

        ByteArrayOutputStream baos;
        ObjectOutputStream oos;
        int previousNum = 0;

        Instant before = Instant.now();
        Instant after = Instant.now();

        while (true) {
            long delta = Math.abs(Duration.between(before, after).toMillis());
            if (ServerRunner.fixFPS && delta < 40 && delta != 0) {
                Thread.sleep(40 - delta);
            }
            before = Instant.now();
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            if (previousNum == FrameUtil.currentFrame.getFrameNum()) {
                after = Instant.now();
                continue;
            }
            int currentNum = FrameUtil.currentFrame.getFrameNum();
            FrameInfo temp = new FrameInfo(FrameUtil.currentFrame.getData(), FrameUtil.currentFrame.getFrameNum());
            oos.writeObject(temp);
            oos.flush();

            byte[] frameBuf = baos.toByteArray();

            int numberOfFragments = (int) Math.ceil((double) frameBuf.length / 1024);
            UDPDatagramInfo udpDatagramInfo = new UDPDatagramInfo(frameBuf.length, numberOfFragments);

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            oos.writeObject(udpDatagramInfo);
            oos.flush();

            DatagramPacket metadataPacket =
                    new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, clientAddress, clientPort);
            socket.send(metadataPacket);

            for (int i = 0; i < numberOfFragments; i++) {
                int start = i * 1024;
                int end = (i + 1) * 1024;
                byte[] sendBuffer = ByteUtil.getSubArray(start, end, frameBuf);
                DatagramPacket sendFragment =
                        new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                socket.send(sendFragment);
            }

            after = Instant.now();
            previousNum = currentNum;


            if (FrameUtil.readingFramesOver) {
                System.out.println("Streaming completed.");
                socket.close();
                System.exit(0);
            }
        }

    }
}
