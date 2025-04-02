package my_paint.Shapes;

import java.awt.*;

import static my_paint.MyPaint.SELECTION_THRESHOLD;

public class LineShape extends Shape {
    private int x1, y1, x2, y2;

    public LineShape(int x1, int y1, int x2, int y2, Color color) {
        super(color);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void setEndPoint(int x2, int y2) {
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public boolean contains(Point point) {

        int centerX = Math.min(x1, x2) + Math.abs(x1 - x2) / 2;
        int centerY = Math.min(y1, y2) + Math.abs(y1 - y2) / 2;

        return Math.abs(point.x - centerX) <= SELECTION_THRESHOLD &&
                Math.abs(point.y - centerY) <= SELECTION_THRESHOLD;
    }

    @Override
    public void move(int dx, int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }

    @Override
    public int isOnEndPoint(Point point) {
        if (Math.abs(point.x - x1) <= SELECTION_THRESHOLD && Math.abs(point.y - y1) <= SELECTION_THRESHOLD) {
            return 0;
        }
        if (Math.abs(point.x - x2) <= SELECTION_THRESHOLD && Math.abs(point.y - y2) <= SELECTION_THRESHOLD) {
            return 1;
        }
        return -1;
    }

    @Override
    public void resizeEndPoint(int endPointIndex, Point newPosition) {
        if (endPointIndex == 0) {
            x1 = newPosition.x;
            y1 = newPosition.y;
        } else if (endPointIndex == 1) {
            x2 = newPosition.x;
            y2 = newPosition.y;
        }
    }

    @Override
    public String toString() {
        return String.format("LINE: %d,%d,%d,%d,%d,%d,%d", x1, y1, x2, y2,
                color.getRed(), color.getGreen(), color.getBlue());
    }

    public static LineShape fromString(String str) {
        String[] parts = str.substring(6).split(",");
        int x1 = Integer.parseInt(parts[0]);
        int y1 = Integer.parseInt(parts[1]);
        int x2 = Integer.parseInt(parts[2]);
        int y2 = Integer.parseInt(parts[3]);
        int r = Integer.parseInt(parts[4]);
        int g = Integer.parseInt(parts[5]);
        int b = Integer.parseInt(parts[6]);

        return new LineShape(x1, y1, x2, y2, new Color(r, g, b));
    }
}
