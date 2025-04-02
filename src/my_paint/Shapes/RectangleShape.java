package my_paint.Shapes;

import java.awt.*;

import static my_paint.MyPaint.SELECTION_THRESHOLD;

public class RectangleShape extends Shape {
    private int x, y, width, height;

    public RectangleShape(int x, int y, int width, int height, Color color) {
        super(color);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void normalizeCoordinates() {
        if (width < 0) {
            this.width = Math.abs(width);
            this.x -= width;
        }

        if (height < 0) {
            this.height = Math.abs(height);
            this.y -= height;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);

        int drawX = width < 0 ? x + width : x;
        int drawY = height < 0 ? y + height : y;
        int drawWidth = Math.abs(width);
        int drawHeight = Math.abs(height);

        g.drawRect(drawX, drawY, drawWidth, drawHeight);
    }

    @Override
    public boolean contains(Point point) {
        int rectX = width < 0 ? x + width : x;
        int rectY = height < 0 ? y + height : y;
        int rectWidth = Math.abs(width);
        int rectHeight = Math.abs(height);

        int centerX = rectX + rectWidth / 2;
        int centerY = rectY + rectHeight / 2;

        return Math.abs(point.x - centerX) <= SELECTION_THRESHOLD &&
                Math.abs(point.y - centerY) <= SELECTION_THRESHOLD;
    }

    @Override
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    @Override
    public int isOnEndPoint(Point point) {
        int rectX = width < 0 ? x + width : x;
        int rectY = height < 0 ? y + height : y;
        int rectWidth = Math.abs(width);
        int rectHeight = Math.abs(height);

        Point[] corners = {
                new Point(rectX, rectY),
                new Point(rectX + rectWidth, rectY),
                new Point(rectX + rectWidth, rectY + rectHeight),
                new Point(rectX, rectY + rectHeight)
        };

        for (int i = 0; i < corners.length; i++) {
            if (Math.abs(point.x - corners[i].x) <= SELECTION_THRESHOLD &&
                    Math.abs(point.y - corners[i].y) <= SELECTION_THRESHOLD) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void resizeEndPoint(int endPointIndex, Point newPosition) {
        switch (endPointIndex) {
            case 0: // top-left
                width = (x + width) - newPosition.x;
                height = (y + height) - newPosition.y;
                x = newPosition.x;
                y = newPosition.y;
                break;
            case 1: // top-right
                width = newPosition.x - x;
                height = (y + height) - newPosition.y;
                y = newPosition.y;
                break;
            case 2: // bottom-right
                width = newPosition.x - x;
                height = newPosition.y - y;
                break;
            case 3: // bottom-left
                width = (x + width) - newPosition.x;
                height = newPosition.y - y;
                x = newPosition.x;
                break;
        }
    }

    @Override
    public String toString() {
        return String.format("RECTANGLE: %d,%d,%d,%d,%d,%d,%d", x, y, width, height,
                color.getRed(), color.getGreen(), color.getBlue());
    }

    public static RectangleShape fromString(String str) {
        String[] parts = str.substring(11).split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int width = Integer.parseInt(parts[2]);
        int height = Integer.parseInt(parts[3]);
        int r = Integer.parseInt(parts[4]);
        int g = Integer.parseInt(parts[5]);
        int b = Integer.parseInt(parts[6]);

        return new RectangleShape(x, y, width, height, new Color(r, g, b));
    }
}
