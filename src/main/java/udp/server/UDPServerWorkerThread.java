package udp.server;

import data.UDPDatagramInfo;
import util.ByteUtil;
import util.FrameUtil;

import java.io.ByteArrayOutputStream;
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
            ByteArrayOutputStream baos;
            ObjectOutputStream oos;
            while (true) {
                if (count < FrameUtil.frames.size()) {

                    baos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(baos);
                    oos.writeObject(FrameUtil.frames.get(count));
                    oos.flush();

                    // get the byte array of the object
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

                    Thread.sleep(50);

                    for (int i = 0; i < numberOfFragments; i++) {
                        int start = i * 64000;
                        int end = (i + 1) * 64000;
                        byte[] sendBuffer = ByteUtil.getSubArray(start, end, frameBuf);
                        DatagramPacket sendFragment =
                                new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                        socket.send(sendFragment);
                        System.out.println("Sent bytes");
                    }

                    count++;
                }
                if (count > FrameUtil.frames.size() && FrameUtil.readingFramesOver) {
                    break;
                }
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
