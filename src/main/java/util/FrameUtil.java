package util;

import data.FrameInfo;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FrameUtil {

    public static List<FrameInfo> frames = new ArrayList<>();
    public static boolean readingFramesOver = false;

    public static void readVideo() throws IOException, JCodecException {
        File file = new File("src/main/java/video_samples/2min.mp4");
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        Picture picture;

        int count = 0;
        while (null != (picture = grab.getNativeFrame())) {
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            BufferedImage resized = ImageUtil.resize(bufferedImage, 720, 540);
            byte[] compressedImage = ImageUtil.compress(resized);
            frames.add(new FrameInfo(compressedImage, count));
            count++;
        }
        readingFramesOver = true;
    }

    public static void readCamera() throws IOException, JCodecException, InterruptedException {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new FlowLayout());
        jFrame.setSize(720, 540);
        jFrame.setTitle("Server - Streaming Camera");

        JLabel lbl = new JLabel();
        jFrame.add(lbl);
        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        int count = 0;
        while (true) {
            Frame frame = grabber.grab();
            BufferedImage image = new Java2DFrameConverter().convert(frame);

            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                lbl.setIcon(icon);
                jFrame.setVisible(true);
            }

            BufferedImage resized = ImageUtil.resize(image, 720, 540);
            byte[] compressedImage = ImageUtil.compress(resized);
            frames.add(new FrameInfo(compressedImage, count));
            count++;
            Thread.sleep(30);
        }

    }
}
