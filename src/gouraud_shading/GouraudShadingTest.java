package gouraud_shading;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.text.DecimalFormat;

public class GouraudShadingTest extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int NUM_TRIANGLES = 5000;

    private BufferedImage bufferedImage;
    private JPanel screenPanel;
    private JTextArea resultArea;
    private JButton testBufferedButton;
    private JButton testScreenButton;
    private JButton clearButton;
    private JButton regenerateButton;

    private Triangle2D[] testTriangles;
    private Random random;
    private DecimalFormat df;

    public GouraudShadingTest() {

        setSize(WIDTH, HEIGHT + 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        random = new Random();
        df = new DecimalFormat("#,###.##");

        bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        generateRandomTriangles();

        screenPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bufferedImage, 0, 0, this);
            }
        };
        screenPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JPanel controlPanel = new JPanel();
        testBufferedButton = new JButton("BufferedImage Test");
        testScreenButton = new JButton("Screen Test");
        clearButton = new JButton("Clear");
        regenerateButton = new JButton("Regenerate Triangles");

        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        controlPanel.add(testBufferedButton);
        controlPanel.add(testScreenButton);
        controlPanel.add(clearButton);
        controlPanel.add(regenerateButton);
        controlPanel.add(scrollPane);

        setLayout(new BorderLayout());
        add(screenPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        testBufferedButton.addActionListener(e -> testBufferedRendering());
        testScreenButton.addActionListener(e -> testScreenRendering());
        clearButton.addActionListener(e -> clearScreen());
        regenerateButton.addActionListener(e -> generateRandomTriangles());

        setVisible(true);
    }

    private void generateRandomTriangles() {
        testTriangles = new Triangle2D[NUM_TRIANGLES];

        for (int i = 0; i < NUM_TRIANGLES; i++) {
            int[] x = new int[3];
            int[] y = new int[3];
            Color[] colors = new Color[3];

            int centerX = random.nextInt(WIDTH);
            int centerY = random.nextInt(HEIGHT);

            int size = 20 + random.nextInt(80);

            for (int j = 0; j < 3; j++) {
                double angle = Math.toRadians(random.nextInt(360));
                double distance = size * (0.5 + random.nextDouble() * 0.5);

                x[j] = (int)(centerX + Math.cos(angle) * distance);
                y[j] = (int)(centerY + Math.sin(angle) * distance);

                x[j] = Math.max(0, Math.min(WIDTH - 1, x[j]));
                y[j] = Math.max(0, Math.min(HEIGHT - 1, y[j]));

                colors[j] = new Color(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256)
                );
            }

            testTriangles[i] = new Triangle2D(x, y, colors);
        }
    }

    private void testBufferedRendering() {
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        int totalPixels = 0;
        for (Triangle2D triangle : testTriangles) {
            totalPixels += triangle.countPixelsInside();
        }

        long startTimeStandard = System.nanoTime();

        for (Triangle2D triangle : testTriangles) {
            triangle.gouraudShadeToImage(bufferedImage);
        }

        long endTimeStandard = System.nanoTime();
        double standardTime = (endTimeStandard - startTimeStandard) / 1000000.0;

        screenPanel.repaint();


        g = bufferedImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        long startTimeOptimized = System.nanoTime();

        for (Triangle2D triangle : testTriangles) {
            triangle.optimizedGouraudShadeToImage(bufferedImage);
        }

        long endTimeOptimized = System.nanoTime();
        double optimizedTime = (endTimeOptimized - startTimeOptimized) / 1000000.0;

        screenPanel.repaint();


        double trianglesPerSecondStandard = NUM_TRIANGLES / (standardTime / 1000.0);
        double pixelsPerSecondStandard = totalPixels / (standardTime / 1000.0);

        double trianglesPerSecondOptimized = NUM_TRIANGLES / (optimizedTime / 1000.0);
        double pixelsPerSecondOptimized = totalPixels / (optimizedTime / 1000.0);

        resultArea.append("BufferedImage Results:\n");
        resultArea.append("Standard:\n");
        resultArea.append("Time: " + df.format(standardTime) + " ms\n");
        resultArea.append("Triangles/s: " + df.format(trianglesPerSecondStandard) + "\n");
        resultArea.append("Pixels/s: " + df.format(pixelsPerSecondStandard) + "\n\n");

        resultArea.append("Optimized:\n");
        resultArea.append("Time: " + df.format(optimizedTime) + " ms\n");
        resultArea.append("Triangles/s: " + df.format(trianglesPerSecondOptimized) + "\n");
        resultArea.append("Pixels/s: " + df.format(pixelsPerSecondOptimized) + "\n");
        resultArea.append("Faster in " + df.format(standardTime / optimizedTime) + "x times\n");

        resultArea.append("Avg num of pixels per triangle: " + df.format((double)totalPixels / NUM_TRIANGLES) + "\n\n\n");
    }

    private void testScreenRendering() {
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        screenPanel.repaint();

        int totalPixels = 0;
        for (Triangle2D triangle : testTriangles) {
            totalPixels += triangle.countPixelsInside();
        }

        long startTimeStandard = System.nanoTime();

        Graphics screenGraphics = screenPanel.getGraphics();
        for (Triangle2D triangle : testTriangles) {
            triangle.gouraudShadeToScreen(screenGraphics);
        }

        long endTimeStandard = System.nanoTime();
        double standardTime = (endTimeStandard - startTimeStandard) / 1000000.0;


        g = (Graphics2D) screenPanel.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        long startTimeOptimized = System.nanoTime();

        Graphics optimizedGraphics = screenPanel.getGraphics();
        for (Triangle2D triangle : testTriangles) {
            triangle.optimizedGouraudShadeToScreen(optimizedGraphics);
        }

        long endTimeOptimized = System.nanoTime();
        double optimizedTime = (endTimeOptimized - startTimeOptimized) / 1000000.0;


        double trianglesPerSecondStandard = NUM_TRIANGLES / (standardTime / 1000.0);
        double pixelsPerSecondStandard = totalPixels / (standardTime / 1000.0);

        double trianglesPerSecondOptimized = NUM_TRIANGLES / (optimizedTime / 1000.0);
        double pixelsPerSecondOptimized = totalPixels / (optimizedTime / 1000.0);

        resultArea.append("Screen Test:\n");
        resultArea.append("Standard:\n");
        resultArea.append("Time: " + df.format(standardTime) + " ms\n");
        resultArea.append("Triangles/s: " + df.format(trianglesPerSecondStandard) + "\n");
        resultArea.append("Pixels/s: " + df.format(pixelsPerSecondStandard) + "\n\n");

        resultArea.append("Optimized:\n");
        resultArea.append("Time: " + df.format(optimizedTime) + " ms\n");
        resultArea.append("Triangles/s: " + df.format(trianglesPerSecondOptimized) + "\n");
        resultArea.append("Pixels/s: " + df.format(pixelsPerSecondOptimized) + "\n");
        resultArea.append("Faster in " + df.format(standardTime / optimizedTime) + "x times\n");

        resultArea.append("Avg num of pixels per triangle: " + df.format((double)totalPixels / NUM_TRIANGLES) + "\n\n\n");
    }

    private void clearScreen() {
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        Graphics screenGraphics = screenPanel.getGraphics();
        screenGraphics.setColor(Color.BLACK);
        screenGraphics.fillRect(0, 0, WIDTH, HEIGHT);

        resultArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GouraudShadingTest());
    }
}