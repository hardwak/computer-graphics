package poster_editor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;

abstract class PosterElement implements Serializable {
    protected AffineTransform transform;
    protected Color color;
    protected static final int HANDLE_SIZE = 8;

    public PosterElement() {
        transform = new AffineTransform();
        color = Color.BLACK;
    }

    public abstract void draw(Graphics2D g2d);

    public abstract boolean contains(Point2D point);

    public void translate(double dx, double dy) {
        transform.translate(dx, dy);
    }

    public void translateInScreenCoordinates(double dx, double dy) {
        AffineTransform vectorTransform = new AffineTransform(transform);
        double[] matrix = new double[4];
        vectorTransform.getMatrix(matrix);
        vectorTransform.setTransform(matrix[0], matrix[1], matrix[2], matrix[3], 0, 0);

        try {
            AffineTransform inverseTransform = vectorTransform.createInverse();
            Point2D.Double vector = new Point2D.Double(dx, dy);

            inverseTransform.transform(vector, vector);

            translate(vector.x, vector.y);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    public void rotate(double angle) {
        Point2D center = getCenter();

        AffineTransform rotateTransform = new AffineTransform();
        rotateTransform.translate(center.getX(), center.getY());
        rotateTransform.rotate(angle);
        rotateTransform.translate(-center.getX(), -center.getY());

        transform.preConcatenate(rotateTransform);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isCenterHit(Point2D point) {
        Point2D center = getCenter();
        return center.distance(point) <= (double) HANDLE_SIZE / 2;
    }

    public boolean isCornerHit(Point2D point) {
        Point2D corner = getCorner();
        return corner.distance(point) <= (double) HANDLE_SIZE / 2;
    }

    public void handleCornerDrag(Point2D lastPoint, Point2D newPoint) {
        Point2D center = getCenter();

        double lastAngle = Math.atan2(
                lastPoint.getY() - center.getY(),
                lastPoint.getX() - center.getX()
        );

        double newAngle = Math.atan2(
                newPoint.getY() - center.getY(),
                newPoint.getX() - center.getX()
        );

        double rotationAngle = newAngle - lastAngle;

        double lastDist = center.distance(lastPoint);
        double newDist = center.distance(newPoint);
        double scale = newDist / lastDist;

        AffineTransform modifyTransform = new AffineTransform();
        modifyTransform.translate(center.getX(), center.getY());
        modifyTransform.rotate(rotationAngle);
        modifyTransform.scale(scale, scale);
        modifyTransform.translate(-center.getX(), -center.getY());

        transform.preConcatenate(modifyTransform);
    }

    public void drawSelectionHandles(Graphics2D g2d) {
        Point2D center = getCenter();
        g2d.setColor(new Color(56, 255, 208));
        g2d.fillRect(
                (int) (center.getX() - HANDLE_SIZE / 2),
                (int) (center.getY() - HANDLE_SIZE / 2),
                HANDLE_SIZE, HANDLE_SIZE
        );

        Point2D corner = getCorner();
        g2d.setColor(new Color(56, 255, 208));
        g2d.fillRect(
                (int) (corner.getX() - HANDLE_SIZE / 2),
                (int) (corner.getY() - HANDLE_SIZE / 2),
                HANDLE_SIZE, HANDLE_SIZE
        );
    }

    public abstract Point2D getCenter();

    public abstract Point2D getCorner();
}
