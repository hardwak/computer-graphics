package lab1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class cell {
    public static void main(String[] args) {
        if (args.length < 9) {
            System.out.println("Usage: java cell <line_width> <line_gap_x> <line_gap_y> <line_r> <line_g> <line_b> <bg_r> <bg_g> <bg_b>");
            return;
        }

        String path = "c:\\Study\\grafika\\grafika_lab\\Grafics\\images";

        BufferedImage image;
        image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        int height = image.getHeight();
        int width = image.getWidth();

        int line_width = Integer.parseInt(args[0]);
        int line_gap_x = Integer.parseInt(args[1]);
        int line_gap_y = Integer.parseInt(args[2]);

        int line_r = Integer.parseInt(args[3]);
        int line_g = Integer.parseInt(args[4]);
        int line_b = Integer.parseInt(args[5]);
        Color line_color = new Color(line_r, line_g, line_b);

        int bg_r = Integer.parseInt(args[6]);
        int bg_g = Integer.parseInt(args[7]);
        int bg_b = Integer.parseInt(args[8]);
        Color background_color = new Color(bg_r, bg_g, bg_b);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                if (i % (line_width + line_gap_x) > line_gap_x || j % (line_width + line_gap_y) > line_gap_y) {
                    image.setRGB(j, i, line_color.getRGB());
                }
                else {
                    image.setRGB(j, i, background_color.getRGB());
                }
            }
        }

        try {
            File dir = new File(path);
            dir.mkdir();
            ImageIO.write(image, "png", new File(path + "\\cell.png"));
            System.out.println("image created successfully");
        } catch (IOException e) {
            System.out.println("Gray image cannot be stored in BMP file");
        }
    }
}
