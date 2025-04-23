package poster_editor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

class CircleElement extends PosterElement {
    private final int radius = 50;

    public CircleElement(Point2D position) {
        super();
        color = Color.BLACK;

        transform.translate(position.getX() - radius, position.getY() - radius);
    }

    @Override
    public void draw(Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.transform(transform);

        g2d.setColor(color);
        g2d.fillOval(0, 0, 2 * radius, 2 * radius);

        g2d.setTransform(originalTransform);
    }

    @Override
    public boolean contains(Point2D point) {
        try {
            AffineTransform inverseTransform = transform.createInverse();
            Point2D transformedPoint = inverseTransform.transform(point, null);

            double distanceSquared = Math.pow(transformedPoint.getX() - radius, 2) +
                    Math.pow(transformedPoint.getY() - radius, 2);

            return distanceSquared <= radius * radius;
        } catch (NoninvertibleTransformException e) {
            return false;
        }
    }

    @Override
    public Point2D getCenter() {
        Point2D.Double center = new Point2D.Double(radius, radius);
        return transform.transform(center, null);
    }

    @Override
    public Point2D getCorner() {
        Point2D.Double corner = new Point2D.Double(2 * radius, radius);
        return transform.transform(corner, null);
    }
}
