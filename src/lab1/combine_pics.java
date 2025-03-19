package lab1;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class combine_pics {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String path = "c:\\Study\\grafika\\grafika_lab\\Grafics\\images";

        BufferedImage image1, image2;

        try {
            image1 = ImageIO.read(new File("c:\\Study\\grafika\\grafika_lab\\Grafics\\images\\for_combine_1.JPG"));
            image2 = ImageIO.read(new File("c:\\Study\\grafika\\grafika_lab\\Grafics\\images\\for_combine_2.JPG"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Image not found");
        }

        int height = image1.getHeight();
        int width = image1.getWidth();

        if (height != image2.getHeight() || width != image2.getWidth())
            throw new IllegalArgumentException("Dimensions do not match");

        int ring_width = 200;

        int line_width = 200;
        int line_gap = 400;

        int cell_size = 1;

        System.out.println("1 - Rings\n" +
                "2 - Cell\n" +
                "3 - Chess board\n" +
                "Enter mask option:");
        int mask = sc.nextInt();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch (mask) {
                    case 1: {
                        double d = Math.sqrt((double) (i - height / 2) * (i - height / 2)
                                + (j - width / 2) * (j - width / 2));

                        if ((((int) d) / ring_width) % 2 == 0) {
                            image1.setRGB(j, i, image2.getRGB(j, i));
                        }
                        break;
                    }
                    case 2: {
                        if (i % (line_width + line_gap) > line_gap || j % (line_width + line_gap) > line_gap) {
                            image1.setRGB(j, i, image2.getRGB(j, i));
                        }
                        break;
                    }
                    case 3: {
                        if ((i / cell_size + j / cell_size) % 2 == 0)
                            image1.setRGB(j, i, image2.getRGB(j, i));
                        break;
                    }
                    default:
                        System.out.println("Invalid mask option");
                        return;
                }
            }
        }

        try {
            File dir = new File(path);
            dir.mkdir();
            ImageIO.write(image1, "jpg", new File(path + "\\combined.jpg"));
            System.out.println("image created successfully");
        } catch (IOException e) {
            System.out.println("Gray image cannot be stored in BMP file");
        }

    }


}
