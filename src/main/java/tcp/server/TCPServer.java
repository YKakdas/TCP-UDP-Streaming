package tcp.server;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {

    public static void main(String[] args) throws IOException, JCodecException {
        new TCPServer();
    }

    public TCPServer() throws IOException, JCodecException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        ServerSocket serverSocket = new ServerSocket(4000);

        File file = new File("sample-mp4-file.mp4");
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));

        List<byte[]> frames = new ArrayList<>();

        Picture picture;
        while (null != (picture = grab.getNativeFrame())) {
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            byte[] compressedImage = ImageUtil.compress(bufferedImage);

            frames.add(compressedImage);
        }

        for (int i = 0; i < 8; i++) {
            TCPServerWorkerThread thread = new TCPServerWorkerThread(frames, serverSocket);
            executorService.submit(thread);
        }
        executorService.shutdown();

        while (!executorService.isTerminated()) {

        }
    }


}
