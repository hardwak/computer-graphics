package poster_editor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

class RectangleElement extends PosterElement {
    private final int width = 100;
    private final int height = 80;

    public RectangleElement(Point2D position) {
        super();
        color = Color.BLACK;

        transform.translate(position.getX() - width / 2.0, position.getY() - height / 2.0);
    }

    @Override
    public void draw(Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.transform(transform);

        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);

        g2d.setTransform(originalTransform);
    }

    @Override
    public boolean contains(Point2D point) {
        try {
            AffineTransform inverseTransform = transform.createInverse();
            Point2D transformedPoint = inverseTransform.transform(point, null);

            return transformedPoint.getX() >= 0 && transformedPoint.getX() < width &&
                    transformedPoint.getY() >= 0 && transformedPoint.getY() < height;
        } catch (NoninvertibleTransformException e) {
            return false;
        }
    }

    @Override
    public Point2D getCenter() {
        Point2D.Double center = new Point2D.Double(width / 2.0, height / 2.0);
        return transform.transform(center, null);
    }

    @Override
    public Point2D getCorner() {
        Point2D.Double corner = new Point2D.Double(width, 0);
        return transform.transform(corner, null);
    }
}
