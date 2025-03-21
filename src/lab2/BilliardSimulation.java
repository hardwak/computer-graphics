package lab2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Ball {
    double x, y, vx, vy;
    Color color;
    final int radius = 20;
    final double friction = 0.997;

    public Ball(double x, double y, double vx, double vy, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
    }

    public void move(int width, int height) {
        x += vx;
        y += vy;

        if (x - radius < 0 || x + radius > width) {
            vx = -vx;
            x = Math.max(radius, Math.min(width - radius, x));
        }
        if (y - radius < 0 || y + radius > height) {
            vy = -vy;
            y = Math.max(radius, Math.min(height - radius, y));
        }

        vx *= friction;
        vy *= friction;
    }
}

public class BilliardSimulation extends JPanel implements ActionListener {
    private int width = 800, height = 400;
    private final List<Ball> balls = new ArrayList<>();

    public BilliardSimulation() {
        balls.add(new Ball(100, 100, 8, 6, Color.RED));
        balls.add(new Ball(200, 150, -6, 4, Color.YELLOW));
        balls.add(new Ball(300, 200, 4, -4, Color.BLUE));
        balls.add(new Ball(400, 250, -8, -12, Color.BLACK));
        balls.add(new Ball(500, 250, -8, 12, Color.MAGENTA));
        balls.add(new Ball(600, 350, 8, -14, Color.PINK));
        balls.add(new Ball(700, 350, -8, 12, Color.GREEN));

        Timer timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();
        width = size.width;
        height = size.height;

        g.setColor(new Color(0, 150, 0));
        g.fillRect(0, 0, width, height);

        for (Ball ball : balls) {
            g.setColor(ball.color);
            g.fillOval((int) ball.x - ball.radius, (int) ball.y - ball.radius, 2 * ball.radius, 2 * ball.radius);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < balls.size(); i++) {
            Ball b1 = balls.get(i);
            b1.move(width, height);

            for (int j = i + 1; j < balls.size(); j++) {
                Ball b2 = balls.get(j);
                double dx = b2.x - b1.x;
                double dy = b2.y - b1.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < b1.radius * 2) {
                    //normal vector
                    double nx = dx / distance;
                    double ny = dy / distance;

                    //prevent balls kissing
                    double overlap = 2 * b1.radius - distance;
                    b1.x -= overlap * nx / 2;
                    b1.y -= overlap * ny / 2;
                    b2.x += overlap * nx / 2;
                    b2.y += overlap * ny / 2;

                    // t  = (-ny, nx)
                    double v1n = b1.vx * nx + b1.vy * ny;
                    double v1t = -b1.vx * ny + b1.vy * nx;
                    double v2n = b2.vx * nx + b2.vy * ny;
                    double v2t = -b2.vx * ny + b2.vy * nx;

                    //Vx = Vin * nx + Vit * (tx / -ny)
                    //Vy = Vin * ny + Vit * (ty / nx)
                    b1.vx = v2n * nx - v2t * ny;
                    b1.vy = v2n * ny + v2t * nx;
                    b2.vx = v1n * nx - v1t * ny;
                    b2.vy = v1n * ny + v1t * nx;
                }
            }
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Billiard Ball Simulation");
        BilliardSimulation panel = new BilliardSimulation();
        frame.add(panel);
        frame.setSize(820, 440);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

