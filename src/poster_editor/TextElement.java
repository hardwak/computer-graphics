package poster_editor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class TextElement extends PosterElement {
    private final String text;
    private final Font font;
    private final Rectangle2D textBounds;

    public TextElement(String text, Point2D position) {
        super();
        this.text = text;
        this.font = new Font("Arial", Font.PLAIN, 24);

        FontMetrics metrics = new JPanel().getFontMetrics(font);
        this.textBounds = metrics.getStringBounds(text, null);

        transform.translate(
                position.getX() - textBounds.getWidth() / 2.0,
                position.getY() - textBounds.getHeight() / 2.0
        );
    }

    @Override
    public void draw(Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.transform(transform);

        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(text, 0, (int) -textBounds.getY());

        g2d.setTransform(originalTransform);
    }

    @Override
    public boolean contains(Point2D point) {
        try {
            AffineTransform inverseTransform = transform.createInverse();
            Point2D transformedPoint = inverseTransform.transform(point, null);

            transformedPoint = new Point2D.Double(
                    transformedPoint.getX(),
                    transformedPoint.getY() + textBounds.getY()
            );

            return textBounds.contains(transformedPoint);
        } catch (NoninvertibleTransformException e) {
            return false;
        }
    }

    @Override
    public Point2D getCenter() {
        Point2D.Double center = new Point2D.Double(
                textBounds.getWidth() / 2.0,
                textBounds.getHeight() / 2.0
        );
        return transform.transform(center, null);
    }

    @Override
    public Point2D getCorner() {
        Point2D.Double corner = new Point2D.Double(textBounds.getWidth(), 0);
        return transform.transform(corner, null);
    }
}
