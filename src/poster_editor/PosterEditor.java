package poster_editor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class PosterEditor extends JFrame {
    private final JPanel thumbnailPanel;
    private final JPanel shapesPanel;
    private final PosterPanel posterPanel;
    private final JPanel controlPanel;
    final List<BufferedImage> loadedImages = new ArrayList<>();

    public PosterEditor() {
        super("Poster Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));

        thumbnailPanel = new JPanel();
        thumbnailPanel.setLayout(new WrapLayout());
        thumbnailPanel.setBorder(BorderFactory.createTitledBorder("Images"));
        JScrollPane thumbnailScrollPane = new JScrollPane(thumbnailPanel);

        shapesPanel = new JPanel();
        shapesPanel.setLayout(new GridLayout(2, 2, 10, 10));
        shapesPanel.setBorder(BorderFactory.createTitledBorder("Shapes"));

        addShapesToGallery();

        leftPanel.add(thumbnailScrollPane);
        leftPanel.add(shapesPanel);

        posterPanel = new PosterPanel();
        JScrollPane posterScrollPane = new JScrollPane(posterPanel);

        controlPanel = new JPanel();
        setupControlPanel();

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(posterScrollPane);

        add(mainSplitPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        loadImagesFromDirectory();

        setVisible(true);
    }

    private void setupControlPanel() {
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        JButton colorPickerBtn = new JButton("Change Color");

        JButton moveLeftBtn = new JButton("←");
        JButton moveRightBtn = new JButton("→");
        JButton moveUpBtn = new JButton("↑");
        JButton moveDownBtn = new JButton("↓");

        JButton rotateLeftBtn = new JButton("⟲");
        JButton rotateRightBtn = new JButton("⟳");

        JButton bringToFrontBtn = new JButton("Bring to Front");
        JButton sendToBackBtn = new JButton("Send to Back");

        JButton saveBtn = new JButton("Save Poster");
        JButton loadBtn = new JButton("Load Poster");
        JButton exportBtn = new JButton("Export Image");

        colorPickerBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose a color", Color.RED);
            if (newColor != null) {
                posterPanel.setSelectedElementColor(newColor);
            }
        });

        moveLeftBtn.addActionListener(e -> posterPanel.moveSelectedElement(-1, 0));
        moveRightBtn.addActionListener(e -> posterPanel.moveSelectedElement(1, 0));
        moveUpBtn.addActionListener(e -> posterPanel.moveSelectedElement(0, -1));
        moveDownBtn.addActionListener(e -> posterPanel.moveSelectedElement(0, 1));

        rotateLeftBtn.addActionListener(e -> posterPanel.rotateSelectedElement(-1));
        rotateRightBtn.addActionListener(e -> posterPanel.rotateSelectedElement(1));

        bringToFrontBtn.addActionListener(e -> posterPanel.bringSelectedToFront());
        sendToBackBtn.addActionListener(e -> posterPanel.sendSelectedToBack());

        saveBtn.addActionListener(e -> saveToFile());
        loadBtn.addActionListener(e -> loadFromFile());
        exportBtn.addActionListener(e -> exportImage());

        controlPanel.add(colorPickerBtn);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(moveLeftBtn);
        controlPanel.add(moveRightBtn);
        controlPanel.add(moveUpBtn);
        controlPanel.add(moveDownBtn);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(rotateLeftBtn);
        controlPanel.add(rotateRightBtn);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(bringToFrontBtn);
        controlPanel.add(sendToBackBtn);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(saveBtn);
        controlPanel.add(loadBtn);
        controlPanel.add(exportBtn);
    }

    private void addShapesToGallery() {
        JPanel rectanglePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLACK);
                g2d.fillRect(10, 10, getWidth() - 20, getHeight() - 20);
            }
        };
        rectanglePanel.setPreferredSize(new Dimension(80, 80));

        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLACK);
                g2d.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
            }
        };
        circlePanel.setPreferredSize(new Dimension(80, 80));

        JButton addTextBtn = new JButton("Add Text");
        addTextBtn.addActionListener(e -> {
            String text = JOptionPane.showInputDialog(this, "Enter text:");
            if (text != null && !text.isEmpty()) {
                posterPanel.addTextElement(text);
            }
        });

        setupDragAndDrop(rectanglePanel, "rectangle");
        setupDragAndDrop(circlePanel, "circle");

        shapesPanel.add(rectanglePanel);
        shapesPanel.add(circlePanel);
        shapesPanel.add(addTextBtn);
    }

    private void loadImagesFromDirectory() {
        try {
            Path dir = Paths.get("images");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                return;
            }

            Files.list(dir)
                    .filter(path -> {
                        String name = path.toString().toLowerCase();
                        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
                    })
                    .forEach(this::addImageThumbnail);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading images: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addImageThumbnail(Path imagePath) {
        try {
            BufferedImage originalImage = ImageIO.read(imagePath.toFile());
            int thumbWidth = 80;
            int thumbHeight = 80;
            Image thumbnail = originalImage.getScaledInstance(
                    thumbWidth, thumbHeight, Image.SCALE_SMOOTH);

            JPanel thumbPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(thumbnail, 0, 0, this);
                }
            };
            thumbPanel.setPreferredSize(new Dimension(thumbWidth, thumbHeight));
            thumbPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            loadedImages.add(originalImage);
            final int imageIndex = loadedImages.size() - 1;
            setupImageDragAndDrop(thumbPanel, imageIndex);

            thumbnailPanel.add(thumbPanel);
            thumbnailPanel.repaint();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupDragAndDrop(JPanel sourcePanel, String shapeType) {
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JPanel panel = (JPanel) e.getSource();
                TransferHandler handler = panel.getTransferHandler();
                handler.exportAsDrag(panel, e, TransferHandler.COPY);
            }
        };

        sourcePanel.addMouseListener(dragAdapter);

        sourcePanel.setTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection(shapeType);
            }
        });
    }

    private void setupImageDragAndDrop(JPanel thumbPanel, int imageIndex) {
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JPanel panel = (JPanel) e.getSource();
                TransferHandler handler = panel.getTransferHandler();
                handler.exportAsDrag(panel, e, TransferHandler.COPY);
            }
        };

        thumbPanel.addMouseListener(dragAdapter);

        thumbPanel.setTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection("image:" + imageIndex);
            }
        });
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Poster Data Files (*.pstr)", "pstr"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pstr"))
                path += ".pstr";
            file = new File(path);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                posterPanel.saveToStream(oos);
                JOptionPane.showMessageDialog(this, "Poster saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error saving file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Poster Data Files (*.pstr)", "pstr"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                posterPanel.loadFromStream(ois);
                JOptionPane.showMessageDialog(this, "Poster loaded successfully!");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error loading file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images (*.png)", "png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".png")) {
                path += ".png";
                file = new File(path);
            }

            try {
                posterPanel.exportImage(file);
                JOptionPane.showMessageDialog(this, "Image exported successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error while saving poster: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PosterEditor::new);
    }
}

