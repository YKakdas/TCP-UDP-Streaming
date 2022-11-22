package udp.server;

import data.UDPDatagramInfo;
import util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServerWorkerThread extends Thread {
    private int serverPort;

    public UDPServerWorkerThread(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(serverPort);

            int count = 0;

            byte[] dummy = new byte[1024];
            DatagramPacket received = new DatagramPacket(dummy, dummy.length);
            socket.receive(received);
            InetAddress clientAddress = received.getAddress();
            int clientPort = received.getPort();

            while (true) {
                if (count < UDPServer.frames.size()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(UDPServer.frames.get(count));
                    oos.flush();
                    // get the byte array of the object
                    byte[] Buf = baos.toByteArray();

                    int numberOfFragments = (int) Math.ceil((double) Buf.length / 64000);
                    UDPDatagramInfo udpDatagramInfo = new UDPDatagramInfo(Buf.length, numberOfFragments);

                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    ObjectOutputStream oos2 = new ObjectOutputStream(baos2);

                    oos2.writeObject(udpDatagramInfo);
                    oos2.flush();
                    System.out.println(baos2.toByteArray().length);
                    DatagramPacket sendSize =
                            new DatagramPacket(baos2.toByteArray(), baos2.toByteArray().length, clientAddress, clientPort);
                    socket.send(sendSize);

                    Thread.sleep(50);
                    System.out.println(Buf.length);
                    for (int i = 0; i < numberOfFragments; i++) {
                        int start = i * 64000;
                        int end = (i + 1) * 64000;
                        byte [] send = ByteUtil.getSubArray(start,end,Buf);
                        DatagramPacket sendPacket =
                                new DatagramPacket(send, send.length, clientAddress, clientPort);
                        socket.send(sendPacket);
                        System.out.println("Sent bytes");
                    }


                    count++;
                }
                if (count > UDPServer.frames.size() && UDPServer.readingFramesOver) {
                    break;
                }
            }


            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
