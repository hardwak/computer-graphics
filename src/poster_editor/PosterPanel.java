package poster_editor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class PosterPanel extends JPanel{
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    private final List<PosterElement> elements = new ArrayList<>();
    private PosterElement selectedElement = null;
    private int dragMode = 0;
    private Point2D lastDragPoint;

    public PosterPanel() {
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        setTransferHandler(new PosterTransferHandler());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                lastDragPoint = e.getPoint();

                if (e.getButton() == MouseEvent.BUTTON3) {
                    selectElementAt(e.getPoint());
                    if (selectedElement != null) {
                        elements.remove(selectedElement);
                        selectedElement = null;
                        repaint();
                    }
                    return;
                }


                for (PosterElement element : elements) {
                    if (element.isCornerHit(e.getPoint())) {
                        selectedElement = element;
                        dragMode = 2;
                        repaint();
                        return;
                    }

                    if (element.isCenterHit(e.getPoint())) {
                        selectedElement = element;
                        dragMode = 1;
                        repaint();
                        return;
                    }
                }

                selectElementAt(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragMode = 0;
                lastDragPoint = null;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                if (selectedElement != null && lastDragPoint != null) {
                    Point2D currentPoint = e.getPoint();

                    if (dragMode == 1) {
                        double dx = currentPoint.getX() - lastDragPoint.getX();
                        double dy = currentPoint.getY() - lastDragPoint.getY();
                        selectedElement.translateInScreenCoordinates(dx, dy);
                    } else if (dragMode == 2) {
                        selectedElement.handleCornerDrag(lastDragPoint, currentPoint);
                    }

                    lastDragPoint = currentPoint;
                    repaint();
                }
            }
        });
    }

    private void selectElementAt(Point2D point) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            PosterElement element = elements.get(i);
            if (element.contains(point)) {
                selectedElement = element;
                repaint();
                return;
            }
        }

        selectedElement = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (PosterElement element : elements) {
            element.draw(g2d);

            if (element == selectedElement) {
                element.drawSelectionHandles(g2d);
            }
        }
    }

    public void addElement(PosterElement element) {
        elements.add(element);
        selectedElement = element;
        repaint();
    }

    public void addTextElement(String text) {
        TextElement textElement = new TextElement(
                text,
                new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0)
        );

        addElement(textElement);
    }

    public void moveSelectedElement(int dx, int dy) {
        if (selectedElement != null) {
            selectedElement.translateInScreenCoordinates(dx, dy);
            repaint();
        }
    }

    public void rotateSelectedElement(int degrees) {
        if (selectedElement != null) {
            selectedElement.rotate(Math.toRadians(degrees));
            repaint();
        }
    }

    public void bringSelectedToFront() {
        if (selectedElement != null) {
            int index = elements.indexOf(selectedElement);
            if (index < elements.size() - 1) {
                elements.remove(selectedElement);
                elements.add(index + 1, selectedElement);
                repaint();
            }
        }
    }

    public void sendSelectedToBack() {
        if (selectedElement != null) {
            int index = elements.indexOf(selectedElement);
            if (index > 0) {
                elements.remove(selectedElement);
                elements.add(index - 1, selectedElement);
                repaint();
            }
        }
    }

    public void setSelectedElementColor(Color color) {
        if (selectedElement != null) {
            selectedElement.setColor(color);
            repaint();
        }
    }

    public void saveToStream(ObjectOutputStream out) throws IOException {
        out.writeObject(elements);
    }

    public void loadFromStream(ObjectInputStream in) throws IOException, ClassNotFoundException {
        elements.clear();
        elements.addAll((List<PosterElement>) in.readObject());
        selectedElement = null;
        repaint();
    }

    public void exportImage(File file) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        for (PosterElement element : elements) {
            element.draw(g2d);
        }

        g2d.dispose();

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error exporting image: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private class PosterTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            try {
                String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                Point dropPoint = support.getDropLocation().getDropPoint();

                if (data.startsWith("image:")) {
                    int imageIndex = Integer.parseInt(data.substring(6));
                    if (imageIndex >= 0 && imageIndex < ((PosterEditor) SwingUtilities.getWindowAncestor(PosterPanel.this)).loadedImages.size()) {
                        BufferedImage image = ((PosterEditor) SwingUtilities.getWindowAncestor(PosterPanel.this)).loadedImages.get(imageIndex);
                        addElement(new ImageElement(image, dropPoint));
                    }
                } else if (data.equals("rectangle")) {
                    addElement(new RectangleElement(dropPoint));
                } else if (data.equals("circle")) {
                    addElement(new CircleElement(dropPoint));
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
