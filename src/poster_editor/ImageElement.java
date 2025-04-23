package poster_editor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;

class ImageElement extends PosterElement {
    private transient BufferedImage image;
    private final int width;
    private final int height;
    private byte[] imageData;

    public ImageElement(BufferedImage image, Point2D position) {
        super();
        this.image = image;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);

        transform.translate(position.getX() - width / 2.0, position.getY() - height / 2.0);

        storeImageData();
    }

    private void storeImageData() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            imageData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (imageData != null) {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            image = ImageIO.read(bais);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.transform(transform);
        g2d.drawImage(image, 0, 0, null);
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
        Point2D.Double corner = new Point2D.Double(width, height);
        return transform.transform(corner, null);
    }
}
