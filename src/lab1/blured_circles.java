package lab1;

import java.io.*;
import java.awt.image.*;
import java.awt.Color;
import java.lang.Math;
import javax.imageio.*;

public class blured_circles {

    public static void main(String[] args) {
        String path = "c:\\Study\\grafika\\grafika_lab\\Grafics\\images";

        BufferedImage image;
        image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        int height = image.getHeight();
        int width = image.getWidth();

        int color, ring_width = 10;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double d = Math.sqrt((double) (i - width / 2) * (i - width / 2)
                        + (j - height / 2) * (j - height / 2));

                int grayness = (int) (127.5*(Math.sin(Math.PI*d/ring_width) + 1));
                color = new Color(grayness, grayness, grayness).getRGB();

                image.setRGB(j, i, color);
            }
        }

        try {
            File dir = new File(path);
            dir.mkdir();
            ImageIO.write(image, "png", new File(path + "\\blured_rings.png"));
            System.out.println("image created successfully");
        } catch (IOException e) {
            System.out.println("Gray image cannot be stored in BMP file");
        }

    }

}
