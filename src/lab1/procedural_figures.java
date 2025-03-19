package lab1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class procedural_figures {

    static String path = "c:\\Study\\grafika\\grafika_lab\\Grafics\\images";

    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        circles(image);
        rings(image);
        radialLines(image);
        duplicatedRings(image);
        squares(image);
    }

    public static void circles(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        int cellSize = 100;
        int radius = 40;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cellX = (j / cellSize) * cellSize + cellSize / 2;
                int cellY = (i / cellSize) * cellSize + cellSize / 2;
                int dist = (int) Math.sqrt((j - cellX) * (j - cellX) + (i - cellY) * (i - cellY));

                if (dist <= radius) {
                    image.setRGB(j, i, Color.BLACK.getRGB());
                } else {
                    image.setRGB(j, i, Color.LIGHT_GRAY.getRGB());
                }
            }
        }

        saveImage(image, "circles");
    }

    public static void rings(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double d = Math.sqrt((double) (i - width / 2) * (i - width / 2)
                        + (j - height / 2) * (j - height / 2));

                double gap = 1000/ d;

                if (d % (gap * 2) <= gap) {
                    image.setRGB(j, i, Color.BLACK.getRGB());
                } else {
                    image.setRGB(j, i, Color.WHITE.getRGB());
                }
            }
        }
        saveImage(image, "rings");
    }

    public static void radialLines(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double angle = Math.atan2(i - height / 2, j - width / 2) * 180 / Math.PI;
                if (((int) angle + 180) % 20 > 10) {
                    image.setRGB(j, i, Color.BLACK.getRGB());
                } else {
                    image.setRGB(j, i, Color.WHITE.getRGB());
                }
            }
        }

        saveImage(image, "radial_lines");
    }

    public static void duplicatedRings(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        int cellSize = 100;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cellX = (j / cellSize) * cellSize + cellSize / 2;
                int cellY = (i / cellSize) * cellSize + cellSize / 2;
                int dist = (int) Math.sqrt((j - cellX) * (j - cellX) + (i - cellY) * (i - cellY));

                if (dist % 15 > 5) {
                    image.setRGB(j, i, Color.BLACK.getRGB());
                } else {
                    image.setRGB(j, i, Color.LIGHT_GRAY.getRGB());
                }
            }
        }

        saveImage(image, "duplicated_rings");
    }

    public static void squares(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        int cellSize = 100;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (
                        (i % cellSize + j % cellSize) >= cellSize / 2 &&
                                (i % cellSize + j % cellSize) <= cellSize / 2 + cellSize &&
                                (i % cellSize - j % cellSize) <= cellSize / 2 &&
                                (i % cellSize - j % cellSize) >= -cellSize / 2
                ) {
                    image.setRGB(j, i, Color.BLACK.getRGB());
                } else {
                    image.setRGB(j, i, Color.WHITE.getRGB());
                }
            }
        }

        saveImage(image, "squares");
    }

    public static void saveImage(BufferedImage image, String name) {
        try {
            File dir = new File(path);
            dir.mkdirs();
            ImageIO.write(image, "png", new File(path + "\\" + name + ".png"));
            System.out.println("Image created successfully");
        } catch (IOException e) {
            System.out.println("Error saving image");
        }
    }
}
