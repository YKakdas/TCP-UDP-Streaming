package tcp.server;

import data.FrameInfo;
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

    public static List<FrameInfo> frames = new ArrayList<>();
    public static boolean readingFramesOver = false;

    public static void main(String[] args) throws IOException, JCodecException {
        new TCPServer();
    }

    public TCPServer() throws IOException, JCodecException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        ServerSocket serverSocket = new ServerSocket(4000);

        for (int i = 0; i < 8; i++) {
            TCPServerWorkerThread thread =
                    new TCPServerWorkerThread(serverSocket);
            executorService.submit(thread);
        }
        executorService.shutdown();

        readFrames();

        while (!executorService.isTerminated()) {

        }
    }

    private void readFrames() throws IOException, JCodecException {
        File file = new File("src/main/java/video_samples/2min.mp4");
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        Picture picture;

        while (null != (picture = grab.getNativeFrame())) {
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            BufferedImage resized = ImageUtil.resize(bufferedImage, 1024, 720);
            byte[] compressedImage = ImageUtil.compress(resized);
            frames.add(new FrameInfo(compressedImage, compressedImage.length));
        }
        readingFramesOver = true;
    }


}
