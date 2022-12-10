package udp.server;

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
        while (true) {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            if (previousNum == FrameUtil.currentFrame.getFrameNum()) {
                continue;
            }
            int currentNum = FrameUtil.currentFrame.getFrameNum();
            FrameInfo temp = new FrameInfo(FrameUtil.currentFrame.getData(), FrameUtil.currentFrame.getFrameNum());
            oos.writeObject(temp);
            oos.flush();

            byte[] frameBuf = baos.toByteArray();

            int numberOfFragments = (int) Math.ceil((double) frameBuf.length / 64000);
            UDPDatagramInfo udpDatagramInfo = new UDPDatagramInfo(frameBuf.length, numberOfFragments);

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            oos.writeObject(udpDatagramInfo);
            oos.flush();

            DatagramPacket metadataPacket =
                    new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, clientAddress, clientPort);
            socket.send(metadataPacket);

            for (int i = 0; i < numberOfFragments; i++) {
                int start = i * 64000;
                int end = (i + 1) * 64000;
                byte[] sendBuffer = ByteUtil.getSubArray(start, end, frameBuf);
                DatagramPacket sendFragment =
                        new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                socket.send(sendFragment);
                System.out.println("sent");
            }

            previousNum = currentNum;
            System.out.println("Heree");

            if (FrameUtil.readingFramesOver) {
                break;
            }
        }
        oos.writeInt(-1);
        oos.flush();
        socket.close();
    }
}
