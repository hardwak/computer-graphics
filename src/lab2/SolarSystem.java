package lab2;

import javax.swing.*;
import java.awt.*;

public class SolarSystem extends JPanel {
    private int sunX = 400, sunY = 400;
    private double mercuryAngle = 0;
    private double venusAngle = 1;
    private double earthAngle = 3;
    private double moonAngle = 1;
    private double marsAngle = 5;
    private double jupiterAngle = 4.5;
    private double saturnAngle = 3.5;
    private double uranusAngle = 6;
    private double neptuneAngle = 3;

    private final int mercuryOrbitRadius = 50;
    private final int venusOrbitRadius = 80;
    private final int earthOrbitRadius = 110;
    private final int moonOrbitRadius = 70;
    private final int marsOrbitRadius = 150;
    private final int jupiterOrbitRadius = 200;
    private final int saturnOrbitRadius = 250;
    private final int uranusOrbitRadius = 300;
    private final int neptuneOrbitRadius = 350;

    public SolarSystem() {
        setBackground(Color.BLACK);
        Timer timer = new Timer(30, e -> {
            mercuryAngle += 0.1;
            venusAngle += 0.07;
            earthAngle += 0.05;
            moonAngle += 0.02;
            marsAngle += 0.03;
            jupiterAngle += 0.02;
            saturnAngle += 0.015;
            uranusAngle += 0.01;
            neptuneAngle += 0.008;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();
        sunX = size.width / 2;
        sunY = size.height / 2;


        g.setColor(Color.YELLOW);
        g.fillOval(sunX - 20, sunY - 20, 40, 40);

        drawPlanet(g, Color.DARK_GRAY, mercuryOrbitRadius, mercuryAngle, 6);
        drawPlanet(g, Color.ORANGE, venusOrbitRadius, venusAngle, 10);
        drawPlanetWithMoon(g, Color.GREEN, earthOrbitRadius, earthAngle, 10, Color.GRAY, moonOrbitRadius, moonAngle, 5);
        drawPlanet(g, Color.RED, marsOrbitRadius, marsAngle, 8);
        drawPlanet(g, Color.ORANGE, jupiterOrbitRadius, jupiterAngle, 14);
        drawPlanet(g, Color.LIGHT_GRAY, saturnOrbitRadius, saturnAngle, 12);
        drawPlanet(g, Color.CYAN, uranusOrbitRadius, uranusAngle, 10);
        drawPlanet(g, Color.BLUE, neptuneOrbitRadius, neptuneAngle, 10);
    }

    private void drawPlanet(Graphics g, Color color, int orbitRadius, double angle, int size) {
        int x = (int) (sunX + orbitRadius * Math.cos(angle));
        int y = (int) (sunY + orbitRadius * Math.sin(angle));
        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(sunX - orbitRadius, sunY - orbitRadius, 2 * orbitRadius, 2 * orbitRadius);
        g.setColor(color);
        g.fillOval(x - size, y - size, 2 * size, 2 * size);
    }

    private void drawPlanetWithMoon(Graphics g, Color planetColor, int orbitRadius, double planetAngle, int planetSize, Color moonColor, int moonOrbitRadius, double moonAngle, int moonSize) {
        int planetX = (int) (sunX + orbitRadius * Math.cos(planetAngle));
        int planetY = (int) (sunY + orbitRadius * Math.sin(planetAngle));
        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(sunX - orbitRadius, sunY - orbitRadius, 2 * orbitRadius, 2 * orbitRadius);
        g.setColor(planetColor);
        g.fillOval(planetX - planetSize, planetY - planetSize, 2 * planetSize, 2 * planetSize);

        int moonX = (int) (planetX + moonOrbitRadius * Math.cos(moonAngle));
        int moonY = (int) (planetY + moonOrbitRadius * Math.sin(moonAngle));
        g.setColor(Color.GRAY);
        g.drawOval(planetX - moonOrbitRadius, planetY - moonOrbitRadius, 2 * moonOrbitRadius, 2 * moonOrbitRadius);
        g.setColor(moonColor);
        g.fillOval(moonX - moonSize, moonY - moonSize, 2 * moonSize, 2 * moonSize);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Solar System");
        SolarSystem panel = new SolarSystem();
        frame.add(panel);
        frame.setSize(820, 840);
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
