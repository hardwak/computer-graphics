package my_paint;

import my_paint.Shapes.*;
import my_paint.Shapes.Shape;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MyPaint extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int CONTROL_PANEL_HEIGHT = 100;
    public static final int SELECTION_THRESHOLD = 12;

    private DrawingPanel drawingPanel;

    private JRadioButton lineButton;
    private JRadioButton rectangleButton;
    private JRadioButton circleButton;
    private JSpinner redField, greenField, blueField;

    public MyPaint() {
        setTitle("MyPaint");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, CONTROL_PANEL_HEIGHT));
        controlPanel.setLayout(new FlowLayout());

        JPanel selectorsPanel = new JPanel();

        ButtonGroup shapeGroup = new ButtonGroup();
        lineButton = new JRadioButton("Line");
        rectangleButton = new JRadioButton("Rectangle");
        circleButton = new JRadioButton("Circle");

        lineButton.setSelected(true);

        shapeGroup.add(lineButton);
        shapeGroup.add(rectangleButton);
        shapeGroup.add(circleButton);

        JLabel colorLabel = new JLabel("Color");
        redField = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        greenField = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        blueField = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));

        selectorsPanel.add(lineButton);
        selectorsPanel.add(rectangleButton);
        selectorsPanel.add(circleButton);
        selectorsPanel.add(colorLabel);
        selectorsPanel.add(redField);
        selectorsPanel.add(greenField);
        selectorsPanel.add(blueField);

        JPanel buttonsPanel = new JPanel();

        JButton saveVectorButton = new JButton("Save Vector");
        JButton loadVectorButton = new JButton("Load Vector");
        JButton saveRasterButton = new JButton("Save Raster");
        JButton resetButton = new JButton("Clear All");

        saveVectorButton.addActionListener(e -> saveVectorImage());
        loadVectorButton.addActionListener(e -> loadVectorImage());
        saveRasterButton.addActionListener(e -> saveRasterImage());
        resetButton.addActionListener(e -> drawingPanel.clearAll());

        buttonsPanel.add(saveVectorButton);
        buttonsPanel.add(loadVectorButton);
        buttonsPanel.add(saveRasterButton);
        buttonsPanel.add(resetButton);

        controlPanel.add(selectorsPanel);
        controlPanel.add(buttonsPanel);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void saveVectorImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                for (Shape shape : drawingPanel.shapes) {
                    writer.println(shape.toString());
                }
                JOptionPane.showMessageDialog(this, "Vector image saved successfully");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadVectorImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                drawingPanel.shapes.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("LINE:")) {
                        drawingPanel.shapes.add(LineShape.fromString(line));
                    } else if (line.startsWith("RECTANGLE:")) {
                        drawingPanel.shapes.add(RectangleShape.fromString(line));
                    } else if (line.startsWith("CIRCLE:")) {
                        drawingPanel.shapes.add(CircleShape.fromString(line));
                    }
                }
                drawingPanel.repaint();
                JOptionPane.showMessageDialog(this, "Vector image loaded successfully");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveRasterImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = new BufferedImage(
                        drawingPanel.getWidth(),
                        drawingPanel.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g2d = image.createGraphics();
                drawingPanel.paint(g2d);
                g2d.dispose();

                String filename = fileChooser.getSelectedFile().toString();
                if (!filename.toLowerCase().endsWith(".png")) {
                    filename += ".png";
                }

                ImageIO.write(image, "png", new File(filename));
                JOptionPane.showMessageDialog(this, "Raster image saved successfully");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving raster image: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DrawingPanel extends JPanel {
        private List<Shape> shapes = new ArrayList<>();
        private Shape currentShape = null;
        private Shape selectedShape = null;
        private Point startPoint = null;
        private boolean isDragging = false;
        private boolean isResizing = false;
        private int selectedEndPoint = -1;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();

                    //deleting
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        Shape shapeToDelete = findShapeAt(e.getPoint());
                        if (shapeToDelete != null) {
                            shapes.remove(shapeToDelete);
                            repaint();
                        }
                        return;
                    }

                    if (e.getButton() == MouseEvent.BUTTON1) {
                        //resizing
                        for (Shape shape : shapes) {
                            int endPointIndex = shape.isOnEndPoint(e.getPoint());
                            if (endPointIndex != -1) {
                                selectedShape = shape;
                                selectedEndPoint = endPointIndex;
                                isResizing = true;
                                return;
                            }
                        }

                        //dragging
                        selectedShape = findShapeAt(e.getPoint());
                        if (selectedShape != null) {
                            isDragging = true;
                            return;
                        }

                        Color color = getSelectedColor();
                        if (lineButton.isSelected()) {
                            currentShape = new LineShape(startPoint.x, startPoint.y, startPoint.x, startPoint.y, color);
                        } else if (rectangleButton.isSelected()) {
                            currentShape = new RectangleShape(startPoint.x, startPoint.y, 0, 0, color);
                        } else if (circleButton.isSelected()) {
                            currentShape = new CircleShape(startPoint.x, startPoint.y, 0, color);
                        }
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (currentShape != null) {
                        updateCurrentShape(e.getPoint());
                        repaint();
                    } else if (isDragging && selectedShape != null) {
                        selectedShape.move(e.getPoint().x - startPoint.x, e.getPoint().y - startPoint.y);
                        startPoint = e.getPoint();
                        repaint();
                    } else if (isResizing && selectedShape != null) {
                        selectedShape.resizeEndPoint(selectedEndPoint, e.getPoint());
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (currentShape != null) {
                        updateCurrentShape(e.getPoint());
                        shapes.add(currentShape);
                        currentShape = null;
                        repaint();
                    }

                    if (selectedShape != null && isResizing && selectedShape instanceof RectangleShape) {
                        ((RectangleShape) selectedShape).normalizeCoordinates();
                    }

                    selectedShape = null;
                    isDragging = false;
                    isResizing = false;
                    selectedEndPoint = -1;
                }

                private void updateCurrentShape(Point endPoint) {
                    if (currentShape instanceof LineShape) {
                        ((LineShape) currentShape).setEndPoint(endPoint.x, endPoint.y);
                    } else if (currentShape instanceof RectangleShape) {
                        int width = endPoint.x - startPoint.x;
                        int height = endPoint.y - startPoint.y;

                        ((RectangleShape) currentShape).setSize(width, height);
                    } else if (currentShape instanceof CircleShape) {
                        int dx = endPoint.x - startPoint.x;
                        int dy = endPoint.y - startPoint.y;
                        int radius = (int) Math.sqrt(dx * dx + dy * dy);

                        ((CircleShape) currentShape).setRadius(radius);
                    }
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        public void clearAll() {
            shapes.clear();
            repaint();
        }

        private Shape findShapeAt(Point point) {
            for (Shape shape : shapes) {
                if (shape.contains(point)) {
                    return shape;
                }
            }
            return null;
        }

        private Color getSelectedColor() {
            try {
                int r = (int) redField.getValue();
                int g = (int) greenField.getValue();
                int b = (int) blueField.getValue();

                return new Color(r, g, b);
            } catch (NumberFormatException e) {
                return Color.BLACK;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            for (Shape shape : shapes) {
                shape.draw(g2d);
            }

            if (currentShape != null) {
                currentShape.draw(g2d);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyPaint::new);
    }
}