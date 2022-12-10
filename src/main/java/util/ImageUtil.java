package util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ImageUtil {

    public static byte[] compress(BufferedImage bufferedImage) {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (ImageOutputStream outputStream = new MemoryCacheImageOutputStream(compressed)) {

            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("JPEG").next();

            // Configure JPEG compression: 70% quality
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(0.3f);

            // Set your in-memory stream as the output
            jpgWriter.setOutput(outputStream);

            // Write image as JPEG w/configured settings to the in-memory stream
            jpgWriter.write(null, new IIOImage(bufferedImage, null, null), jpgWriteParam);

            // Dispose the writer to free resources
            jpgWriter.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compressed.toByteArray();
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage mirror(BufferedImage src) {
        int height = src.getHeight();
        int width = src.getWidth();
        BufferedImage mirrored = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int j = 0; j < height; j++) {
            for (int i = 0, w = width - 1; i < width; i++, w--) {
                int p = src.getRGB(i, j);
                //set mirror image pixel value - both left and right
                mirrored.setRGB(w, j, p);
            }
        }
        return mirrored;
    }
}
