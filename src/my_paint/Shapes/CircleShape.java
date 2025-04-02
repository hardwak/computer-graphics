package my_paint.Shapes;

import java.awt.*;

import static my_paint.MyPaint.SELECTION_THRESHOLD;

public class CircleShape extends Shape {
    private int centerX, centerY, radius;

    public CircleShape(int centerX, int centerY, int radius, Color color) {
        super(color);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    @Override
    public boolean contains(Point point) {
        return Math.abs(point.x - centerX) <= SELECTION_THRESHOLD &&
                Math.abs(point.y - centerY) <= SELECTION_THRESHOLD;
    }

    @Override
    public void move(int dx, int dy) {
        centerX += dx;
        centerY += dy;
    }

    @Override
    public int isOnEndPoint(Point point) {
        Point[] circlePoints = {
                new Point(centerX + radius, centerY),
                new Point(centerX, centerY + radius),
                new Point(centerX - radius, centerY),
                new Point(centerX, centerY - radius)
        };

        for (int i = 0; i < circlePoints.length; i++) {
            if (Math.abs(point.x - circlePoints[i].x) <= SELECTION_THRESHOLD &&
                    Math.abs(point.y - circlePoints[i].y) <= SELECTION_THRESHOLD) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void resizeEndPoint(int endPointIndex, Point newPosition) {
        int dx = newPosition.x - centerX;
        int dy = newPosition.y - centerY;
        radius = (int) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("CIRCLE: %d,%d,%d,%d,%d,%d", centerX, centerY, radius,
                color.getRed(), color.getGreen(), color.getBlue());
    }

    public static CircleShape fromString(String str) {
        String[] parts = str.substring(8).split(",");
        int centerX = Integer.parseInt(parts[0]);
        int centerY = Integer.parseInt(parts[1]);
        int radius = Integer.parseInt(parts[2]);
        int r = Integer.parseInt(parts[3]);
        int g = Integer.parseInt(parts[4]);
        int b = Integer.parseInt(parts[5]);

        return new CircleShape(centerX, centerY, radius, new Color(r, g, b));
    }
}
