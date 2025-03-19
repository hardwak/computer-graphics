package lab1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class chess {

    public static void main(String[] args) {
        if (args.length < 7) {
            System.out.println("Usage: java chess <cell_size> <line_r> <line_g> <line_b> <bg_r> <bg_g> <bg_b>");
            return;
        }

        String path = "c:\\Study\\grafika\\grafika_lab\\Grafics\\images";

        BufferedImage image;
        image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        int height = image.getHeight();
        int width = image.getWidth();

        int cell_size = Integer.parseInt(args[0]);

        int line_r = Integer.parseInt(args[1]);
        int line_g = Integer.parseInt(args[2]);
        int line_b = Integer.parseInt(args[3]);
        Color line_color = new Color(line_r, line_g, line_b);

        int bg_r = Integer.parseInt(args[4]);
        int bg_g = Integer.parseInt(args[5]);
        int bg_b = Integer.parseInt(args[6]);
        Color background_color = new Color(bg_r, bg_g, bg_b);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((i / cell_size + j / cell_size) % 2 == 0) {
                    image.setRGB(j, i, line_color.getRGB());
                }
                else
                    image.setRGB(j, i, background_color.getRGB());
            }
        }

        try {
            File dir = new File(path);
            dir.mkdir();
            ImageIO.write(image, "png", new File(path + "\\chess.png"));
            System.out.println("image created successfully");
        } catch (IOException e) {
            System.out.println("Gray image cannot be stored in BMP file");
        }

    }
}
