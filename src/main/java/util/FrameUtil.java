package util;

import config.ServerRunner;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class FrameUtil {
    public static volatile FrameInfo currentFrame = null;
    public static boolean readingFramesOver = false;
    public static boolean serverDown = false;
    public static boolean isCamera = false;

    public static void readVideo() throws IOException, JCodecException, InterruptedException {
        File file = new File(ServerRunner.filepath);
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        Picture picture;

        int count = 0;
        Instant before = Instant.now();
        Instant after = Instant.now();
        while (null != (picture = grab.getNativeFrame())) {
            long delta = Math.abs(Duration.between(before, after).toMillis());
            if (ServerRunner.fixFPS && delta < 40) {
                Thread.sleep(40 - delta);
            }
            before = Instant.now();
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            BufferedImage resized = ImageUtil.resize(bufferedImage, 720, 540);
            byte[] compressedImage = ImageUtil.compress(resized);
            currentFrame = new FrameInfo(compressedImage, count);
            after = Instant.now();
            count++;
        }
        readingFramesOver = true;
    }

    public static void readCamera() {
        isCamera = true;
        try {
            JFrame jFrame = new JFrame();
            jFrame.setLayout(new FlowLayout());
            jFrame.setSize(720, 540);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setTitle("Server - Streaming Camera");
            jFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("Server terminated streaming.");
                    readingFramesOver = true;
                    serverDown = true;
                    e.getWindow().dispose();
                    System.exit(0);
                }
            });

            JLabel lbl = new JLabel();
            jFrame.add(lbl);
            FrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();

            int count = 0;
            while (true) {
                Frame frame = grabber.grab();
                BufferedImage image = new Java2DFrameConverter().convert(frame);

                if (image != null) {
                    image = ImageUtil.mirror(image);
                    ImageIcon icon = new ImageIcon(image);
                    lbl.setIcon(icon);
                    jFrame.setVisible(true);
                }

                BufferedImage resized = ImageUtil.resize(image, 720, 540);

                byte[] compressedImage = ImageUtil.compress(resized);

                currentFrame = new FrameInfo(compressedImage, count);

                count++;
            }
        } catch (Exception e) {
            readingFramesOver = true;
        }

    }

}
