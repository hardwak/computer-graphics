package lab1;

/*
 * Computer graphics courses at Wroclaw University of Technology
 * (C) by Jurek Sas, 2009
 *
 * Description:
 *   This demo shows basic raster operations on raset image
 *   represented by BufferedImage object. Image is created
 *   on pixel-by-pixel basis and then stored in a file.
 *
 *   Ten program demonstracyjny pokazuje spos�b wykonywania
 *   podstawowych operacji grafiki rastrowej z uzyciem klasy
 *   BufferedImage. Wyja�niono wykonywanie podstawowych operacji
 *   rastrowych na obiekcie BufefredImage
 */

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.awt.image.*;
import java.awt.Color;
import java.lang.Math;
import javax.imageio.*;

public class Demo0 {

    /**
     * @param args
     */
    private static Scanner in;

    public static void main(String[] args) {
        String dirname = "d:\\GK";
        String image_name = "c:\\GK\\im1.jpg";

        // TODO Auto-generated method stub
        System.out.println("Demo0 just started");

        BufferedImage image;

        // Create an empty image
        image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        // Fill it with a gray-shaded pattern
        int color;
        int i, j;
        int height = image.getHeight();
        int width = image.getWidth();

        color = 0;
        int ring_width = 20;
        int ri = 50;
        int ro = 150;
        Random rand = new Random();
        for (i = 0; i < height; i++)
            for (j = 0; j < width; j++) {
                // Pattern 1 - Basic sharp rings pattern
                double d = Math.sqrt((double) (i - width / 2) * (i - width / 2)
                        + (j - height / 2) * (j - height / 2));
                ri = ((int) d) / ring_width;
                if ((ri % 2) == 0) {
                    color = byte2RGB(0, 0, 0);
                    color = new Color(0, 0, 0).getRGB();
                } else
                    color = byte2RGB(255, 255, 255);

                // In order to read R,G,B from a pixel
                image.setRGB(j, i, color);
            }

        // Save image in graphics file
        try {
            File dir = new File(dirname);
            dir.mkdir();
            ImageIO.write(image, "bmp", new File("d:\\out_img_gray.bmp"));
            System.out.println("Gray image created successfully");
        } catch (IOException e) {
            System.out.println("Gray image cannot be stored in BMP file");
        } ;

        // Now make color image
        int intensity;
        for (i = 0; i < height; i++)
            for (j = 0; j < width; j++) {
                intensity = (byte) (j % 256);
                color = byte2RGB(intensity, (256 - intensity), (i % 256));
                image.setRGB(j, i, color);
            }

        // Save image in graphics file
        try {
            ImageIO.write(image, "jpg", new File("d:\\out_img_color.jpg"));
            System.out.println("Color image created successfully");
        } catch (IOException e) {
            System.out.println("Color image cannot be stored in BMP file");
        } ;

        // Not used here but you should know how ...
        // Acquire the list of formats currently supported by ImageIO
        String formats[] = ImageIO.getReaderFormatNames();
        writeln("You can use the following image file formats:");
        for (i = 0; i < formats.length; i++)
            writeln(formats[i]);

        // Not used here but you should know how ...
        // Lines below show how to query supported file types
        // and how to read a graphic file
        in = new Scanner(System.in);
        BufferedImage input_image = null;
        writeln("Type the image location and name to read:");
        image_name = readStr();

        try {
            input_image = ImageIO.read(new File(image_name));
        } catch (IOException e) {
            System.out.println("Cannot read this image");
        }
    }

    static int byte2RGB(int red, int green, int blue) {
        // Color components must be in range 0 - 255
        red = 0xff & red;
        green = 0xff & green;
        blue = 0xff & blue;
        return (red << 16) + (green << 8) + blue;
    }

    // =======================================================================
    // =======================================================================
    // Console functions - not strictly related to CG but make the work easier
    // =======================================================================
    // =======================================================================

    static void writeln(String stg) {
        System.out.println(stg);
    }

    static void readln() {
        try {
            while (System.in.read() != '\n');
        } catch (Throwable obj) {
        }
    }

    static String readStr() {
        return in.next();
    }

    static int readInt() {
        return (in.nextInt());
    }

    static double readDouble() {
        return (in.nextDouble());
    }
}

