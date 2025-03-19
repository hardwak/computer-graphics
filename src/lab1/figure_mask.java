package lab1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class figure_mask {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String path = "c:\\Study\\grafika\\grafika_lab\\Grafics\\images";

        BufferedImage image;

        try {
            image = ImageIO.read(new File("c:\\Study\\grafika\\grafika_lab\\Grafics\\images\\example.JPG"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Image not found");
        }

        int height = image.getHeight();
        int width = image.getWidth();

        int ring_width = 20;

        int line_width = 20;
        int line_gap = 50;

        int cell_size = 20;

        System.out.println("1 - Rings\n" +
                "2 - Cell\n" +
                "3 - Chess board\n" +
                "Enter mask option:");
        int mask = sc.nextInt();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch(mask) {
                    case 1: {
                        double d = Math.sqrt((double) (i - height / 2) * (i - height / 2)
                                + (j - width / 2) * (j - width / 2));

                        if ((((int) d) / ring_width) % 2 == 0) {
                            image.setRGB(j, i, Color.BLACK.getRGB());
                        }
                        break;
                    }
                    case 2: {
                        if (i % (line_width + line_gap) > line_gap || j % (line_width + line_gap) > line_gap) {
                            image.setRGB(j, i, Color.BLACK.getRGB());
                        }
                        break;
                    }
                    case 3: {
                        if ((i / cell_size + j / cell_size) % 2 == 0)
                            image.setRGB(j, i, Color.BLACK.getRGB());
                        break;
                    }
                }
            }
        }

        try {
            File dir = new File(path);
            dir.mkdir();
            ImageIO.write(image, "jpg", new File(path + "\\masked.jpg"));
            System.out.println("image created successfully");
        } catch (IOException e) {
            System.out.println("Gray image cannot be stored in BMP file");
        }

    }

}
