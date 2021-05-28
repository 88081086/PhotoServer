/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package pengguang.photoserver;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import com.mortennobel.imagescaling.ResampleOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.AdvancedResizeOp;

public class Thumbnail {
    private void writeJpeg(BufferedImage image, String destFile, float quality)
            throws IOException {
            ImageWriter writer = null;
            FileImageOutputStream output = null;
            try {
                writer = ImageIO.getImageWritersByFormatName("jpeg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
                output = new FileImageOutputStream(new File(destFile));
                writer.setOutput(output);
                IIOImage iioImage = new IIOImage(image, null, null);
                writer.write(null, iioImage, param);
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (writer != null) writer.dispose();
                if (output != null) output.close();
            }
    }


    void generate(String sourceFile, String destFile, int width, int height)
            throws IOException {
            BufferedImage sourceImage = ImageIO.read(new File(sourceFile));
            int scaledWidth = width;
            int scaledHeight = height;
            if (sourceImage.getWidth()*height > sourceImage.getHeight()*width) {
                scaledWidth = scaledHeight*sourceImage.getWidth()/sourceImage.getHeight();
            } else {
                scaledHeight = scaledWidth*sourceImage.getHeight()/sourceImage.getWidth();
            }
            ResampleOp resampleOp = new ResampleOp(scaledWidth, scaledHeight);
            resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
            resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
            BufferedImage destImage = resampleOp.filter(sourceImage, null);
            float JPEG_QUALITY = 0.9f;
            if (scaledWidth > width) {
                writeJpeg(destImage.getSubimage((scaledWidth-width)>>1, 0, width, height), destFile, JPEG_QUALITY);
            } else {
                writeJpeg(destImage.getSubimage(0, (scaledHeight-height)>>1, width, height), destFile, JPEG_QUALITY);
            }
    }

    public static void main(String[] args) {
        try {
            new Thumbnail().generate(args[0], args[1], 320, 320);
        } catch (Exception e) {
            System.out.println(""+e);
        }
    }
}