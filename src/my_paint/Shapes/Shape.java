package my_paint.Shapes;

import java.awt.*;

public abstract class Shape {
    protected Color color;

    public Shape(Color color) {
        this.color = color;
    }

    public abstract void draw(Graphics2D g);
    public abstract boolean contains(Point point);
    public abstract void move(int dx, int dy);
    public abstract int isOnEndPoint(Point point);
    public abstract void resizeEndPoint(int endPointIndex, Point newPosition);

    @Override
    public abstract String toString();
}
